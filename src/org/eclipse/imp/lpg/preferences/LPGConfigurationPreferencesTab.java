package org.eclipse.imp.lpg.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.imp.preferences.ConfigurationPreferencesTab;
import org.eclipse.imp.preferences.ISafariPreferencesService;
import org.eclipse.imp.preferences.SafariPreferencesUtilities;
import org.eclipse.imp.preferences.fields.SafariBooleanFieldEditor;
import org.eclipse.imp.preferences.fields.SafariDirectoryListFieldEditor;
import org.eclipse.imp.preferences.fields.SafariFieldEditor;
import org.eclipse.imp.preferences.fields.SafariFileFieldEditor;
import org.eclipse.imp.preferences.fields.SafariStringFieldEditor;

public class LConfigurationPreferencesTab extends ConfigurationPreferencesTab {
		
	
	public LConfigurationPreferencesTab(ISafariPreferencesService prefService) {
		super(prefService);
	}
	
	
	/**
	 * To create specific preferences fields.  At this level does nothing.
	 * Override in subclasses.
	 *
	 */
	protected SafariFieldEditor[] createFields(Composite composite) {
		
		// Example:  declare preference fields
		SafariBooleanFieldEditor useDefaultExecField = null;
		SafariFileFieldEditor execField = null;
		SafariBooleanFieldEditor useDefaultGenIncludePathField = null;
		SafariDirectoryListFieldEditor includeDirectoriesField = null;
		SafariStringFieldEditor extensionsToProcessField = null;
		SafariStringFieldEditor extensionsToIncludeField = null;
		SafariBooleanFieldEditor emitDiagnosticMessagesField = null;
		SafariBooleanFieldEditor generateListingsField = null;
		

		boolean value = false;
		
		useDefaultExecField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_USE_DEFAULT_EXEC, "Use default generator executable?",
				composite, true, true, true, PreferenceInitializer.getDefaultUseDefaultExecutable(), false, false, true);
		Link useDefaultExecDetails = prefUtils.createDetailsLink(composite, useDefaultExecField, useDefaultExecField.getChangeControl().getParent(), "Details ...");

		execField = prefUtils.makeNewFileField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_JIKESPG_EXEC_PATH, "Generator executable",
				composite, false, false, true, PreferenceInitializer.getDefaultExecutablePath(), false, "", true);
		Link execFieldDetails = prefUtils.createDetailsLink(composite, execField, execField.getTextControl().getParent(), "Details ...");

		prefUtils.createToggleFieldListener(useDefaultExecField, execField, false);
		value = !useDefaultExecField.getBooleanValue();
		execField.getTextControl().setEditable(value);
		execField.getTextControl().setEnabled(value);
		execField.setEnabled(value, execField.getParent());
				
		SafariPreferencesUtilities.fillGridPlace(composite, 2);
		
		useDefaultGenIncludePathField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR, "Use default generator include path?",
				composite, true, true, true, PreferenceInitializer.getDefaultUseDefaultIncludeDirs(), false, false, true);
		Link useDefaultGenIncludePathFieldDetails = prefUtils.createDetailsLink(composite, useDefaultGenIncludePathField, useDefaultGenIncludePathField.getChangeControl().getParent(), "Details ...");
		
		includeDirectoriesField = prefUtils.makeNewDirectoryListField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_JIKESPG_INCLUDE_DIRS, "Include directories:",
				composite, false, false, true, PreferenceInitializer.getDefaultIncludePath(), false, "", true);
		Link includeDirectoriesFieldDetails = prefUtils.createDetailsLink(composite, includeDirectoriesField, includeDirectoriesField.getTextControl().getParent(), "Details ...");

		prefUtils.createToggleFieldListener(useDefaultGenIncludePathField, includeDirectoriesField, false);
		value = !useDefaultGenIncludePathField.getBooleanValue();
		includeDirectoriesField.getTextControl().setEditable(value);
		includeDirectoriesField.getTextControl().setEnabled(value);
		includeDirectoriesField.setEnabled(value, includeDirectoriesField.getParent());
		
		SafariPreferencesUtilities.fillGridPlace(composite, 2);	
		
		extensionsToProcessField = prefUtils.makeNewStringField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_EXTENSION_LIST, "File-name extensions to process:",
				composite, true, true, true, PreferenceInitializer.getDefaultExtensionList(), false, "", true);
		Link extensionsToProcessFieldDetails = prefUtils.createDetailsLink(composite, extensionsToProcessField, extensionsToProcessField.getTextControl().getParent(), "Details ...");

		extensionsToIncludeField = prefUtils.makeNewStringField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_NON_ROOT_EXTENSION_LIST, "File-name extensions for include files:",
				composite, true, true, true, PreferenceInitializer.getDefaultNonRootExtensionList(), false, "", true);
		Link extensionsToIncludeFieldDetails = prefUtils.createDetailsLink(composite, extensionsToIncludeField, extensionsToIncludeField.getTextControl().getParent(), "Details ...");

		emitDiagnosticMessagesField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_EMIT_MESSAGES, "Emit diagnostic messages during build?",
				composite, true, true, true, PreferenceInitializer.getDefaultEmitMessages(), false, false, true);
		Link emitDiagnosticMessagesFieldDetails = prefUtils.createDetailsLink(composite, emitDiagnosticMessagesField, emitDiagnosticMessagesField.getChangeControl().getParent(), "Details ...");
		
		generateListingsField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, ISafariPreferencesService.CONFIGURATION_LEVEL, PreferenceConstants.P_GEN_LISTINGS, "Generate listing files?",
				composite, true, true, true, PreferenceInitializer.getDefaultGenerateListings(), false, false, true);
		Link generateListingsFieldDetails = prefUtils.createDetailsLink(composite, generateListingsField, generateListingsField.getChangeControl().getParent(), "Details ...");
		
	
		SafariFieldEditor fields[] = new SafariFieldEditor[8];
		fields[0] = useDefaultExecField;
		fields[1] = execField;
		fields[2] = useDefaultGenIncludePathField;
		fields[3] = includeDirectoriesField;
		fields[4] = extensionsToProcessField;
		fields[5] = extensionsToIncludeField;
		fields[6] = emitDiagnosticMessagesField;
		fields[7] = generateListingsField;
		
		return fields;

	}
	
	
	
	
}
