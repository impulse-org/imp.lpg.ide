package org.eclipse.safari.jikespg.refactoring;

import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.uide.editor.UniversalEditor;
import org.eclipse.uide.refactoring.RefactoringStarter;

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
