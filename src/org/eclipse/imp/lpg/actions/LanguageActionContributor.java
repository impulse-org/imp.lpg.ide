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
