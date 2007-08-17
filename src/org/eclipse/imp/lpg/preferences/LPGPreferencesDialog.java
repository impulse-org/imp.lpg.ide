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
		prefService = LPGRuntimePlugin.getPreferencesService();
	}

	protected PreferencesTab[] createTabs(IPreferencesService prefService,
			TabbedPreferencesPage page, TabFolder tabFolder) {
		PreferencesTab[] tabs = new PreferencesTab[4];

		LPGPreferencesDialogProjectTab projectTab = new LPGPreferencesDialogProjectTab(
				prefService);
		projectTab.createProjectPreferencesTab(page, tabFolder);
		tabs[0] = projectTab;

		LPGPreferencesDialogInstanceTab instanceTab = new LPGPreferencesDialogInstanceTab(
				prefService);
		instanceTab.createInstancePreferencesTab(page, tabFolder);
		tabs[1] = instanceTab;

		LPGPreferencesDialogConfigurationTab configurationTab = new LPGPreferencesDialogConfigurationTab(
				prefService);
		configurationTab.createConfigurationPreferencesTab(page, tabFolder);
		tabs[2] = configurationTab;

		LPGPreferencesDialogDefaultTab defaultTab = new LPGPreferencesDialogDefaultTab(
				prefService);
		defaultTab.createDefaultPreferencesTab(page, tabFolder);
		tabs[3] = defaultTab;

		return tabs;
	}

}
