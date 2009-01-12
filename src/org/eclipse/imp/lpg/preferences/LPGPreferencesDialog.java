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

package org.eclipse.imp.lpg.preferences;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.lpg.LPGRuntimePlugin; // SMS 27 Mar 2007

/**
 * The IMP-based tabbed preferences page for language LPG.
 * 
 * Naming conventions:  This template uses the language name as a prefix
 * for naming the language plugin class and the preference-tab classes.
 * 	
 */
public class LPGPreferencesDialog extends TabbedPreferencesPage {

	public LPGPreferencesDialog() {
		super();
		// Get the language-specific preferences service
		// SMS 28 Mar 2007:  parameterized full name of plugin class
		prefService = LPGRuntimePlugin.getInstance().getPreferencesService();
	}

	protected PreferencesTab[] createTabs(IPreferencesService prefService,
			TabbedPreferencesPage page, TabFolder tabFolder) {
		PreferencesTab[] tabs = new PreferencesTab[4];

		LPGPreferencesDialogProjectTab projectTab = new LPGPreferencesDialogProjectTab(
				prefService);
		projectTab.createTabContents(page, tabFolder);
		tabs[0] = projectTab;

		LPGPreferencesDialogInstanceTab instanceTab = new LPGPreferencesDialogInstanceTab(
				prefService);
		instanceTab.createTabContents(page, tabFolder);
		tabs[1] = instanceTab;

		LPGPreferencesDialogConfigurationTab configurationTab = new LPGPreferencesDialogConfigurationTab(
				prefService);
		configurationTab.createTabContents(page, tabFolder);
		tabs[2] = configurationTab;

		LPGPreferencesDialogDefaultTab defaultTab = new LPGPreferencesDialogDefaultTab(
				prefService);
		defaultTab.createTabContents(page, tabFolder);
		tabs[3] = defaultTab;

		return tabs;
	}

}
