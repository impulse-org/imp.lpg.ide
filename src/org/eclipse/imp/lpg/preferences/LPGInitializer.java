/******************************************/
/* WARNING: GENERATED FILE - DO NOT EDIT! */
/******************************************/
package org.eclipse.imp.lpg.preferences;

import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.lpg.LPGRuntimePlugin;

/**
 * Initializations of default values for preferences.
 */
public class LPGInitializer extends PreferencesInitializer {
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferencesService service = LPGRuntimePlugin.getInstance().getPreferencesService();

		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_SOURCEFONT, "Courier New");
		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_TABWIDTH, 4);
		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_INDENTWIDTH, 4);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_SPACESFORTABS, false);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_USEDEFAULTEXECUTABLE, true);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_EXECUTABLETOUSE, "${pluginResource:lpg.generator/lpgexe/lpg-${os}_${arch}}");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_USEDEFAULTINCLUDEPATH, true);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_INCLUDEPATHTOUSE, ".;..;${pluginResource:lpg.generator/templates/java};${pluginResource:lpg.generator/include/java}");
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_SOURCEFILEEXTENSIONS, "g");
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_INCLUDEFILEEXTENSIONS, "gi");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_EMITDIAGNOSTICS, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_GENERATELISTINGS, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_QUIETOUTPUT, true);
	}

	/*
	 * Clear (remove) any preferences set on the given level.
	 */
	public void clearPreferencesOnLevel(String level) {
		IPreferencesService service = LPGRuntimePlugin.getInstance().getPreferencesService();
		service.clearPreferencesAtLevel(level);

	}
}
