/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ui.texteditor.TextEditorAction;

public class InlineNonTerminalRefactoringAction extends TextEditorAction {
//    private final UniversalEditor fEditor;

    public InlineNonTerminalRefactoringAction(UniversalEditor editor) {
	super(RefactoringResources.ResBundle, "inlineNonTerminal.", editor);
//	fEditor= editor;
    }

    public void run() {
	final InlineNonTerminalRefactoring refactoring= new InlineNonTerminalRefactoring((UniversalEditor) this.getTextEditor());

	if (refactoring != null)
		new RefactoringStarter().activate(refactoring, new InlineNonTerminalWizard(refactoring, "Inline Non-Terminal"), this.getTextEditor().getSite().getShell(), "Inline Non-Terminal", false);
    }
}
