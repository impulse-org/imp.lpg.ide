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

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class MakeNonEmptyWizard extends RefactoringWizard {
    public MakeNonEmptyWizard(MakeNonEmptyRefactoring refactoring, String pageTitle) {
	super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
	setDefaultPageTitle(pageTitle);
    }

    protected void addUserInputPages() {
	MakeNonEmptyInputPage page= new MakeNonEmptyInputPage("Make Non-Empty");

	addPage(page);
    }

    public MakeNonEmptyRefactoring getMakeNonEmptyRefactoring() {
	return (MakeNonEmptyRefactoring) getRefactoring();
    }
}
