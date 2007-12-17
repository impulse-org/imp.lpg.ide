/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
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
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public class MakeLeftRecursiveRefactoring extends Refactoring {
    private final IFile fGrammarFile;
    private final ASTNode fNode;

    public MakeLeftRecursiveRefactoring(UniversalEditor editor) {
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
	return "Make Left Recursive";
    }

    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	if (!(fNode instanceof nonTerm) && !(fNode instanceof rule))
	    return RefactoringStatus.createFatalErrorStatus("Make Left Recursive is only valid for non-terminals and recursive productions");

	if (fNode instanceof nonTerm) {
	    nonTerm nt= (nonTerm) fNode;
	    ruleList rhSides= nt.getruleList();

	    for(int i=0; i < rhSides.size(); i++)
		checkProduction((rule) rhSides.getElementAt(i), nt);
	} else {
	    rule prod= (rule) fNode;
	    checkProduction(prod, (nonTerm) prod.getParent());
	}

	return new RefactoringStatus();
    }

    private RefactoringStatus checkProduction(rule prod, nonTerm nt) {
	symWithAttrsList rhsSyms= prod.getsymWithAttrsList();
	final int N= rhsSyms.size();

	for(int i= 0; i < N; i++) { // make sure the production is of the form a ::= b c ... a
	    if (rhsSyms.getElementAt(i).toString().equals(nt.getruleNameWithAttributes().getSYMBOL().toString()))
		return RefactoringStatus.createFatalErrorStatus("Non-terminal must have the form 'a ::= b c ... a'");
	}
	if (!rhsSyms.getElementAt(N-1).toString().equals(nt.getruleNameWithAttributes().getSYMBOL().toString()))
	    return RefactoringStatus.createFatalErrorStatus("Non-terminal must have the form 'a ::= b c ... a'");
	return new RefactoringStatus();
    }

    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	return new RefactoringStatus();
    }

    private void rewriteProduction(rule prod, nonTerm nt, TextFileChange tfc) {
	// TODO Replace hand-written transform code with usage of SAFARI AST rewriter.
	String ntSym= nt.getruleNameWithAttributes().getSYMBOL().toString();
	symWithAttrsList syms= prod.getsymWithAttrsList();
	int N= syms.size();
	ASTNode symToDelete= syms.getElementAt(N-1);
	int deleteStart= symToDelete.getLeftIToken().getStartOffset();
	int deleteEnd= symToDelete.getRightIToken().getEndOffset();

	if (!symToDelete.getLeftIToken().toString().equals(ntSym.toString()))
	    return;
	tfc.addEdit(new DeleteEdit(deleteStart, deleteEnd - deleteStart + 1));
	tfc.addEdit(new InsertEdit(syms.getElementAt(0).getLeftIToken().getStartOffset(), ntSym + " "));
    }

    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
	TextFileChange tfc= new TextFileChange("Make Left Recursive", fGrammarFile);
	nonTerm nt;

	tfc.setEdit(new MultiTextEdit());

	if (fNode instanceof nonTerm) {
	    nt= (nonTerm) fNode;
	    ruleList rhSides= nt.getruleList();

	    for(int i=0; i < rhSides.size(); i++)
		rewriteProduction((rule) rhSides.getElementAt(i), nt, tfc);
	} else {
	    rule prod= (rule) fNode;
	    nt= (nonTerm) prod.getParent();
	    rewriteProduction(prod, nt, tfc);
	}

	return tfc;
    }
}
