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


/**
 * The project level preferences tab.
 */
public class LPGProjectTab extends ProjectPreferencesTab {

	public LPGProjectTab(IPreferencesService prefService) {
		super(prefService, false);
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
	protected FieldEditor[] createFields(TabbedPreferencesPage page, Composite parent)
	{
		List<FieldEditor> fields = new ArrayList<FieldEditor>();

		BooleanFieldEditor UseDefaultExecutable = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "UseDefaultExecutable", "Use default executable",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(UseDefaultExecutable);

		Link UseDefaultExecutableDetailsLink = fPrefUtils.createDetailsLink(parent, UseDefaultExecutable, UseDefaultExecutable.getChangeControl().getParent(), "Details ...");

		UseDefaultExecutableDetailsLink.setEnabled(false);
		fDetailsLinks.add(UseDefaultExecutableDetailsLink);


		FileFieldEditor ExecutableToUse = fPrefUtils.makeNewFileField(
			page, this, fPrefService,
			"project", "ExecutableToUse", "Executable to use",
			"The full path to the LPG generator executable",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
		fields.add(ExecutableToUse);

		Link ExecutableToUseDetailsLink = fPrefUtils.createDetailsLink(parent, ExecutableToUse, ExecutableToUse.getTextControl().getParent(), "Details ...");

		ExecutableToUseDetailsLink.setEnabled(false);
		fDetailsLinks.add(ExecutableToUseDetailsLink);


		fPrefUtils.createToggleFieldListener(UseDefaultExecutable, ExecutableToUse, false);
		boolean isEnabledExecutableToUse = false;
		ExecutableToUse.getTextControl().setEditable(isEnabledExecutableToUse);
		ExecutableToUse.getTextControl().setEnabled(isEnabledExecutableToUse);
		ExecutableToUse.setEnabled(isEnabledExecutableToUse, ExecutableToUse.getParent());


		BooleanFieldEditor UseDefaultIncludePath = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "UseDefaultIncludePath", "Use default include path",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(UseDefaultIncludePath);

		Link UseDefaultIncludePathDetailsLink = fPrefUtils.createDetailsLink(parent, UseDefaultIncludePath, UseDefaultIncludePath.getChangeControl().getParent(), "Details ...");

		UseDefaultIncludePathDetailsLink.setEnabled(false);
		fDetailsLinks.add(UseDefaultIncludePathDetailsLink);


		DirectoryListFieldEditor IncludePathToUse = fPrefUtils.makeNewDirectoryListField(
			page, this, fPrefService,
			"project", "IncludePathToUse", "Include path to use",
			"A semicolon-separated list of folders to search for template and include files",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
		fields.add(IncludePathToUse);

		Link IncludePathToUseDetailsLink = fPrefUtils.createDetailsLink(parent, IncludePathToUse, IncludePathToUse.getTextControl().getParent(), "Details ...");

		IncludePathToUseDetailsLink.setEnabled(false);
		fDetailsLinks.add(IncludePathToUseDetailsLink);


		fPrefUtils.createToggleFieldListener(UseDefaultIncludePath, IncludePathToUse, false);
		boolean isEnabledIncludePathToUse = false;
		IncludePathToUse.getTextControl().setEditable(isEnabledIncludePathToUse);
		IncludePathToUse.getTextControl().setEnabled(isEnabledIncludePathToUse);
		IncludePathToUse.setEnabled(isEnabledIncludePathToUse, IncludePathToUse.getParent());


		StringFieldEditor SourceFileExtensions = fPrefUtils.makeNewStringField(
			page, this, fPrefService,
			"project", "SourceFileExtensions", "Source file extensions",
			"A comma-separated list of file name extensions identifying top-level LPG grammar files",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
		fields.add(SourceFileExtensions);

		Link SourceFileExtensionsDetailsLink = fPrefUtils.createDetailsLink(parent, SourceFileExtensions, SourceFileExtensions.getTextControl().getParent(), "Details ...");

		SourceFileExtensionsDetailsLink.setEnabled(false);
		fDetailsLinks.add(SourceFileExtensionsDetailsLink);


		StringFieldEditor IncludeFileExtensions = fPrefUtils.makeNewStringField(
			page, this, fPrefService,
			"project", "IncludeFileExtensions", "Include file extensions",
			"A comma-separated list of file name extensions identifying included LPG grammar files",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
		fields.add(IncludeFileExtensions);

		Link IncludeFileExtensionsDetailsLink = fPrefUtils.createDetailsLink(parent, IncludeFileExtensions, IncludeFileExtensions.getTextControl().getParent(), "Details ...");

		IncludeFileExtensionsDetailsLink.setEnabled(false);
		fDetailsLinks.add(IncludeFileExtensionsDetailsLink);


		BooleanFieldEditor EmitDiagnostics = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "EmitDiagnostics", "Emit diagnostics",
			"If true, emit messages to the LPG plugin log as the build proceeds",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(EmitDiagnostics);

		Link EmitDiagnosticsDetailsLink = fPrefUtils.createDetailsLink(parent, EmitDiagnostics, EmitDiagnostics.getChangeControl().getParent(), "Details ...");

		EmitDiagnosticsDetailsLink.setEnabled(false);
		fDetailsLinks.add(EmitDiagnosticsDetailsLink);


		BooleanFieldEditor GenerateListings = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "GenerateListings", "Generate listings",
			"If true, place detailed information about each grammar file in a corresponding listing file",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(GenerateListings);

		Link GenerateListingsDetailsLink = fPrefUtils.createDetailsLink(parent, GenerateListings, GenerateListings.getChangeControl().getParent(), "Details ...");

		GenerateListingsDetailsLink.setEnabled(false);
		fDetailsLinks.add(GenerateListingsDetailsLink);


		BooleanFieldEditor QuietOutput = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "QuietOutput", "Quiet output",
			"If true, suppress the output of various facts like the number of terminals, lookahead, etc.",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(QuietOutput);

		Link QuietOutputDetailsLink = fPrefUtils.createDetailsLink(parent, QuietOutput, QuietOutput.getChangeControl().getParent(), "Details ...");

		QuietOutputDetailsLink.setEnabled(false);
		fDetailsLinks.add(QuietOutputDetailsLink);

		return fields.toArray(new FieldEditor[fields.size()]);
	}


	protected void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite)
	{
		boolean haveCurrentListeners = false;

		Preferences oldNode = event.getPrevious();
		Preferences newNode = event.getNew();

		if (oldNode == null && newNode == null) {
			// Happens sometimes when you clear the project selection.
			// Nothing, really, to do in this case ...
			return;
		}

		// If oldeNode is not null, we want to remove any preference-change listeners from it
		if (oldNode != null && oldNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {
			// Print an advisory message if you want to
		}

		// Declare local references to the fields
		BooleanFieldEditor UseDefaultExecutable = (BooleanFieldEditor) fFields[0];
		Link UseDefaultExecutableDetailsLink = (Link) fDetailsLinks.get(0);
		FileFieldEditor ExecutableToUse = (FileFieldEditor) fFields[1];
		Link ExecutableToUseDetailsLink = (Link) fDetailsLinks.get(1);
		BooleanFieldEditor UseDefaultIncludePath = (BooleanFieldEditor) fFields[2];
		Link UseDefaultIncludePathDetailsLink = (Link) fDetailsLinks.get(2);
		DirectoryListFieldEditor IncludePathToUse = (DirectoryListFieldEditor) fFields[3];
		Link IncludePathToUseDetailsLink = (Link) fDetailsLinks.get(3);
		StringFieldEditor SourceFileExtensions = (StringFieldEditor) fFields[4];
		Link SourceFileExtensionsDetailsLink = (Link) fDetailsLinks.get(4);
		StringFieldEditor IncludeFileExtensions = (StringFieldEditor) fFields[5];
		Link IncludeFileExtensionsDetailsLink = (Link) fDetailsLinks.get(5);
		BooleanFieldEditor EmitDiagnostics = (BooleanFieldEditor) fFields[6];
		Link EmitDiagnosticsDetailsLink = (Link) fDetailsLinks.get(6);
		BooleanFieldEditor GenerateListings = (BooleanFieldEditor) fFields[7];
		Link GenerateListingsDetailsLink = (Link) fDetailsLinks.get(7);
		BooleanFieldEditor QuietOutput = (BooleanFieldEditor) fFields[8];
		Link QuietOutputDetailsLink = (Link) fDetailsLinks.get(8);

		// If we have a new project preferences node, then do various things
		// to set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
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

				fPrefUtils.setField(UseDefaultExecutable, UseDefaultExecutable.getHolder());
				UseDefaultExecutable.getChangeControl().setEnabled(true);
				UseDefaultExecutableDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(ExecutableToUse, ExecutableToUse.getHolder());
				ExecutableToUse.getTextControl().setEditable(!UseDefaultExecutable.getBooleanValue());
				ExecutableToUse.getTextControl().setEnabled(!UseDefaultExecutable.getBooleanValue());
				ExecutableToUse.setEnabled(!UseDefaultExecutable.getBooleanValue(), ExecutableToUse.getParent());
				ExecutableToUseDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(UseDefaultIncludePath, UseDefaultIncludePath.getHolder());
				UseDefaultIncludePath.getChangeControl().setEnabled(true);
				UseDefaultIncludePathDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(IncludePathToUse, IncludePathToUse.getHolder());
				IncludePathToUse.getTextControl().setEditable(!UseDefaultIncludePath.getBooleanValue());
				IncludePathToUse.getTextControl().setEnabled(!UseDefaultIncludePath.getBooleanValue());
				IncludePathToUse.setEnabled(!UseDefaultIncludePath.getBooleanValue(), IncludePathToUse.getParent());
				IncludePathToUseDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(SourceFileExtensions, SourceFileExtensions.getHolder());
				SourceFileExtensions.getTextControl().setEditable(true);
				SourceFileExtensions.getTextControl().setEnabled(true);
				SourceFileExtensions.setEnabled(true, SourceFileExtensions.getParent());
				SourceFileExtensionsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(IncludeFileExtensions, IncludeFileExtensions.getHolder());
				IncludeFileExtensions.getTextControl().setEditable(true);
				IncludeFileExtensions.getTextControl().setEnabled(true);
				IncludeFileExtensions.setEnabled(true, IncludeFileExtensions.getParent());
				IncludeFileExtensionsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(EmitDiagnostics, EmitDiagnostics.getHolder());
				EmitDiagnostics.getChangeControl().setEnabled(true);
				EmitDiagnosticsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(GenerateListings, GenerateListings.getHolder());
				GenerateListings.getChangeControl().setEnabled(true);
				GenerateListingsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(QuietOutput, QuietOutput.getHolder());
				QuietOutput.getChangeControl().setEnabled(true);
				QuietOutputDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				clearModifiedMarksOnLabels();
			}

			// Add property change listeners
			if (UseDefaultExecutable.getHolder() != null) addProjectPreferenceChangeListeners(UseDefaultExecutable, "UseDefaultExecutable", UseDefaultExecutable.getHolder());
			if (ExecutableToUse.getHolder() != null) addProjectPreferenceChangeListeners(ExecutableToUse, "ExecutableToUse", ExecutableToUse.getHolder());
			if (UseDefaultIncludePath.getHolder() != null) addProjectPreferenceChangeListeners(UseDefaultIncludePath, "UseDefaultIncludePath", UseDefaultIncludePath.getHolder());
			if (IncludePathToUse.getHolder() != null) addProjectPreferenceChangeListeners(IncludePathToUse, "IncludePathToUse", IncludePathToUse.getHolder());
			if (SourceFileExtensions.getHolder() != null) addProjectPreferenceChangeListeners(SourceFileExtensions, "SourceFileExtensions", SourceFileExtensions.getHolder());
			if (IncludeFileExtensions.getHolder() != null) addProjectPreferenceChangeListeners(IncludeFileExtensions, "IncludeFileExtensions", IncludeFileExtensions.getHolder());
			if (EmitDiagnostics.getHolder() != null) addProjectPreferenceChangeListeners(EmitDiagnostics, "EmitDiagnostics", EmitDiagnostics.getHolder());
			if (GenerateListings.getHolder() != null) addProjectPreferenceChangeListeners(GenerateListings, "GenerateListings", GenerateListings.getHolder());
			if (QuietOutput.getHolder() != null) addProjectPreferenceChangeListeners(QuietOutput, "QuietOutput", QuietOutput.getHolder());

			haveCurrentListeners = true;
		}

		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected\nn			// Clear the preferences from the store
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);

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

				QuietOutput.getChangeControl().setEnabled(false);

			}

			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
			// To help assure that field properties are established properly
			performApply();
		}
	}


}
