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

public class MakeEmptyRefactoring extends Refactoring {
    private final IFile fGrammarFile;
    private final ASTNode fNode;

    public MakeEmptyRefactoring(UniversalEditor editor) {
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
	ISourcePositionLocator locator= parseController.getNodeLocator();

	return (ASTNode) locator.findNode(root, sel.x);
    }

    public String getName() {
	return "Make Empty";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	if (!(fNode instanceof nonTerm))
	    return RefactoringStatus.createFatalErrorStatus("Make Empty is only valid for non-terminals");

	nonTerm nt= (nonTerm) fNode;
	ruleList rhSides= nt.getruleList();

	if (rhSides.size() != 2)
	    return RefactoringStatus.createFatalErrorStatus("Make Empty is only valid for non-terminals with 2 productions");

	rule rhs1= (rule) rhSides.getElementAt(0);
	rule rhs2= (rule) rhSides.getElementAt(1);

	symWithAttrsList rhs1Syms= rhs1.getsymWithAttrsList();
	symWithAttrsList rhs2Syms= rhs2.getsymWithAttrsList();

	// swap rhs1/rhs2 as needed to ensure that rhs1 is the "leaf case"
	if (rhs1Syms.size() == rhs2Syms.size() + 1) {
	    rule tmp= rhs1;
	    symWithAttrsList tmpList= rhs1Syms;

	    rhs1= rhs2;
	    rhs2= tmp;
	    rhs1Syms= rhs2Syms;
	    rhs2Syms= tmpList;
	}

	for(int i= 0; i < rhs1Syms.size(); i++) { // make sure the productions are of the form lhs ::= b c ... | a b c ...
	    if (!rhs1Syms.getElementAt(i).toString().equals(rhs2Syms.getElementAt(i+1).toString()))
		return RefactoringStatus.createFatalErrorStatus("Non-terminal must have the form 'a ::= b c ... | a b c ...'");
	}
	if (!rhs2Syms.getElementAt(0).toString().equals(nt.getruleNameWithAttributes().getSYMBOL().toString()))
	    return RefactoringStatus.createFatalErrorStatus("Non-terminal must have the form 'a ::= b c ... | a b c ...'");

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
	int N= rhs1Syms.size();

	if (rhs1Syms.size() == rhs2Syms.size() + 1) {
	    rule tmp= rhs1;
	    symWithAttrsList tmpList= rhs1Syms;

	    rhs1= rhs2;
	    rhs2= tmp;
	    rhs1Syms= rhs2Syms;
	    rhs2Syms= tmpList;
	}

	int startOffset= rhs1Syms.getElementAt(0).getLeftIToken().getStartOffset();
	int endOffset= rhs1Syms.getElementAt(N-1).getLeftIToken().getEndOffset();

	TextFileChange tfc= new TextFileChange("Make Empty", fGrammarFile);

	tfc.setEdit(new MultiTextEdit());
	tfc.addEdit(new ReplaceEdit(startOffset, endOffset - startOffset + 1, "$empty"));
	return tfc;
    }
}
