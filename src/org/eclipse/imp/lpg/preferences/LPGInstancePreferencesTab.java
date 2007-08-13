package org.eclipse.imp.lpg.preferences;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.InstancePreferencesTab;
import org.eclipse.imp.preferences.PreferencesUtilities;
import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.imp.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.preferences.fields.DirectoryListFieldEditor;
import org.eclipse.imp.preferences.fields.FileFieldEditor;
import org.eclipse.imp.preferences.fields.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

public class LPGInstancePreferencesTab extends InstancePreferencesTab {

	
	public LPGInstancePreferencesTab(IPreferencesService prefService) {
		super(prefService);
	}

	
	/**
	 * To create specific preferences fields.
	 */
	protected FieldEditor[] createFields(Composite composite) {
		
		BooleanFieldEditor useDefaultExecField = null;
		FileFieldEditor execField = null;
		BooleanFieldEditor useDefaultGenIncludePathField = null;
		DirectoryListFieldEditor includeDirectoriesField = null;
		StringFieldEditor extensionsToProcessField = null;
		StringFieldEditor extensionsToIncludeField = null;
		BooleanFieldEditor emitDiagnosticMessagesField = null;
		BooleanFieldEditor generateListingsField = null;
		
		
		/*
		 * FYI:  The parameters to makeNew*Field following the "composite" parameter"
		 *	boolean isEnabled,	
		 *	boolean hasSpecialValue, String specialValue,
		 *	boolean emptyValueAllowed, String emptyValue,
		 *	boolean isRemovable
		 */
		
		// Used in setting enabled and editable status
		boolean value = false;
		
		useDefaultExecField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_USE_DEFAULT_EXEC, "Use default generator executable?",
				composite, true, true, true, PreferenceInitializer.getDefaultUseDefaultExecutable(), false, false, true);
		Link useDefaultExecDetails = prefUtils.createDetailsLink(composite, useDefaultExecField, useDefaultExecField.getChangeControl().getParent(), "Details ...");

		execField = prefUtils.makeNewFileField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_LPG_EXEC_PATH, "Generator executable",
				composite, false, false, true, PreferenceInitializer.getDefaultExecutablePath(), false, "", true);
		Link execFieldDetails = prefUtils.createDetailsLink(composite, execField, execField.getTextControl().getParent(), "Details ...");

		prefUtils.createToggleFieldListener(useDefaultExecField, execField, false);
		value = !useDefaultExecField.getBooleanValue();
		execField.getTextControl().setEditable(value);
		execField.getTextControl().setEnabled(value);
		execField.setEnabled(value, execField.getParent());
		
		PreferencesUtilities.fillGridPlace(composite, 2);	
		
		
		useDefaultGenIncludePathField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR, "Use default generator include path?",
				composite, true, true, true, PreferenceInitializer.getDefaultUseDefaultIncludeDirs(), false, false, true);
		Link useDefaultGenIncludePathFieldDetails = prefUtils.createDetailsLink(composite, useDefaultGenIncludePathField, useDefaultGenIncludePathField.getChangeControl().getParent(), "Details ...");
	
		includeDirectoriesField = prefUtils.makeNewDirectoryListField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_LPG_INCLUDE_DIRS, "Include directories:",
				composite, false, false, true, PreferenceInitializer.getDefaultIncludePath(), false, "", true);
		Link includeDirectoriesFieldDetails = prefUtils.createDetailsLink(composite, includeDirectoriesField, includeDirectoriesField.getTextControl().getParent(), "Details ...");
		
		prefUtils.createToggleFieldListener(useDefaultGenIncludePathField, includeDirectoriesField, false);
		value = !useDefaultGenIncludePathField.getBooleanValue();
		includeDirectoriesField.getTextControl().setEditable(value);
		includeDirectoriesField.getTextControl().setEnabled(value);
		includeDirectoriesField.setEnabled(value, includeDirectoriesField.getParent());	;
		
		PreferencesUtilities.fillGridPlace(composite, 2);	
		
		extensionsToProcessField = prefUtils.makeNewStringField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_EXTENSION_LIST, "File-name extensions to process:",
				composite, true, true, true, PreferenceInitializer.getDefaultExtensionList(), false, "", true);
		Link extensionsToProcessFieldDetails = prefUtils.createDetailsLink(composite, extensionsToProcessField, extensionsToProcessField.getTextControl().getParent(), "Details ...");

		extensionsToIncludeField = prefUtils.makeNewStringField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_NON_ROOT_EXTENSION_LIST, "File-name extensions for include files:",
				composite, true, true, true, PreferenceInitializer.getDefaultNonRootExtensionList(), false, "", true);
		Link extensionsToIncludeFieldDetails = prefUtils.createDetailsLink(composite, extensionsToIncludeField, extensionsToIncludeField.getTextControl().getParent(), "Details ...");

		emitDiagnosticMessagesField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_EMIT_MESSAGES, "Emit diagnostic messages during build?",
				composite, true, true, true, PreferenceInitializer.getDefaultEmitMessages(), false, false, true);
		Link emitDiagnosticMessagesFieldDetails = prefUtils.createDetailsLink(composite, emitDiagnosticMessagesField, emitDiagnosticMessagesField.getChangeControl().getParent(), "Details ...");
		
		generateListingsField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.INSTANCE_LEVEL, PreferenceConstants.P_GEN_LISTINGS, "Generate listing files?",
				composite, true, true, true, PreferenceInitializer.getDefaultGenerateListings(), false, false, true);
		Link generateListingsFieldDetails = prefUtils.createDetailsLink(composite, generateListingsField, generateListingsField.getChangeControl().getParent(), "Details ...");
		
		FieldEditor fields[] = new FieldEditor[8];
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
