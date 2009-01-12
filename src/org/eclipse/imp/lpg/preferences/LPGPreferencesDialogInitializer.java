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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
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
		IPreferencesService service = LPGRuntimePlugin.getInstance().getPreferencesService();

		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_USEDEFAULTEXECUTABLE, true);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_EXECUTABLETOUSE, getExecutableToUse());
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_USEDEFAULTINCLUDEPATH, true);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_INCLUDEPATHTOUSE, getDefaultIncludePath());
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_SOURCEFILEEXTENSIONS, "g");
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_INCLUDEFILEEXTENSIONS, "gi");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_EMITDIAGNOSTICS, false);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, LPGPreferencesDialogConstants.P_GENERATELISTINGS, true);
	}
	
			
	public String getExecutableToUse() {
		return "${pluginResource:lpg.runtime/lpgexe/lpg-${os}_${arch}}";
	}
	
	
    public static String getDefaultIncludePath() {
        return "${pluginResource:lpg.runtime/templates}";
    }
}
