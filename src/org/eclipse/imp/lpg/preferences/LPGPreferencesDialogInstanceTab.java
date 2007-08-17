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
 * The instance level preferences tab.
 */


public class LPGPreferencesDialogInstanceTab extends InstancePreferencesTab {

	public LPGPreferencesDialogInstanceTab(IPreferencesService prefService) {
		super(prefService);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the instance preferences level.
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
			"instance", "UseDefaultExecutable", "UseDefaultExecutable",
			parent,
			true, true,
			true, true,
			false, false,
			true);
			Link UseDefaultExecutableDetailsLink = prefUtils.createDetailsLink(parent, UseDefaultExecutable, UseDefaultExecutable.getChangeControl().getParent(), "Details ...");

		fields.add(UseDefaultExecutable);


		FileFieldEditor ExecutableToUse = prefUtils.makeNewFileField(
			page, tab, prefsService,
			"instance", "ExecutableToUse", "ExecutableToUse",
			parent,
			true, true,
			false, "Unspecified",
			true, "",
			true);
			Link ExecutableToUseDetailsLink = prefUtils.createDetailsLink(parent, ExecutableToUse, ExecutableToUse.getTextControl().getParent(), "Details ...");

		fields.add(ExecutableToUse);


		prefUtils.createToggleFieldListener(UseDefaultExecutable, ExecutableToUse, false);
		boolean UseDefaultExecutableValue = UseDefaultExecutable.getBooleanValue();
		ExecutableToUse.getTextControl().setEditable(UseDefaultExecutableValue);
		ExecutableToUse.getTextControl().setEnabled(UseDefaultExecutableValue);
		ExecutableToUse.setEnabled(UseDefaultExecutableValue, ExecutableToUse.getParent());


		BooleanFieldEditor UseDefaultIncludePath = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"instance", "UseDefaultIncludePath", "UseDefaultIncludePath",
			parent,
			true, true,
			true, false,
			false, false,
			true);
			Link UseDefaultIncludePathDetailsLink = prefUtils.createDetailsLink(parent, UseDefaultIncludePath, UseDefaultIncludePath.getChangeControl().getParent(), "Details ...");

		fields.add(UseDefaultIncludePath);


		DirectoryListFieldEditor IncludePathToUse = prefUtils.makeNewDirectoryListField(
			page, tab, prefsService,
			"instance", "IncludePathToUse", "IncludePathToUse",
			parent,
			true, true,
			true, ".",
			true, "",
			true);
			Link IncludePathToUseDetailsLink = prefUtils.createDetailsLink(parent, IncludePathToUse, IncludePathToUse.getTextControl().getParent(), "Details ...");

		fields.add(IncludePathToUse);


		prefUtils.createToggleFieldListener(UseDefaultIncludePath, IncludePathToUse, false);
		boolean UseDefaultIncludePathValue = UseDefaultIncludePath.getBooleanValue();
		IncludePathToUse.getTextControl().setEditable(UseDefaultIncludePathValue);
		IncludePathToUse.getTextControl().setEnabled(UseDefaultIncludePathValue);
		IncludePathToUse.setEnabled(UseDefaultIncludePathValue, IncludePathToUse.getParent());


		StringFieldEditor SourceFileExtensions = prefUtils.makeNewStringField(
			page, tab, prefsService,
			"instance", "SourceFileExtensions", "SourceFileExtensions",
			parent,
			true, true,
			true, "g,lpg,gra",
			true, "",
			true);
			Link SourceFileExtensionsDetailsLink = prefUtils.createDetailsLink(parent, SourceFileExtensions, SourceFileExtensions.getTextControl().getParent(), "Details ...");

		fields.add(SourceFileExtensions);


		StringFieldEditor IncludeFileExtensions = prefUtils.makeNewStringField(
			page, tab, prefsService,
			"instance", "IncludeFileExtensions", "IncludeFileExtensions",
			parent,
			true, true,
			false, "Unspecified",
			true, "",
			true);
			Link IncludeFileExtensionsDetailsLink = prefUtils.createDetailsLink(parent, IncludeFileExtensions, IncludeFileExtensions.getTextControl().getParent(), "Details ...");

		fields.add(IncludeFileExtensions);


		BooleanFieldEditor EmitDiagnostics = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"instance", "EmitDiagnostics", "EmitDiagnostics",
			parent,
			true, true,
			true, false,
			false, false,
			true);
			Link EmitDiagnosticsDetailsLink = prefUtils.createDetailsLink(parent, EmitDiagnostics, EmitDiagnostics.getChangeControl().getParent(), "Details ...");

		fields.add(EmitDiagnostics);


		BooleanFieldEditor GenerateListings = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"instance", "GenerateListings", "GenerateListings",
			parent,
			true, true,
			true, false,
			false, false,
			true);
			Link GenerateListingsDetailsLink = prefUtils.createDetailsLink(parent, GenerateListings, GenerateListings.getChangeControl().getParent(), "Details ...");

		fields.add(GenerateListings);

		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}
}
