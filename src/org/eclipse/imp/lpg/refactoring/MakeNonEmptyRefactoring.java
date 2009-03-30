/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.lpg.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.ruleList;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrsList;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public class MakeNonEmptyRefactoring extends Refactoring {
    private final IFile fGrammarFile;
    private final ASTNode fNode;

    public MakeNonEmptyRefactoring(UniversalEditor editor) {
	super();

	IEditorInput input= editor.getEditorInput();

	if (input instanceof IFileEditorInput) {
	    IFileEditorInput fileInput= (IFileEditorInput) input;

	    fGrammarFile= fileInput.getFile();
	    fNode= findNode(editor);
	} else {
	    fGrammarFile= null;
	    fNode= null;
	}
    }

    private ASTNode findNode(UniversalEditor editor) {
	Point sel= editor.getSelection();
	IParseController parseController= editor.getParseController();
	ASTNode root= (ASTNode) parseController.getCurrentAst();
	ISourcePositionLocator locator= parseController.getSourcePositionLocator();

	return (ASTNode) locator.findNode(root, sel.x);
    }

    public String getName() {
	return "Make Non-Empty";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	if (!(fNode instanceof nonTerm))
	    return RefactoringStatus.createFatalErrorStatus("Make Non-Empty is only valid for non-terminals");

	nonTerm nt= (nonTerm) fNode;
	ruleList rhSides= nt.getruleList();

	if (rhSides.size() != 2)
	    return RefactoringStatus.createFatalErrorStatus("Make Non-Empty is only valid for non-terminals with 2 productions");

	rule rhs1= (rule) rhSides.getElementAt(0);
	rule rhs2= (rule) rhSides.getElementAt(1);

	symWithAttrsList rhs1Syms= rhs1.getsymWithAttrsList();
	symWithAttrsList rhs2Syms= rhs2.getsymWithAttrsList();

	if (rhs2Syms.size() == 1 && rhs2Syms.getElementAt(0).toString().equals("$empty")) {
	    rule tmp= rhs1;
	    symWithAttrsList tmpList= rhs1Syms;

	    rhs1= rhs2;
	    rhs2= tmp;
	    rhs1Syms= rhs2Syms;
	    rhs2Syms= tmpList;
	} else if (rhs1Syms.size() != 1 || !rhs1Syms.getElementAt(0).toString().equals("$empty")) {
	    return RefactoringStatus.createFatalErrorStatus("Non-terminal must have the form 'a ::= $empty | a b'");
	}

	if (!rhs2Syms.getElementAt(0).toString().equals(nt.getruleNameWithAttributes().getSYMBOL().toString()))
	    return RefactoringStatus.createFatalErrorStatus("Non-terminal must have the form 'a ::= $empty | a b'");

	return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	// TODO Replace hand-written transform code with usage of SAFARI AST rewriter.
	nonTerm nt= (nonTerm) fNode;
	ruleList rhSides= nt.getruleList();
	rule rhs1= (rule) rhSides.getElementAt(0);
	rule rhs2= (rule) rhSides.getElementAt(1);

	symWithAttrsList rhs1Syms= rhs1.getsymWithAttrsList();
	symWithAttrsList rhs2Syms= rhs2.getsymWithAttrsList();
	int N= rhs2Syms.size();

	if (rhs2Syms.size() == 1) {
	    rule tmp= rhs1;
	    symWithAttrsList tmpList= rhs1Syms;

	    rhs1= rhs2;
	    rhs2= tmp;
	    rhs1Syms= rhs2Syms;
	    rhs2Syms= tmpList;
	}

	ASTNode symToReplace= rhs1Syms.getElementAt(0);
	StringBuffer buff= new StringBuffer();

	for(int i=1; i < N; i++) {
	    if (i > 1) buff.append(' ');
	    buff.append(rhs2Syms.getElementAt(i));
	}

	TextFileChange tfc= new TextFileChange("Make Non-Empty", fGrammarFile);

	tfc.setEdit(new MultiTextEdit());
	tfc.addEdit(new ReplaceEdit(symToReplace.getLeftIToken().getStartOffset(), symToReplace.getRightIToken().getEndOffset() - symToReplace.getLeftIToken().getStartOffset() + 1, buff.toString()));
	return tfc;
    }
}
