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

package org.eclipse.imp.lpg;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.lpg.preferences.LPGPreferencesDialogConstants;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.IFactoryExtender;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.utils.ExtensionFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class LPGRuntimePlugin extends PluginBase {
    public static final String kPluginID= "org.eclipse.imp.lpg.runtime";

    /**
     * The unique instance of this plugin class
     */
    protected static LPGRuntimePlugin sPlugin;

    protected static PreferencesService preferencesService = null;

    private static String kLanguageID;

    public static LPGRuntimePlugin getInstance() {
        return sPlugin;
    }

    public LPGRuntimePlugin() {
        super();
        sPlugin= this;
    }
    
      // SMS 27 Apr 2008
      // Commented out to replace with version back-ported from Eclipse 3.3 adaptation
      // Later:  uncommented
    public void start(BundleContext context) throws Exception {
        super.start(context);

        kLanguageID= ExtensionFactory.retrieveLanguageIdFromPlugin(kPluginID);

        // Initialize the LPGPreferences fields with the preference store data.
        if (preferencesService == null) {
        	preferencesService = getPreferencesService();
        }

        ModelFactory.getInstance().installExtender(
        	new IFactoryExtender() {
			    public void extend(ISourceProject project) {
				IPreferenceStore store= LPGRuntimePlugin.getInstance().getPreferenceStore();
				IPath includeDir = new Path(store.getString(LPGPreferencesDialogConstants.P_INCLUDEPATHTOUSE));
		
				project.getBuildPath().add(ModelFactory.createPathEntry(IPathEntry.PathEntryType.SOURCE_FOLDER, includeDir));
			    }
			    public void extend(ICompilationUnit unit) { }
			},
		    LanguageRegistry.findLanguage(kLanguageID));

        fEmitInfoMessages = preferencesService.getBooleanPreference(LPGPreferencesDialogConstants.P_EMITDIAGNOSTICS);
    }

    
    // SMS 27 Apr 2008
    // Version back-ported from adaptation for Eclipse 3.3
//    public void start(BundleContext context) throws Exception {
//        super.start(context);
//
//        try {
//        	fEmitInfoMessages = getPreferencesService().getBooleanPreference(LPGPreferencesDialogConstants.P_EMITDIAGNOSTICS);
//        } catch(IllegalArgumentException e) {
//        	// FIXME Preference not available, assuming yes
//        	fEmitInfoMessages = true;
//        }
//    }
    
    
    public static final IPath ICONS_PATH= new Path("icons/"); //$NON-NLS-1$

    protected void initializeImageRegistry(ImageRegistry reg) {
        IPath path= ICONS_PATH.append("default.gif");//$NON-NLS-1$
        ImageDescriptor imageDescriptor= createImageDescriptor(getInstance().getBundle(), path);
        reg.put(ILPGResources.DEFAULT_AST, imageDescriptor);

        path= ICONS_PATH.append("outline_item.gif");//$NON-NLS-1$
        imageDescriptor= createImageDescriptor(getInstance().getBundle(), path);
        reg.put(ILPGResources.OUTLINE_ITEM, imageDescriptor);

        path= ICONS_PATH.append("grammarfile.gif");//$NON-NLS-1$
        imageDescriptor= createImageDescriptor(getInstance().getBundle(), path);
        reg.put(ILPGResources.GRAMMAR_FILE, imageDescriptor);

        path= ICONS_PATH.append("grammarfile-warning.jpg");//$NON-NLS-1$
        imageDescriptor= createImageDescriptor(getInstance().getBundle(), path);
        reg.put(ILPGResources.GRAMMAR_FILE_WARNING, imageDescriptor);

        path= ICONS_PATH.append("grammarfile-error.jpg");//$NON-NLS-1$
        imageDescriptor= createImageDescriptor(getInstance().getBundle(), path);
        reg.put(ILPGResources.GRAMMAR_FILE_ERROR, imageDescriptor);
    }

    public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path) {
        URL url= FileLocator.find(bundle, path, null);
        if (url != null) {
            return ImageDescriptor.createFromURL(url);
        }
        return null;
    }

    public static String getLanguageID() {
        return kLanguageID;
    }

    public String getID() {
        return kPluginID;
    }
    
    public static PreferencesService getPreferencesService() {
    	if (preferencesService == null) {
        	preferencesService = new PreferencesService();
        	preferencesService.setLanguageName(kLanguageID);
        	
    		// To trigger the automatic invocation of the preferences initializer:
    		try {
    			new DefaultScope().getNode(kPluginID);
    		} catch (Exception e) {
    			// If this ever happens, it will probably be because the preferences
    			// and their initializer haven't been defined yet.  In that situation
    			// there's not really anything to do--you can't initialize preferences
    			// that don't exist.  So swallow the exception and continue ...
    		}
    	}
    	return preferencesService;
    }

    // Overwriting method in PluginBase because at that level we don't have
    // a preferences service to query dynamically, only a field set from this level
    // at the time of preference initialization
    public void maybeWriteInfoMsg(String msg) {
        if (!preferencesService.getBooleanPreference(LPGPreferencesDialogConstants.P_EMITDIAGNOSTICS))
            return;
        writeInfoMsg(msg);
    }
}
