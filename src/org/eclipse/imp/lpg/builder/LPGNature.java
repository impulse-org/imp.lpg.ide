/*
 * Created on Nov 1, 2005
 */
package org.eclipse.safari.jikespg.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.safari.jikespg.JikesPGRuntimePlugin;
import org.eclipse.uide.core.ProjectNatureBase;
import org.eclipse.uide.runtime.IPluginLog;

import com.ibm.watson.smapifier.builder.SmapiProjectNature;

public class JikesPGNature extends ProjectNatureBase {
    public static final String	k_natureID = JikesPGRuntimePlugin.kPluginID + ".jikesPGNature";

    public String getNatureID() {
	return k_natureID;
    }

    public String getBuilderID() {
	return JikesPGBuilder.BUILDER_ID;
    }

    public void addToProject(IProject project) {
        super.addToProject(project);
        new SmapiProjectNature("g").addToProject(project);
    }

    protected void refreshPrefs() {
	// TODO implement preferences and hook in here
    }

    public IPluginLog getLog() {
	return JikesPGRuntimePlugin.getInstance();
    }

    protected String getDownstreamBuilderID() {
	return "org.eclipse.jdt.core.javabuilder";
    }
}
