package org.eclipse.imp.lpg.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.ProjectPreferencesTab;
import org.eclipse.imp.preferences.PreferencesUtilities;
import org.eclipse.imp.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.preferences.fields.DirectoryListFieldEditor;
import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.imp.preferences.fields.FileFieldEditor;
import org.eclipse.imp.preferences.fields.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.osgi.service.prefs.Preferences;

public class LPGProjectPreferencesTab extends ProjectPreferencesTab {

	/*
	 * In contrast to the other tabs, with the project tab the
	 * field editors have to be shared between two methods, so
	 * they should be declared outside of both.
	 */
	BooleanFieldEditor useDefaultExecField = null;
	FileFieldEditor execField = null;
	BooleanFieldEditor useDefaultGenIncludePathField = null;
	DirectoryListFieldEditor includeDirectoriesField = null;
	StringFieldEditor extensionsToProcessField = null;
	StringFieldEditor extensionsToIncludeField = null;
	BooleanFieldEditor emitDiagnosticMessagesField = null;
	BooleanFieldEditor generateListingsField = null;

	
	
	public LPGProjectPreferencesTab(IPreferencesService prefService) {
		super(prefService);
	}

	
	
	/**
	 * To create specific preferences fields.
	 */
	protected FieldEditor[] createFields(Composite composite) {
		
		/*
		 * FYI:  The parameters to makeNewStringField following the "composite" parameter
		 *	boolean isEnabled, isEditable
		 *	boolean hasSpecialValue, String specialValue,
		 *	boolean emptyValueAllowed, String emptyValue,
		 *	boolean isRemovable
		 */
		
		
		// Used in setting enabled and editable status
		boolean value = false;
	
		useDefaultExecField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_USE_DEFAULT_EXEC, "Use default generator executable?",
				composite, false, true, true, PreferenceInitializer.getDefaultUseDefaultExecutable(), false, false, true);
		Link useDefaultExecFieldDetails = prefUtils.createDetailsLink(composite, useDefaultExecField, useDefaultExecField.getChangeControl().getParent(), "Details ...");
		detailsLinks.add(useDefaultExecFieldDetails);
	
		execField = prefUtils.makeNewFileField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_JIKESPG_EXEC_PATH, "Generator executable",
				composite, false, false, true, PreferenceInitializer.getDefaultExecutablePath(), false, "", true);
		Link execFieldDetails = prefUtils.createDetailsLink(composite, execField, execField.getTextControl().getParent(), "Details ...");
		detailsLinks.add(execFieldDetails);

		prefUtils.createToggleFieldListener(useDefaultExecField, execField, false);
		// Initialize enabled state of field according to whether a project is defined (not likely here)
		// and the setting of the toggle control field (useDefaultExecField)
		value = prefService.getProject() != null && !useDefaultExecField.getBooleanValue();
		execField.getTextControl().setEditable(value);
		execField.getTextControl().setEnabled(value);
		execField.setEnabled(value, execField.getParent());
		
		PreferencesUtilities.fillGridPlace(composite, 2);	
		
		
		useDefaultGenIncludePathField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR, "Use default generator include path?",
				composite, false, true, true, PreferenceInitializer.getDefaultUseDefaultIncludeDirs(), false, false, true);
		Link useDefaultGenIncludePathFieldDetails = prefUtils.createDetailsLink(composite, useDefaultGenIncludePathField, useDefaultGenIncludePathField.getChangeControl().getParent(), "Details ...");
		detailsLinks.add(useDefaultGenIncludePathFieldDetails);
	
		includeDirectoriesField = prefUtils.makeNewDirectoryListField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_JIKESPG_INCLUDE_DIRS, "Include directories:",
				composite, false, false, true, PreferenceInitializer.getDefaultIncludePath(), false, "", true);
		Link includeDirectoriesFieldDetails = prefUtils.createDetailsLink(composite, includeDirectoriesField, includeDirectoriesField.getTextControl().getParent(), "Details ...");
		detailsLinks.add(includeDirectoriesFieldDetails);

		prefUtils.createToggleFieldListener(useDefaultGenIncludePathField, includeDirectoriesField, false);
		// Initialize enabled state of field according to whether a project is defined (not likely here)
		// and the setting of the toggle control field (useDefaultGenIncludePathField)
		value = prefService.getProject() != null && !useDefaultGenIncludePathField.getBooleanValue();
		includeDirectoriesField.getTextControl().setEditable(value);
		includeDirectoriesField.getTextControl().setEnabled(value);
		includeDirectoriesField.setEnabled(value, includeDirectoriesField.getParent());

		
		PreferencesUtilities.fillGridPlace(composite, 2);	

		
		extensionsToProcessField = prefUtils.makeNewStringField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_EXTENSION_LIST, "File-name extensions to process:",
				composite, false, false, true, PreferenceInitializer.getDefaultExtensionList(), false, "", true);
		Link extensionsToProcessFieldDetails = prefUtils.createDetailsLink(composite, extensionsToProcessField, extensionsToProcessField.getTextControl().getParent(), "Details ...");
		detailsLinks.add(extensionsToProcessFieldDetails);
		
		extensionsToIncludeField = prefUtils.makeNewStringField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_NON_ROOT_EXTENSION_LIST, "File-name extensions for include files:",
				composite, false, false, true, PreferenceInitializer.getDefaultNonRootExtensionList(), false, "", true);
		Link extensionsToIncludeFieldDetails = prefUtils.createDetailsLink(composite, extensionsToIncludeField, extensionsToIncludeField.getTextControl().getParent(), "Details ...");
		detailsLinks.add(extensionsToIncludeFieldDetails);
		
		emitDiagnosticMessagesField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_EMIT_MESSAGES, "Emit diagnostic messages during build?",
				composite, false, false, true, PreferenceInitializer.getDefaultEmitMessages(), false, false, true);
		Link emitDiagnosticMessagesFieldDetails = prefUtils.createDetailsLink(composite, emitDiagnosticMessagesField, emitDiagnosticMessagesField.getChangeControl().getParent(), "Details ...");
		detailsLinks.add(emitDiagnosticMessagesFieldDetails);
		
		generateListingsField = prefUtils.makeNewBooleanField(
				prefPage, this,
				prefService, IPreferencesService.PROJECT_LEVEL, PreferenceConstants.P_GEN_LISTINGS, "Generate listing files?",
				composite, false, false, true, PreferenceInitializer.getDefaultGenerateListings(), false, false, true);
		Link generateListingsFieldDetails = prefUtils.createDetailsLink(composite, generateListingsField, generateListingsField.getChangeControl().getParent(), "Details ...");
		detailsLinks.add(generateListingsFieldDetails);

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
	
	
	public void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite)
	{
		boolean haveCurrentListeners = false;
		
		Preferences oldeNode = event.getPrevious();
		Preferences newNode = event.getNew();
		
		if (oldeNode == null && newNode == null) {
			// This is what happens for some reason when you clear the project selection,
			// but there shouldn't be anything to do (I don't think), because newNode == null
			// implies that the preferences should be cleared, disabled, etc., and oldeNode
			// ==  null implies that they should already be cleared, disabled, etc.
			// So, it should be okay to return from here ...
			return;
		}
		//System.err.println("SafariProjectSelectionListener.selection:  old node = " + (oldeNode == null ? "null" : "not null"));
		//System.err.println("SafariProjectSelectionListener.selection:  new node = " + (newNode == null ? "null" : "not null"));
		
		
		// If oldeNode is not null, we want to remove any preference-change listeners from it
		if (oldeNode != null && oldeNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {	
			//System.out.println("JsdivProjectPreferencesPage.SafariProjectSelectionListener.selection():  " +
			//	"\n\tnode is null, not of IEclipsePreferences type, or currentListener is null; not possible to add preference-change listener");
		}

		
		// Declare these for various manipulations of fields and controls to follow
		Composite useDefaultExecFieldHolder	= null;
		Composite execFieldHolder = null;
		Composite useDefaultGenIncludePathFieldHolder = null;
		Composite includeDirectoriesFieldHolder = null;
		Composite extensionsToProcessFieldHolder = null;
		Composite extensionsToIncludeFieldHolder = null;
		Composite emitDiagnosticMessagesFieldHolder = null;
		Composite generateListingsFieldHolder = null;

		
		// If we have a new project preferences node, then do various things
		// to set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
			// Set project name in the selected-project field
			selectedProjectName.setStringValue(newNode.name());
			
			// If the containing composite is not disposed, then set the fields'
			// values and make them enabled and editable (as appropriate to the
			// type of field)

			// Not entirely sure why the composite could or should be disposed if we're here,
			// but it happens sometimes when selecting a project for the second time, i.e.,
			// after having selected a project once, clicked "OK", and then brought up the
			// dialog and selected a project again.  PERHAPS there is a race condition, such
			// that sometimes the project-selection dialog is still overlaying the preferences
			// tab at the time that the listeners try to update the tab.  If the project-selection
			// dialog were still up then the preferences tab would be disposed.
			
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
				
				// Used in setting enabled and editable status
				boolean value = false;
				
				useDefaultExecFieldHolder = useDefaultExecField.getChangeControl().getParent();
				prefUtils.setField(useDefaultExecField, useDefaultExecFieldHolder);
				useDefaultExecField.getChangeControl().setEnabled(true);
				
				execFieldHolder = execField.getTextControl().getParent();
				prefUtils.setField(execField, execFieldHolder);
				value = !useDefaultExecField.getBooleanValue();
				execField.getTextControl().setEditable(value);
				execField.getTextControl().setEnabled(value);
				execField.setEnabled(value, execField.getParent());
	
				useDefaultGenIncludePathFieldHolder = useDefaultGenIncludePathField.getChangeControl().getParent();
				prefUtils.setField(useDefaultGenIncludePathField, useDefaultGenIncludePathFieldHolder);
				useDefaultGenIncludePathField.getChangeControl().setEnabled(true);

				includeDirectoriesFieldHolder = includeDirectoriesField.getTextControl().getParent();
				prefUtils.setField(includeDirectoriesField, includeDirectoriesFieldHolder);
				value = !useDefaultGenIncludePathField.getBooleanValue();
				includeDirectoriesField.getTextControl().setEditable(value);
				includeDirectoriesField.getTextControl().setEnabled(value);
				includeDirectoriesField.setEnabled(value, includeDirectoriesField.getParent());
				
				extensionsToProcessFieldHolder = extensionsToProcessField.getTextControl().getParent();
				prefUtils.setField(extensionsToProcessField, extensionsToProcessFieldHolder);
				extensionsToProcessField.getTextControl(extensionsToProcessFieldHolder).setEditable(true);
				extensionsToProcessField.getTextControl(extensionsToProcessFieldHolder).setEnabled(true);
				extensionsToProcessField.setEnabled(true, extensionsToProcessField.getParent());
				
				extensionsToIncludeFieldHolder = extensionsToIncludeField.getTextControl().getParent();
				prefUtils.setField(extensionsToIncludeField, extensionsToIncludeFieldHolder);
				extensionsToIncludeField.getTextControl(extensionsToIncludeFieldHolder).setEditable(true);
				extensionsToIncludeField.getTextControl(extensionsToIncludeFieldHolder).setEnabled(true);
				extensionsToIncludeField.setEnabled(true, extensionsToIncludeField.getParent());
				
				emitDiagnosticMessagesFieldHolder = emitDiagnosticMessagesField.getChangeControl().getParent();
				prefUtils.setField(emitDiagnosticMessagesField, emitDiagnosticMessagesFieldHolder);
				emitDiagnosticMessagesField.getChangeControl().setEnabled(true);
				
				generateListingsFieldHolder = generateListingsField.getChangeControl().getParent();
				prefUtils.setField(generateListingsField, generateListingsFieldHolder);
				generateListingsField.getChangeControl().setEnabled(true);

				clearModifiedMarksOnLabels();
				
			}
			

			// Add property change listeners
			// Example
			if (useDefaultExecFieldHolder != null) addProjectPreferenceChangeListeners(
					useDefaultExecField, PreferenceConstants.P_USE_DEFAULT_EXEC, useDefaultExecFieldHolder);
			if (execFieldHolder != null) addProjectPreferenceChangeListeners(execField, PreferenceConstants.P_JIKESPG_EXEC_PATH, execFieldHolder);
			if (useDefaultGenIncludePathFieldHolder != null) addProjectPreferenceChangeListeners(
					useDefaultGenIncludePathField, PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR, useDefaultGenIncludePathFieldHolder);
			if (includeDirectoriesFieldHolder != null) addProjectPreferenceChangeListeners(
					includeDirectoriesField, PreferenceConstants.P_JIKESPG_INCLUDE_DIRS	, includeDirectoriesFieldHolder);
			if (extensionsToProcessFieldHolder != null) addProjectPreferenceChangeListeners(
					extensionsToProcessField, PreferenceConstants.P_EXTENSION_LIST, extensionsToProcessFieldHolder);
			if (extensionsToIncludeFieldHolder != null) addProjectPreferenceChangeListeners(
					extensionsToIncludeField, PreferenceConstants.P_NON_ROOT_EXTENSION_LIST, extensionsToIncludeFieldHolder);
			if (emitDiagnosticMessagesFieldHolder != null) addProjectPreferenceChangeListeners(
					emitDiagnosticMessagesField, PreferenceConstants.P_EMIT_MESSAGES, emitDiagnosticMessagesFieldHolder);
			if (generateListingsFieldHolder != null) addProjectPreferenceChangeListeners(
					generateListingsField, PreferenceConstants.P_GEN_LISTINGS, generateListingsFieldHolder);

			haveCurrentListeners = true;
		}
		
		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected
			
			// Unset project name in the tab
			selectedProjectName.setStringValue("none selected");
			
			// Clear the preferences from the store
			prefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);
			
			// Disable fields and make them non-editable
			// Example:
			if (!composite.isDisposed()) {

					useDefaultExecField.getChangeControl().setEnabled(false);

					execField.getTextControl(execFieldHolder).setEnabled(false);
					execField.getTextControl(execFieldHolder).setEditable(false);

					useDefaultGenIncludePathField.getChangeControl().setEnabled(false);

					includeDirectoriesField.getTextControl(includeDirectoriesFieldHolder).setEnabled(false);
					includeDirectoriesField.getTextControl(includeDirectoriesFieldHolder).setEditable(false);

					extensionsToProcessField.getTextControl(extensionsToProcessFieldHolder).setEditable(false);
					extensionsToProcessField.getTextControl(extensionsToProcessFieldHolder).setEnabled(false);

					extensionsToIncludeField.getTextControl(extensionsToIncludeFieldHolder).setEditable(false);
					extensionsToIncludeField.getTextControl(extensionsToIncludeFieldHolder).setEnabled(false);

					emitDiagnosticMessagesField.getChangeControl().setEnabled(false);
					
					generateListingsField.getChangeControl().setEnabled(false);
			}
			
			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		}
	}
	
}
