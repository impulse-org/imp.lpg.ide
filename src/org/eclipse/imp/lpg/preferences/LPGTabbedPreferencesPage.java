package org.eclipse.imp.lpg.preferences;


import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.imp.preferences.ISafariPreferencesService;
import org.eclipse.imp.preferences.SafariPreferencesTab;
import org.eclipse.imp.preferences.SafariTabbedPreferencesPage;

/**
 * The Safari-based tabbed preferences page for LPG	
 */
public class LTabbedPreferencesPage extends SafariTabbedPreferencesPage {
	
	public LTabbedPreferencesPage() {
		super();
		// Get the language-specific preferences service in the
		// language-specific tab
		prefService = LPGRuntimePlugin.getPreferencesService();	//$ plugin name (based on language name or ?)
	}
	
	
	protected SafariPreferencesTab[] createTabs(
			ISafariPreferencesService prefService, SafariTabbedPreferencesPage page, TabFolder tabFolder) 
	{
		SafariPreferencesTab[] tabs = new SafariPreferencesTab[4];
		
		LProjectPreferencesTab projectTab = new LProjectPreferencesTab(prefService);
		projectTab.createProjectPreferencesTab(page, tabFolder);
		tabs[0] = projectTab;

		LInstancePreferencesTab instanceTab = new LInstancePreferencesTab(prefService);
		instanceTab.createInstancePreferencesTab(page, 	tabFolder);
		tabs[1] = instanceTab;
		
		LConfigurationPreferencesTab configurationTab = new LConfigurationPreferencesTab(prefService);
		configurationTab.createConfigurationPreferencesTab(page, tabFolder);
		tabs[2] = configurationTab;

		LDefaultPreferencesTab defaultTab = new LDefaultPreferencesTab(prefService);
		defaultTab.createDefaultPreferencesTab(page, tabFolder);
		tabs[3] = defaultTab;
		
		return tabs;
	}

}
