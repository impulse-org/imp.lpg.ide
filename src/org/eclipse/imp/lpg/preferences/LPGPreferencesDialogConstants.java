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

/**
 * Constant definitions for preferences.
 *
 * The preferences service uses Strings as keys for preference values,
 * so Strings defined here are used here to designate preference fields.
 * These strings are generated automatically from a preferences specification.
 * Other constants may be defined here manually.
 *
 */


public class LPGPreferencesDialogConstants {

	public static final String P_USEDEFAULTEXECUTABLE = "UseDefaultExecutable";

	public static final String P_EXECUTABLETOUSE = "ExecutableToUse";

	public static final String P_USEDEFAULTINCLUDEPATH = "UseDefaultIncludePath";

	public static final String P_INCLUDEPATHTOUSE = "IncludePathToUse";

	public static final String P_SOURCEFILEEXTENSIONS = "SourceFileExtensions";

	public static final String P_INCLUDEFILEEXTENSIONS = "IncludeFileExtensions";

	public static final String P_EMITDIAGNOSTICS = "EmitDiagnostics";

	public static final String P_GENERATELISTINGS = "GenerateListings";

}
