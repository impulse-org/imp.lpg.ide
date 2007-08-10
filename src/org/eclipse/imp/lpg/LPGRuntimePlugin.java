package org.eclipse.imp.lpg;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.lpg.preferences.PreferenceConstants;
import org.eclipse.imp.lpg.preferences.PreferenceInitializer;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.IFactoryExtender;
import org.eclipse.imp.preferences.SafariPreferencesService;
import org.eclipse.imp.runtime.SAFARIPluginBase;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class LPGRuntimePlugin extends SAFARIPluginBase {
    public static final String kPluginID= "org.eclipse.imp.lpg.runtime";

    /**
     * The unique instance of this plugin class
     */
    protected static LPGRuntimePlugin sPlugin;
    
    // SMS 8 Sep 2006
    protected static SafariPreferencesService preferencesService = null;

    public static LPGRuntimePlugin getInstance() {
        return sPlugin;
    }

    public LPGRuntimePlugin() {
        super();
        sPlugin= this;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);

        // Initialize the JikesPGPreferences fields with the preference store data.
        if (preferencesService == null) {
        	preferencesService = new SafariPreferencesService();
        	preferencesService.setLanguageName("jikespg");
        	(new PreferenceInitializer()).initializeDefaultPreferences();
        }

        ModelFactory.getInstance().installExtender(new IFactoryExtender() {
	    public void extend(ISourceProject project) {
		IPreferenceStore store= LPGRuntimePlugin.getInstance().getPreferenceStore();
		IPath includeDir = new Path(store.getString(PreferenceConstants.P_JIKESPG_INCLUDE_DIRS));

		project.getBuildPath().add(ModelFactory.createPathEntry(IPathEntry.PathEntryType.SOURCE_FOLDER, includeDir));
	    }
	    public void extend(ICompilationUnit unit) { }
	  }, LanguageRegistry.findLanguage("jikespg"));
        // SMS 8 Sep 2006 updated 30 Oct 2006
        // Not sure why the field fEmitInfoMessages is used in preference to the preference cache
        // (ask Bob F. about that).  If it is going to continue to be used, then it should probably
        // be initialized from the new preferences store instead of the deprecated preferences
        // cache.  (The field would seem to represent a sort of separate, single-value cache.)
        // Presently it doesn't actually seem to be used anywhere, at least within the JikesPG
        // UIDE, suggesting that it could safely be removed, at least insofar as JikesPG is concerned.
        // Potential users of the field would have to get the value from the preferences store,
        // but since the value is a preference, that doesn't seem inappropriate.
        //fEmitInfoMessages= JikesPGPreferenceCache.builderEmitMessages;
        fEmitInfoMessages = preferencesService.getBooleanPreference(PreferenceConstants.P_EMIT_MESSAGES);
    }

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
        URL url= Platform.find(bundle, path);
        if (url != null) {
            return ImageDescriptor.createFromURL(url);
        }
        return null;
    }

    public String getID() {
        return kPluginID;
    }
    
    // SMS 8 Sep 2006
    //private final static IPreferencesService preferencesService = Platform.getPreferencesService();
    public static SafariPreferencesService getPreferencesService() {
    	if (preferencesService == null) {
    		preferencesService = new SafariPreferencesService();
        	preferencesService.setLanguageName("jikespg");
           	(new PreferenceInitializer()).initializeDefaultPreferences();
    	}
    	return preferencesService;
    }
    
    
    // SMS 30 Oct 2006
    // Overwriting method in SAFAIPluginBase because at that level we don't have
    // a preferences service to query dynamically, only a field set from this level
    // at the time of preference initialization
    public void maybeWriteInfoMsg(String msg) {
        //if (!fEmitInfoMessages)
        if (!preferencesService.getBooleanPreference(PreferenceConstants.P_EMIT_MESSAGES))
            return;
        writeInfoMsg(msg);
    }

    
}
