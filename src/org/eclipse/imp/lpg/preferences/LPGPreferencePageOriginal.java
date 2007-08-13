package org.eclipse.imp.lpg.preferences;

import java.util.HashSet;

import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.builder.LPGBuilder;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into
 * JFace that allows us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */
public class LPGPreferencePageOriginal extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    private FileFieldEditor fExecField;
    private DirectoryListFieldEditor fTemplateField;

    public LPGPreferencePageOriginal() {
	super(GRID);
	setPreferenceStore(LPGRuntimePlugin.getInstance().getPreferenceStore());
	setDescription("Preferences for the LPG parser/lexer generator");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks
     * needed to manipulate various types of preferences. Each field editor knows how to
     * save and restore itself.
     */
    public void createFieldEditors() {
	final BooleanFieldEditor useDefaultExecField= new BooleanFieldEditorExtraListener(PreferenceConstants.P_USE_DEFAULT_EXEC, "Use default generator executable", getFieldEditorParent(),
	    new IPropertyChangeListener() {
	    	public void propertyChange(PropertyChangeEvent event) {
	    	    if (event.getNewValue().equals(Boolean.TRUE)) {
	    		fExecField.setEnabled(false, LPGPreferencePageOriginal.this.getFieldEditorParent());
			fExecField.setStringValue(LPGBuilder.getDefaultExecutablePath());
	    	    } else
	    		fExecField.setEnabled(true, LPGPreferencePageOriginal.this.getFieldEditorParent());
	    	}
	    });
	fExecField= new FileFieldEditor(PreferenceConstants.P_LPG_EXEC_PATH, "Generator e&xecutable:", getFieldEditorParent());

	final BooleanFieldEditor useDefaultTemplateField= new BooleanFieldEditorExtraListener(PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR, "Use default generator include path", getFieldEditorParent(),
	    new IPropertyChangeListener() {
	    	public void propertyChange(PropertyChangeEvent event) {
	    	    if (event.getNewValue().equals(Boolean.TRUE)) {
	    		fTemplateField.setEnabled(false, LPGPreferencePageOriginal.this.getFieldEditorParent());
			fTemplateField.setStringValue(LPGBuilder.getDefaultIncludePath());
	    	    } else
	    		fTemplateField.setEnabled(true, LPGPreferencePageOriginal.this.getFieldEditorParent());
	    	}
	    });
	fTemplateField= new DirectoryListFieldEditor(PreferenceConstants.P_LPG_INCLUDE_DIRS, "&Include directories:", getFieldEditorParent());

	addField(useDefaultExecField);
	addField(fExecField);
	addField(useDefaultTemplateField);
	addField(fTemplateField);
	addField(new StringFieldEditor(PreferenceConstants.P_EXTENSION_LIST, "File-name &extensions to process:",
		getFieldEditorParent()));
	addField(new StringFieldEditor(PreferenceConstants.P_NON_ROOT_EXTENSION_LIST, "File-name extensions for in&cluded files:",
		getFieldEditorParent()));
	addField(new BooleanFieldEditor(PreferenceConstants.P_EMIT_MESSAGES, "Emit &diagnostic messages during build",
		getFieldEditorParent()));
	addField(new BooleanFieldEditor(PreferenceConstants.P_GEN_LISTINGS, "Generate &listing files",
		getFieldEditorParent()));

//	addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE, "An example of a multiple-choice preference", 1,
//		new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));

	if (LPGPreferenceCache.useDefaultExecutable)
	    fExecField.setEnabled(false, getFieldEditorParent());
	if (LPGPreferenceCache.useDefaultIncludeDir)
	    fTemplateField.setEnabled(false, getFieldEditorParent());

	getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.P_EMIT_MESSAGES))
		    LPGPreferenceCache.builderEmitMessages= ((Boolean) event.getNewValue()).booleanValue();
		else if (event.getProperty().equals(PreferenceConstants.P_GEN_LISTINGS))
		    LPGPreferenceCache.generateListing= ((Boolean) event.getNewValue()).booleanValue();
		else if (event.getProperty().equals(PreferenceConstants.P_LPG_EXEC_PATH))
		    LPGPreferenceCache.jikesPGExecutableFile= (String) event.getNewValue();
		else if (event.getProperty().equals(PreferenceConstants.P_LPG_INCLUDE_DIRS))
		    LPGPreferenceCache.jikesPGIncludeDirs= (String) event.getNewValue();
		else if (event.getProperty().equals(PreferenceConstants.P_USE_DEFAULT_EXEC)) {
		    LPGPreferenceCache.useDefaultExecutable= ((Boolean) event.getNewValue()).booleanValue();
		    if (event.getNewValue().equals(Boolean.TRUE)) {
			fExecField.setEnabled(false, getFieldEditorParent());
			fExecField.setStringValue(LPGBuilder.getDefaultExecutablePath());
		    } else {
			fExecField.setEnabled(true, getFieldEditorParent());
		    }
		} else if (event.getProperty().equals(PreferenceConstants.P_USE_DEFAULT_INCLUDE_DIR)) {
		    LPGPreferenceCache.useDefaultIncludeDir= ((Boolean) event.getNewValue()).booleanValue();
		    if (event.getNewValue().equals(Boolean.TRUE)) {
			fTemplateField.setEnabled(false, getFieldEditorParent());
			fTemplateField.setStringValue(LPGBuilder.getDefaultIncludePath());
		    } else {
			fTemplateField.setEnabled(true, getFieldEditorParent());
		    }
		} else if (event.getProperty().equals(PreferenceConstants.P_EXTENSION_LIST)) {
		    LPGPreferenceCache.rootExtensionList= new HashSet();
		    String[] extens= ((String) event.getNewValue()).split(",");
		    for(int i= 0; i < extens.length; i++) {
			LPGPreferenceCache.rootExtensionList.add(extens[i]);
		    }
		} else if (event.getProperty().equals(PreferenceConstants.P_NON_ROOT_EXTENSION_LIST)) {
		    LPGPreferenceCache.nonRootExtensionList= new HashSet();
		    String[] extens= ((String) event.getNewValue()).split(",");
		    for(int i= 0; i < extens.length; i++) {
			LPGPreferenceCache.nonRootExtensionList.add(extens[i]);
		    }
		}
	    }
	});
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {}
}
