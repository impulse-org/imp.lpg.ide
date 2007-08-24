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
 * The project level preferences tab.
 */


public class LPGPreferencesDialogProjectTab extends ProjectPreferencesTab {

	public LPGPreferencesDialogProjectTab(IPreferencesService prefService) {
		super(prefService);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the project preferences level.
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
			"project", "UseDefaultExecutable", "UseDefaultExecutable",
			parent,
			false, false,
			true, true,
			false, false,
			true);
			Link UseDefaultExecutableDetailsLink = prefUtils.createDetailsLink(parent, UseDefaultExecutable, UseDefaultExecutable.getChangeControl().getParent(), "Details ...");

		fields.add(UseDefaultExecutable);


		FileFieldEditor ExecutableToUse = prefUtils.makeNewFileField(
			page, tab, prefsService,
			"project", "ExecutableToUse", "ExecutableToUse",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
			Link ExecutableToUseDetailsLink = prefUtils.createDetailsLink(parent, ExecutableToUse, ExecutableToUse.getTextControl().getParent(), "Details ...");

		fields.add(ExecutableToUse);


		prefUtils.createToggleFieldListener(UseDefaultExecutable, ExecutableToUse, false);
		boolean isEnabledExecutableToUse = false;
		ExecutableToUse.getTextControl().setEditable(isEnabledExecutableToUse);
		ExecutableToUse.getTextControl().setEnabled(isEnabledExecutableToUse);
		ExecutableToUse.setEnabled(isEnabledExecutableToUse, ExecutableToUse.getParent());


		BooleanFieldEditor UseDefaultIncludePath = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"project", "UseDefaultIncludePath", "UseDefaultIncludePath",
			parent,
			false, false,
			true, false,
			false, false,
			true);
			Link UseDefaultIncludePathDetailsLink = prefUtils.createDetailsLink(parent, UseDefaultIncludePath, UseDefaultIncludePath.getChangeControl().getParent(), "Details ...");

		fields.add(UseDefaultIncludePath);


		DirectoryListFieldEditor IncludePathToUse = prefUtils.makeNewDirectoryListField(
			page, tab, prefsService,
			"project", "IncludePathToUse", "IncludePathToUse",
			parent,
			false, false,
			true, ".",
			true, "",
			true);
			Link IncludePathToUseDetailsLink = prefUtils.createDetailsLink(parent, IncludePathToUse, IncludePathToUse.getTextControl().getParent(), "Details ...");

		fields.add(IncludePathToUse);


		prefUtils.createToggleFieldListener(UseDefaultIncludePath, IncludePathToUse, false);
		boolean isEnabledIncludePathToUse = false;
		IncludePathToUse.getTextControl().setEditable(isEnabledIncludePathToUse);
		IncludePathToUse.getTextControl().setEnabled(isEnabledIncludePathToUse);
		IncludePathToUse.setEnabled(isEnabledIncludePathToUse, IncludePathToUse.getParent());


		StringFieldEditor SourceFileExtensions = prefUtils.makeNewStringField(
			page, tab, prefsService,
			"project", "SourceFileExtensions", "SourceFileExtensions",
			parent,
			false, false,
			true, "g,lpg,gra",
			true, "",
			true);
			Link SourceFileExtensionsDetailsLink = prefUtils.createDetailsLink(parent, SourceFileExtensions, SourceFileExtensions.getTextControl().getParent(), "Details ...");

		fields.add(SourceFileExtensions);


		StringFieldEditor IncludeFileExtensions = prefUtils.makeNewStringField(
			page, tab, prefsService,
			"project", "IncludeFileExtensions", "IncludeFileExtensions",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
			Link IncludeFileExtensionsDetailsLink = prefUtils.createDetailsLink(parent, IncludeFileExtensions, IncludeFileExtensions.getTextControl().getParent(), "Details ...");

		fields.add(IncludeFileExtensions);


		BooleanFieldEditor EmitDiagnostics = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"project", "EmitDiagnostics", "EmitDiagnostics",
			parent,
			false, false,
			true, false,
			false, false,
			true);
			Link EmitDiagnosticsDetailsLink = prefUtils.createDetailsLink(parent, EmitDiagnostics, EmitDiagnostics.getChangeControl().getParent(), "Details ...");

		fields.add(EmitDiagnostics);


		BooleanFieldEditor GenerateListings = prefUtils.makeNewBooleanField(
			page, tab, prefsService,
			"project", "GenerateListings", "GenerateListings",
			parent,
			false, false,
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


	protected void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite)
	{
		boolean haveCurrentListeners = false;

		Preferences oldeNode = event.getPrevious();
		Preferences newNode = event.getNew();

		if (oldeNode == null && newNode == null) {
			// Happens sometimes when you clear the project selection.
			// Nothing, really, to do in this case ...
			return;
		}

		// If oldeNode is not null, we want to remove any preference-change listeners from it
		if (oldeNode != null && oldeNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {
			// Print an advisory message if you want to
		}

		// Declare local references to the fields
		BooleanFieldEditor UseDefaultExecutable = (BooleanFieldEditor) fields[0];
		FileFieldEditor ExecutableToUse = (FileFieldEditor) fields[1];
		BooleanFieldEditor UseDefaultIncludePath = (BooleanFieldEditor) fields[2];
		DirectoryListFieldEditor IncludePathToUse = (DirectoryListFieldEditor) fields[3];
		StringFieldEditor SourceFileExtensions = (StringFieldEditor) fields[4];
		StringFieldEditor IncludeFileExtensions = (StringFieldEditor) fields[5];
		BooleanFieldEditor EmitDiagnostics = (BooleanFieldEditor) fields[6];
		BooleanFieldEditor GenerateListings = (BooleanFieldEditor) fields[7];

		// Declare a 'holder' for each preference field; not strictly necessary
		// but helpful in various manipulations of fields and controls to follow
		Composite UseDefaultExecutableHolder = null;
		Composite ExecutableToUseHolder = null;
		Composite UseDefaultIncludePathHolder = null;
		Composite IncludePathToUseHolder = null;
		Composite SourceFileExtensionsHolder = null;
		Composite IncludeFileExtensionsHolder = null;
		Composite EmitDiagnosticsHolder = null;
		Composite GenerateListingsHolder = null;
		// If we have a new project preferences node, then do various things
		// to set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
			// Set project name in the selected-project field
			selectedProjectName.setStringValue(newNode.name());

			// If the containing composite is not disposed, then set field values
			// and make them enabled and editable (as appropriate to the type of field)

			if (!composite.isDisposed()) {
				// Note:  Where there are toggles between fields, it is a good idea to set the
				// properties of the dependent field here according to the values they should have
				// based on the independent field.  There should be listeners to take care of 
				// that sort of adjustment once the tab is established, but when properties are
				// first initialized here, the properties may not always be set correctly through
				// the toggle.  I'm not entirely sure why that happens, except that there may be
				// a race condition between the setting of the dependent values by the listener
				// and the setting of those values here.  If the values are set by the listener
				// first (which might be surprising, but may be possible) then they will be
				// overwritten by values set here--so the values set here should be consistent
				// with what the listener would set.

				UseDefaultExecutableHolder = UseDefaultExecutable.getChangeControl().getParent();
				prefUtils.setField(UseDefaultExecutable, UseDefaultExecutableHolder);
				UseDefaultExecutable.getChangeControl().setEnabled(true);

				ExecutableToUseHolder = ExecutableToUse.getTextControl().getParent();
				prefUtils.setField(ExecutableToUse, ExecutableToUseHolder);
				ExecutableToUse.getTextControl().setEditable(!UseDefaultExecutable.getBooleanValue());
				ExecutableToUse.getTextControl().setEnabled(!UseDefaultExecutable.getBooleanValue());
				ExecutableToUse.setEnabled(!UseDefaultExecutable.getBooleanValue(), ExecutableToUse.getParent());

				UseDefaultIncludePathHolder = UseDefaultIncludePath.getChangeControl().getParent();
				prefUtils.setField(UseDefaultIncludePath, UseDefaultIncludePathHolder);
				UseDefaultIncludePath.getChangeControl().setEnabled(true);

				IncludePathToUseHolder = IncludePathToUse.getTextControl().getParent();
				prefUtils.setField(IncludePathToUse, IncludePathToUseHolder);
				IncludePathToUse.getTextControl().setEditable(!UseDefaultIncludePath.getBooleanValue());
				IncludePathToUse.getTextControl().setEnabled(!UseDefaultIncludePath.getBooleanValue());
				IncludePathToUse.setEnabled(!UseDefaultIncludePath.getBooleanValue(), IncludePathToUse.getParent());

				SourceFileExtensionsHolder = SourceFileExtensions.getTextControl().getParent();
				prefUtils.setField(SourceFileExtensions, SourceFileExtensionsHolder);
				SourceFileExtensions.getTextControl().setEditable(true);
				SourceFileExtensions.getTextControl().setEnabled(true);
				SourceFileExtensions.setEnabled(true, SourceFileExtensions.getParent());

				IncludeFileExtensionsHolder = IncludeFileExtensions.getTextControl().getParent();
				prefUtils.setField(IncludeFileExtensions, IncludeFileExtensionsHolder);
				IncludeFileExtensions.getTextControl().setEditable(true);
				IncludeFileExtensions.getTextControl().setEnabled(true);
				IncludeFileExtensions.setEnabled(true, IncludeFileExtensions.getParent());

				EmitDiagnosticsHolder = EmitDiagnostics.getChangeControl().getParent();
				prefUtils.setField(EmitDiagnostics, EmitDiagnosticsHolder);
				EmitDiagnostics.getChangeControl().setEnabled(true);

				GenerateListingsHolder = GenerateListings.getChangeControl().getParent();
				prefUtils.setField(GenerateListings, GenerateListingsHolder);
				GenerateListings.getChangeControl().setEnabled(true);

				clearModifiedMarksOnLabels();
			}

			// Add property change listeners
			if (UseDefaultExecutableHolder != null) addProjectPreferenceChangeListeners(UseDefaultExecutable, "UseDefaultExecutable", UseDefaultExecutableHolder);
			if (ExecutableToUseHolder != null) addProjectPreferenceChangeListeners(ExecutableToUse, "ExecutableToUse", ExecutableToUseHolder);
			if (UseDefaultIncludePathHolder != null) addProjectPreferenceChangeListeners(UseDefaultIncludePath, "UseDefaultIncludePath", UseDefaultIncludePathHolder);
			if (IncludePathToUseHolder != null) addProjectPreferenceChangeListeners(IncludePathToUse, "IncludePathToUse", IncludePathToUseHolder);
			if (SourceFileExtensionsHolder != null) addProjectPreferenceChangeListeners(SourceFileExtensions, "SourceFileExtensions", SourceFileExtensionsHolder);
			if (IncludeFileExtensionsHolder != null) addProjectPreferenceChangeListeners(IncludeFileExtensions, "IncludeFileExtensions", IncludeFileExtensionsHolder);
			if (EmitDiagnosticsHolder != null) addProjectPreferenceChangeListeners(EmitDiagnostics, "EmitDiagnostics", EmitDiagnosticsHolder);
			if (GenerateListingsHolder != null) addProjectPreferenceChangeListeners(GenerateListings, "GenerateListings", GenerateListingsHolder);

			haveCurrentListeners = true;
		}

		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected\nn			// Unset project name in the tab
			selectedProjectName.setStringValue("none selected");

			// Clear the preferences from the store
			prefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);

			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				UseDefaultExecutable.getChangeControl().setEnabled(false);

				ExecutableToUse.getTextControl().setEditable(false);
				ExecutableToUse.getTextControl().setEnabled(false);
				ExecutableToUse.setEnabled(false, ExecutableToUse.getParent());

				UseDefaultIncludePath.getChangeControl().setEnabled(false);

				IncludePathToUse.getTextControl().setEditable(false);
				IncludePathToUse.getTextControl().setEnabled(false);
				IncludePathToUse.setEnabled(false, IncludePathToUse.getParent());

				SourceFileExtensions.getTextControl().setEditable(false);
				SourceFileExtensions.getTextControl().setEnabled(false);
				SourceFileExtensions.setEnabled(false, SourceFileExtensions.getParent());

				IncludeFileExtensions.getTextControl().setEditable(false);
				IncludeFileExtensions.getTextControl().setEnabled(false);
				IncludeFileExtensions.setEnabled(false, IncludeFileExtensions.getParent());

				EmitDiagnostics.getChangeControl().setEnabled(false);

				GenerateListings.getChangeControl().setEnabled(false);

			}

			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
			// To help assure that field properties are established properly
			performApply();
		}
	}


}
