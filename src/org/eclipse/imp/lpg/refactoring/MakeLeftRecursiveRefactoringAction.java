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

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ui.texteditor.TextEditorAction;

public class MakeLeftRecursiveRefactoringAction extends TextEditorAction {
//    private final UniversalEditor fEditor;

    public MakeLeftRecursiveRefactoringAction(UniversalEditor editor) {
	super(RefactoringResources.ResBundle, "makeLeftRecursive.", editor);
//	fEditor= editor;
    }

    public void run() {
	final MakeLeftRecursiveRefactoring refactoring= new MakeLeftRecursiveRefactoring((UniversalEditor) this.getTextEditor());

	if (refactoring != null)
		new RefactoringStarter().activate(refactoring, new MakeLeftRecursiveWizard(refactoring, "Make LeftRecursive"), this.getTextEditor().getSite().getShell(), "Make LeftRecursive", false);
    }
}
