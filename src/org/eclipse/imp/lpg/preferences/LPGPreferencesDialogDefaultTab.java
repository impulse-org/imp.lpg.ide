/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.preferences;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.imp.preferences.*;
import org.eclipse.imp.preferences.fields.*;
import org.osgi.service.prefs.Preferences;

//		 TODO:  Import additional classes for specific field types from
//		 org.eclipse.uide.preferences.fields

/**
 * The default level preferences tab.
 */


public class LPGPreferencesDialogDefaultTab extends DefaultPreferencesTab {

	public LPGPreferencesDialogDefaultTab(IPreferencesService prefService) {
		super(prefService);
	}

	/**
	 * Creates a language-specific preferences initializer.
	 *
	 * @return    The preference initializer to be used to initialize
	 *            preferences in this tab
	 */
	public AbstractPreferenceInitializer getPreferenceInitializer() {
		LPGPreferencesDialogInitializer preferencesInitializer = new LPGPreferencesDialogInitializer();
		return preferencesInitializer;
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the default preferences level.
	 *
	 * Overrides an unimplemented method in PreferencesTab.
	 *
	 * @return    An array that contains the created preference fields
	 *
	 */
	protected FieldEditor[] createFields(
		TabbedPreferencesPage page, PreferencesTab tab, String tabLevel,
		Composite parent, IPreferencesService prefsService)
	{
		List fields = new ArrayList();

		BooleanFieldEditor UseDefaultExecutable = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"default", "UseDefaultExecutable", "UseDefaultExecutable",
			parent,
			false, false,
			true, true,
			false, false,
			false);
			Link UseDefaultExecutableDetailsLink = prefUtils.createDetailsLink(parent, UseDefaultExecutable, UseDefaultExecutable.getChangeControl().getParent(), "Details ...");

		fields.add(UseDefaultExecutable);


		FileFieldEditor ExecutableToUse = prefUtils.makeNewFileField(
			page, tab, prefsService,
			"default", "ExecutableToUse", "ExecutableToUse",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			false);
			Link ExecutableToUseDetailsLink = prefUtils.createDetailsLink(parent, ExecutableToUse, ExecutableToUse.getTextControl().getParent(), "Details ...");

		fields.add(ExecutableToUse);


		prefUtils.createToggleFieldListener(UseDefaultExecutable, ExecutableToUse, false);
		boolean isEnabledExecutableToUse = !UseDefaultExecutable.getBooleanValue();
		ExecutableToUse.getTextControl().setEditable(isEnabledExecutableToUse);
		ExecutableToUse.getTextControl().setEnabled(isEnabledExecutableToUse);
		ExecutableToUse.setEnabled(isEnabledExecutableToUse, ExecutableToUse.getParent());


		BooleanFieldEditor UseDefaultIncludePath = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"default", "UseDefaultIncludePath", "UseDefaultIncludePath",
			parent,
			false, false,
			true, false,
			false, false,
			false);
			Link UseDefaultIncludePathDetailsLink = prefUtils.createDetailsLink(parent, UseDefaultIncludePath, UseDefaultIncludePath.getChangeControl().getParent(), "Details ...");

		fields.add(UseDefaultIncludePath);


		DirectoryListFieldEditor IncludePathToUse = prefUtils.makeNewDirectoryListField(
			page, tab, prefsService,
			"default", "IncludePathToUse", "IncludePathToUse",
			parent,
			false, false,
			true, ".",
			true, "",
			false);
			Link IncludePathToUseDetailsLink = prefUtils.createDetailsLink(parent, IncludePathToUse, IncludePathToUse.getTextControl().getParent(), "Details ...");

		fields.add(IncludePathToUse);


		prefUtils.createToggleFieldListener(UseDefaultIncludePath, IncludePathToUse, false);
		boolean isEnabledIncludePathToUse = !UseDefaultIncludePath.getBooleanValue();
		IncludePathToUse.getTextControl().setEditable(isEnabledIncludePathToUse);
		IncludePathToUse.getTextControl().setEnabled(isEnabledIncludePathToUse);
		IncludePathToUse.setEnabled(isEnabledIncludePathToUse, IncludePathToUse.getParent());


		StringFieldEditor SourceFileExtensions = prefUtils.makeNewStringField(
			page, tab, prefsService,
			"default", "SourceFileExtensions", "SourceFileExtensions",
			parent,
			false, false,
			true, "g,lpg,gra",
			true, "",
			false);
			Link SourceFileExtensionsDetailsLink = prefUtils.createDetailsLink(parent, SourceFileExtensions, SourceFileExtensions.getTextControl().getParent(), "Details ...");

		fields.add(SourceFileExtensions);


		StringFieldEditor IncludeFileExtensions = prefUtils.makeNewStringField(
			page, tab, prefsService,
			"default", "IncludeFileExtensions", "IncludeFileExtensions",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			false);
			Link IncludeFileExtensionsDetailsLink = prefUtils.createDetailsLink(parent, IncludeFileExtensions, IncludeFileExtensions.getTextControl().getParent(), "Details ...");

		fields.add(IncludeFileExtensions);


		BooleanFieldEditor EmitDiagnostics = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"default", "EmitDiagnostics", "EmitDiagnostics",
			parent,
			false, false,
			true, false,
			false, false,
			false);
			Link EmitDiagnosticsDetailsLink = prefUtils.createDetailsLink(parent, EmitDiagnostics, EmitDiagnostics.getChangeControl().getParent(), "Details ...");

		fields.add(EmitDiagnostics);


		BooleanFieldEditor GenerateListings = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"default", "GenerateListings", "GenerateListings",
			parent,
			false, false,
			true, false,
			false, false,
			false);
			Link GenerateListingsDetailsLink = prefUtils.createDetailsLink(parent, GenerateListings, GenerateListings.getChangeControl().getParent(), "Details ...");

		fields.add(GenerateListings);

		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}
}
