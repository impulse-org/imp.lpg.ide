package org.eclipse.imp.lpg.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.InstancePreferencesTab;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.preferences.fields.DirectoryListFieldEditor;
import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.imp.preferences.fields.FileFieldEditor;
import org.eclipse.imp.preferences.fields.FontFieldEditor;
import org.eclipse.imp.preferences.fields.IntegerFieldEditor;
import org.eclipse.imp.preferences.fields.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;


/**
 * The instance level preferences tab.
 */
public class LPGInstanceTab extends InstancePreferencesTab {

	public LPGInstanceTab(IPreferencesService prefService) {
		super(prefService, false);
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
	protected FieldEditor[] createFields(TabbedPreferencesPage page, Composite parent)
	{
		List<FieldEditor> fields = new ArrayList<FieldEditor>();

		FontFieldEditor sourceFont = fPrefUtils.makeNewFontField(
			page, this, fPrefService,
			"instance", "sourceFont", "source font",
			"",
			parent,
			true, true,
			true);
		fields.add(sourceFont);

		Link sourceFontDetailsLink = fPrefUtils.createDetailsLink(parent, sourceFont, sourceFont.getChangeControl().getParent(), "Details ...");

		sourceFontDetailsLink.setEnabled(true);
		fDetailsLinks.add(sourceFontDetailsLink);


		IntegerFieldEditor tabWidth = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "tabWidth", "tab width",
			"",
			parent,
			true, true,
			false, "0",
			true);
		fields.add(tabWidth);

		Link tabWidthDetailsLink = fPrefUtils.createDetailsLink(parent, tabWidth, tabWidth.getTextControl().getParent(), "Details ...");

		tabWidthDetailsLink.setEnabled(true);
		fDetailsLinks.add(tabWidthDetailsLink);


		IntegerFieldEditor indentWidth = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "indentWidth", "indent width",
			"",
			parent,
			true, true,
			false, "0",
			true);
		fields.add(indentWidth);

		Link indentWidthDetailsLink = fPrefUtils.createDetailsLink(parent, indentWidth, indentWidth.getTextControl().getParent(), "Details ...");

		indentWidthDetailsLink.setEnabled(true);
		fDetailsLinks.add(indentWidthDetailsLink);


		BooleanFieldEditor spacesForTabs = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "spacesForTabs", "spaces for tabs",
			"",
			parent,
			true, true,
			false, false,
			true);
		fields.add(spacesForTabs);

		Link spacesForTabsDetailsLink = fPrefUtils.createDetailsLink(parent, spacesForTabs, spacesForTabs.getChangeControl().getParent(), "Details ...");

		spacesForTabsDetailsLink.setEnabled(true);
		fDetailsLinks.add(spacesForTabsDetailsLink);


		BooleanFieldEditor UseDefaultExecutable = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "UseDefaultExecutable", "Use default executable",
			"",
			parent,
			true, true,
			false, false,
			true);
		fields.add(UseDefaultExecutable);

		Link UseDefaultExecutableDetailsLink = fPrefUtils.createDetailsLink(parent, UseDefaultExecutable, UseDefaultExecutable.getChangeControl().getParent(), "Details ...");

		UseDefaultExecutableDetailsLink.setEnabled(true);
		fDetailsLinks.add(UseDefaultExecutableDetailsLink);


		FileFieldEditor ExecutableToUse = fPrefUtils.makeNewFileField(
			page, this, fPrefService,
			"instance", "ExecutableToUse", "Executable to use",
			"The full path to the LPG generator executable",
			parent,
			true, true,
			true, "",
			true);
		fields.add(ExecutableToUse);

		Link ExecutableToUseDetailsLink = fPrefUtils.createDetailsLink(parent, ExecutableToUse, ExecutableToUse.getTextControl().getParent(), "Details ...");

		ExecutableToUseDetailsLink.setEnabled(true);
		fDetailsLinks.add(ExecutableToUseDetailsLink);


		fPrefUtils.createToggleFieldListener(UseDefaultExecutable, ExecutableToUse, false);
		boolean isEnabledExecutableToUse = !UseDefaultExecutable.getBooleanValue();
		ExecutableToUse.getTextControl().setEditable(isEnabledExecutableToUse);
		ExecutableToUse.getTextControl().setEnabled(isEnabledExecutableToUse);
		ExecutableToUse.setEnabled(isEnabledExecutableToUse, ExecutableToUse.getParent());


		BooleanFieldEditor UseDefaultIncludePath = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "UseDefaultIncludePath", "Use default include path",
			"",
			parent,
			true, true,
			false, false,
			true);
		fields.add(UseDefaultIncludePath);

		Link UseDefaultIncludePathDetailsLink = fPrefUtils.createDetailsLink(parent, UseDefaultIncludePath, UseDefaultIncludePath.getChangeControl().getParent(), "Details ...");

		UseDefaultIncludePathDetailsLink.setEnabled(true);
		fDetailsLinks.add(UseDefaultIncludePathDetailsLink);


		DirectoryListFieldEditor IncludePathToUse = fPrefUtils.makeNewDirectoryListField(
			page, this, fPrefService,
			"instance", "IncludePathToUse", "Include path to use",
			"A semicolon-separated list of folders to search for template and include files",
			parent,
			true, true,
			true, "",
			true);
		fields.add(IncludePathToUse);

		Link IncludePathToUseDetailsLink = fPrefUtils.createDetailsLink(parent, IncludePathToUse, IncludePathToUse.getTextControl().getParent(), "Details ...");

		IncludePathToUseDetailsLink.setEnabled(true);
		fDetailsLinks.add(IncludePathToUseDetailsLink);


		fPrefUtils.createToggleFieldListener(UseDefaultIncludePath, IncludePathToUse, false);
		boolean isEnabledIncludePathToUse = !UseDefaultIncludePath.getBooleanValue();
		IncludePathToUse.getTextControl().setEditable(isEnabledIncludePathToUse);
		IncludePathToUse.getTextControl().setEnabled(isEnabledIncludePathToUse);
		IncludePathToUse.setEnabled(isEnabledIncludePathToUse, IncludePathToUse.getParent());


		StringFieldEditor SourceFileExtensions = fPrefUtils.makeNewStringField(
			page, this, fPrefService,
			"instance", "SourceFileExtensions", "Source file extensions",
			"A comma-separated list of file name extensions identifying top-level LPG grammar files",
			parent,
			true, true,
			true, "",
			true);
		fields.add(SourceFileExtensions);

		Link SourceFileExtensionsDetailsLink = fPrefUtils.createDetailsLink(parent, SourceFileExtensions, SourceFileExtensions.getTextControl().getParent(), "Details ...");

		SourceFileExtensionsDetailsLink.setEnabled(true);
		fDetailsLinks.add(SourceFileExtensionsDetailsLink);


		StringFieldEditor IncludeFileExtensions = fPrefUtils.makeNewStringField(
			page, this, fPrefService,
			"instance", "IncludeFileExtensions", "Include file extensions",
			"A comma-separated list of file name extensions identifying included LPG grammar files",
			parent,
			true, true,
			true, "",
			true);
		fields.add(IncludeFileExtensions);

		Link IncludeFileExtensionsDetailsLink = fPrefUtils.createDetailsLink(parent, IncludeFileExtensions, IncludeFileExtensions.getTextControl().getParent(), "Details ...");

		IncludeFileExtensionsDetailsLink.setEnabled(true);
		fDetailsLinks.add(IncludeFileExtensionsDetailsLink);


		BooleanFieldEditor EmitDiagnostics = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "EmitDiagnostics", "Emit diagnostics",
			"If true, emit messages to the LPG plugin log as the build proceeds",
			parent,
			true, true,
			false, false,
			true);
		fields.add(EmitDiagnostics);

		Link EmitDiagnosticsDetailsLink = fPrefUtils.createDetailsLink(parent, EmitDiagnostics, EmitDiagnostics.getChangeControl().getParent(), "Details ...");

		EmitDiagnosticsDetailsLink.setEnabled(true);
		fDetailsLinks.add(EmitDiagnosticsDetailsLink);


		BooleanFieldEditor GenerateListings = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "GenerateListings", "Generate listings",
			"If true, place detailed information about each grammar file in a corresponding listing file",
			parent,
			true, true,
			false, false,
			true);
		fields.add(GenerateListings);

		Link GenerateListingsDetailsLink = fPrefUtils.createDetailsLink(parent, GenerateListings, GenerateListings.getChangeControl().getParent(), "Details ...");

		GenerateListingsDetailsLink.setEnabled(true);
		fDetailsLinks.add(GenerateListingsDetailsLink);


		BooleanFieldEditor QuietOutput = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "QuietOutput", "Quiet output",
			"If true, suppress the output of various facts like the number of terminals, lookahead, etc.",
			parent,
			true, true,
			false, false,
			true);
		fields.add(QuietOutput);

		Link QuietOutputDetailsLink = fPrefUtils.createDetailsLink(parent, QuietOutput, QuietOutput.getChangeControl().getParent(), "Details ...");

		QuietOutputDetailsLink.setEnabled(true);
		fDetailsLinks.add(QuietOutputDetailsLink);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
