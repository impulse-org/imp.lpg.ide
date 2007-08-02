package org.eclipse.imp.lpg.preferences;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.builder.LPGBuilder;
import org.eclipse.imp.preferences.ISafariPreferencesService;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initializes JikesPG preference default values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
    	
	    // SMS 8 Sep 2006
	    // Use the service instead of the store
	    ///*
    	// Commented this back in so that the original preferences
    	// page would have some values to display
		IPreferenceStore store= LPGRuntimePlugin.getInstance().getPreferenceStore();
	
		store.setDefault(PreferenceConstants.P_EMIT_MESSAGES, getDefaultEmitMessages());
		store.setDefault(PreferenceConstants.P_GEN_LISTINGS, getDefaultGenerateListings());
		store.setDefault(PreferenceConstants.P_USE_DEFAULT_EXEC, getDefaultUseDefaultExecutable());
		store.setDefault(PreferenceConstants.P_JIKESPG_EXEC_PATH, LPGBuilder.getDefaultExecutablePath());
		store.setDefault(PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR, getDefaultUseDefaultIncludeDirs());
		store.setDefault(PreferenceConstants.P_JIKESPG_INCLUDE_DIRS, LPGBuilder.getDefaultIncludePath());
		store.setDefault(PreferenceConstants.P_EXTENSION_LIST, "g,lpg,gra");
		store.setDefault(PreferenceConstants.P_NON_ROOT_EXTENSION_LIST, "gi");
	    //*/
    
		ISafariPreferencesService service = LPGRuntimePlugin.getPreferencesService();
		
		// Examples:
		service.setBooleanPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_EMIT_MESSAGES, getDefaultEmitMessages());
		service.setBooleanPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_GEN_LISTINGS, getDefaultGenerateListings());
		service.setBooleanPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_USE_DEFAULT_EXEC, getDefaultUseDefaultExecutable());
		service.setStringPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_JIKESPG_EXEC_PATH, getDefaultExecutablePath());
		service.setBooleanPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR, getDefaultUseDefaultIncludeDirs());		
		service.setStringPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_JIKESPG_INCLUDE_DIRS, getDefaultIncludePath());
		service.setStringPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_EXTENSION_LIST, getDefaultExtensionList());
		service.setStringPreference(ISafariPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_NON_ROOT_EXTENSION_LIST, getDefaultNonRootExtensionList());
    }
    
    
    // SMS 21 Nov 2006
    // To make these values more uniformly accessible to other
    // users of preferences (notably preference pages)	

    public static boolean getDefaultUseDefaultExecutable() { return true; }
    
    public static String getDefaultExecutablePath() {
    	return LPGBuilder.getDefaultExecutablePath();
    }
    
    public static boolean getDefaultUseDefaultIncludeDirs() { return true; }
    
    public static String getDefaultIncludePath() {
    	return LPGBuilder.getDefaultIncludePath();
    }
    
    public static String getDefaultExtensionList() {
    	return "g, lpg, gra";
    }
    
    public static String getDefaultNonRootExtensionList() {
    	return "gi";
    }
  
    public static boolean getDefaultEmitMessages() { return true; }
    
    public static boolean getDefaultGenerateListings() { return false; }
    
}
