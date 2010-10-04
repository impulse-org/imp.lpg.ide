/******************************************/
/* WARNING: GENERATED FILE - DO NOT EDIT! */
/******************************************/
package org.eclipse.imp.lpg.preferences;

import org.eclipse.swt.widgets.TabFolder;import org.eclipse.imp.preferences.IPreferencesService;import org.eclipse.imp.preferences.PreferencesInitializer;import org.eclipse.imp.preferences.PreferencesTab;import org.eclipse.imp.preferences.TabbedPreferencesPage;import org.eclipse.imp.lpg.LPGRuntimePlugin;

/**
 * A preference page class.
 */
public class LPGPreferencePage extends TabbedPreferencesPage {
	public LPGPreferencePage() {
		super();
		prefService = LPGRuntimePlugin.getInstance().getPreferencesService();
	}

	protected PreferencesTab[] createTabs(IPreferencesService prefService,
		TabbedPreferencesPage page, TabFolder tabFolder) {
		PreferencesTab[] tabs = new PreferencesTab[4];

		LPGDefaultTab defaultTab = new LPGDefaultTab(prefService);
		defaultTab.createTabContents(page, tabFolder);
		tabs[0] = defaultTab;

		LPGConfigurationTab configurationTab = new LPGConfigurationTab(prefService);
		configurationTab.createTabContents(page, tabFolder);
		tabs[1] = configurationTab;

		LPGInstanceTab instanceTab = new LPGInstanceTab(prefService);
		instanceTab.createTabContents(page, tabFolder);
		tabs[2] = instanceTab;

		LPGProjectTab projectTab = new LPGProjectTab(prefService);
		projectTab.createTabContents(page, tabFolder);
		tabs[3] = projectTab;

		return tabs;
	}

	public PreferencesInitializer getPreferenceInitializer() {
		return new LPGInitializer();
	}
}
