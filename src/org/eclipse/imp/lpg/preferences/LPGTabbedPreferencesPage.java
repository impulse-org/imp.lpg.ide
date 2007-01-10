package org.eclipse.safari.jikespg.preferences;


import org.eclipse.safari.jikespg.JikesPGRuntimePlugin;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.uide.preferences.ISafariPreferencesService;
import org.eclipse.uide.preferences.SafariPreferencesTab;
import org.eclipse.uide.preferences.SafariTabbedPreferencesPage;

/**
 * The Safari-based tabbed preferences page for JikesPG	
 */
public class JikesPGTabbedPreferencesPage extends SafariTabbedPreferencesPage {
	
	public JikesPGTabbedPreferencesPage() {
		super();
		// Get the language-specific preferences service in the
		// language-specific tab
		prefService = JikesPGRuntimePlugin.getPreferencesService();	//$ plugin name (based on language name or ?)
	}
	
	
	protected SafariPreferencesTab[] createTabs(
			ISafariPreferencesService prefService, SafariTabbedPreferencesPage page, TabFolder tabFolder) 
	{
		SafariPreferencesTab[] tabs = new SafariPreferencesTab[4];
		
		JikesPGProjectPreferencesTab projectTab = new JikesPGProjectPreferencesTab(prefService);
		projectTab.createProjectPreferencesTab(page, tabFolder);
		tabs[0] = projectTab;

		JikesPGInstancePreferencesTab instanceTab = new JikesPGInstancePreferencesTab(prefService);
		instanceTab.createInstancePreferencesTab(page, 	tabFolder);
		tabs[1] = instanceTab;
		
		JikesPGConfigurationPreferencesTab configurationTab = new JikesPGConfigurationPreferencesTab(prefService);
		configurationTab.createConfigurationPreferencesTab(page, tabFolder);
		tabs[2] = configurationTab;

		JikesPGDefaultPreferencesTab defaultTab = new JikesPGDefaultPreferencesTab(prefService);
		defaultTab.createDefaultPreferencesTab(page, tabFolder);
		tabs[3] = defaultTab;
		
		return tabs;
	}

}
