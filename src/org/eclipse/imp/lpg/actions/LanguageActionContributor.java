/*
 * Created on Nov 12, 2006
 */
package org.eclipse.safari.jikespg.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.uide.editor.UniversalEditor;
import org.eclipse.uide.editor.UniversalEditor.ILanguageActionsContributor;

public class LanguageActionContributor implements ILanguageActionsContributor {
    public LanguageActionContributor() {
	super();
    }

    public IAction[] getEditorActions(UniversalEditor editor) {
	return new IAction[] {
		new GenerateSentenceAction(editor),
		new ParseNonTerminalAction(editor),
		new ShowFirstSetAction(editor),
		new ShowFollowSetAction(editor)
		};
    }

    public static ResourceBundle ResBundle= ResourceBundle.getBundle("org.eclipse.safari.jikespg.actions.ActionMessages");
}
