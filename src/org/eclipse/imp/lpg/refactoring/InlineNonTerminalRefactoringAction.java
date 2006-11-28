package org.eclipse.safari.jikespg.refactoring;

import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.uide.editor.UniversalEditor;
import org.eclipse.uide.refactoring.RefactoringStarter;

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
