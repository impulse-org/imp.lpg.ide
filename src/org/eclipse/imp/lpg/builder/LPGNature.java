/*
 * Created on Nov 1, 2005
 */
package org.eclipse.imp.lpg.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.core.ProjectNatureBase;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.runtime.IPluginLog;

import com.ibm.watson.smapifier.builder.SmapiProjectNature;

public class LPGNature extends ProjectNatureBase {
    public static final String	k_natureID = LPGRuntimePlugin.kPluginID + ".LPGNature";

    public String getNatureID() {
	return k_natureID;
    }

    public String getBuilderID() {
	return LPGBuilder.BUILDER_ID;
    }

    public void addToProject(IProject project) {
        super.addToProject(project);
        new SmapiProjectNature("g").addToProject(project);
    }

    protected void refreshPrefs() {
	// TODO implement preferences and hook in here
    }

    public IPluginLog getLog() {
	return LPGRuntimePlugin.getInstance();
    }

    protected String getDownstreamBuilderID() {
	return "org.eclipse.jdt.core.javabuilder";
    }
}
