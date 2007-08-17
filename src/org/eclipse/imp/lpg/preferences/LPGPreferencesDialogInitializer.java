package org.eclipse.imp.lpg.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.builder.LPGBuilder;
import org.eclipse.imp.preferences.IPreferencesService;

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
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_INCLUDEPATHTOUSE, ".;..;");
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_SOURCEFILEEXTENSIONS, "g");
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_INCLUDEFILEEXTENSIONS, "gi");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_EMITDIAGNOSTICS, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_GENERATELISTINGS, true);
	}
	
	
	public String getExecutableToUse() {
		return LPGBuilder.getDefaultExecutablePath();
	}
	
	
}
