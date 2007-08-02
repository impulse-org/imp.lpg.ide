package org.eclipse.imp.lpg.preferences;


import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.imp.preferences.ISafariPreferencesService;
import org.eclipse.imp.preferences.SafariPreferencesTab;
import org.eclipse.imp.preferences.SafariTabbedPreferencesPage;

/**
 * The Safari-based tabbed preferences page for LPG	
 */
public class LPGTabbedPreferencesPage extends SafariTabbedPreferencesPage {
	
	public LPGTabbedPreferencesPage() {
		super();
		// Get the language-specific preferences service in the
		// language-specific tab
		prefService = LPGRuntimePlugin.getPreferencesService();	//$ plugin name (based on language name or ?)
	}
	
	
	protected SafariPreferencesTab[] createTabs(
			ISafariPreferencesService prefService, SafariTabbedPreferencesPage page, TabFolder tabFolder) 
	{
		SafariPreferencesTab[] tabs = new SafariPreferencesTab[4];
		
		LPGProjectPreferencesTab projectTab = new LPGProjectPreferencesTab(prefService);
		projectTab.createProjectPreferencesTab(page, tabFolder);
		tabs[0] = projectTab;

		LPGInstancePreferencesTab instanceTab = new LPGInstancePreferencesTab(prefService);
		instanceTab.createInstancePreferencesTab(page, 	tabFolder);
		tabs[1] = instanceTab;
		
		LPGConfigurationPreferencesTab configurationTab = new LPGConfigurationPreferencesTab(prefService);
		configurationTab.createConfigurationPreferencesTab(page, tabFolder);
		tabs[2] = configurationTab;

		LPGDefaultPreferencesTab defaultTab = new LPGDefaultPreferencesTab(prefService);
		defaultTab.createDefaultPreferencesTab(page, tabFolder);
		tabs[3] = defaultTab;
		
		return tabs;
	}

}
