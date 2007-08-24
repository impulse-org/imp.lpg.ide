/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.builder.LPGBuilder;
import org.eclipse.imp.preferences.IPreferencesService;
import org.osgi.framework.Bundle;

/**
 * Initializations of default values for preferences.
 */


public class LPGPreferencesDialogInitializer extends AbstractPreferenceInitializer {
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferencesService service = LPGRuntimePlugin.getPreferencesService();

		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_USEDEFAULTEXECUTABLE, true);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_EXECUTABLETOUSE, getExecutableToUse());					//"C:\\eclipse\\workspaces\\eclipse-3.2.2-2\\lpg.runtime.java\\lpgexe\\lpg-win32_x86.exe");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_USEDEFAULTINCLUDEPATH, true);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_INCLUDEPATHTOUSE, getDefaultIncludePath());
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_SOURCEFILEEXTENSIONS, "g");
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_INCLUDEFILEEXTENSIONS, "gi");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_EMITDIAGNOSTICS, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_GENERATELISTINGS, true);
	}
	
			
	public String getExecutableToUse() {
		return LPGBuilder.getDefaultExecutablePath();
	}
	
	
    public static String getDefaultIncludePath() {
    	Bundle bundle= Platform.getBundle(LPGBuilder.LPG_PLUGIN_ID);
    	try {
    	    // Use getEntry() rather than getResource(), since the "templates" folder is
    	    // no longer inside the plugin jar (which is now expanded upon installation).
    	    String tmplPath= FileLocator.toFileURL(bundle.getEntry("templates")).getFile();

    	    if (Platform.getOS().equals("win32"))
    		tmplPath= tmplPath.substring(1);
    	    return tmplPath;
    	} catch(IOException e) {
    	    return null;
    	}
    }
}