/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ui.texteditor.TextEditorAction;

public class MakeNonEmptyRefactoringAction extends TextEditorAction {
//    private final UniversalEditor fEditor;

    public MakeNonEmptyRefactoringAction(UniversalEditor editor) {
	super(RefactoringResources.ResBundle, "makeNonEmpty.", editor);
//	fEditor= editor;
    }

    public void run() {
	final MakeNonEmptyRefactoring refactoring= new MakeNonEmptyRefactoring((UniversalEditor) this.getTextEditor());

	if (refactoring != null)
		new RefactoringStarter().activate(refactoring, new MakeNonEmptyWizard(refactoring, "Make Non-Empty"), this.getTextEditor().getSite().getShell(), "Make Non-Empty", false);
    }
}
