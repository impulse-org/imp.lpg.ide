/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Nov 12, 2006
 */
package org.eclipse.imp.lpg.actions;

import java.util.ResourceBundle;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.ILanguageActionsContributor;
import org.eclipse.jface.action.IAction;

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

    public static ResourceBundle ResBundle= ResourceBundle.getBundle("org.eclipse.imp.lpg.actions.ActionMessages");
}
