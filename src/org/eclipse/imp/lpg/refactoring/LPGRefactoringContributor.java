package org.eclipse.imp.lpg.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.editor.UniversalEditor.IRefactoringContributor;
import org.eclipse.jface.action.IAction;

public class LPGRefactoringContributor implements IRefactoringContributor {
    public LPGRefactoringContributor() { }

    public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
	return new IAction[] {
		new MakeNonEmptyRefactoringAction(editor),
		new MakeEmptyRefactoringAction(editor),
		new MakeLeftRecursiveRefactoringAction(editor),
		new InlineNonTerminalRefactoringAction(editor)
	};
    }
}
