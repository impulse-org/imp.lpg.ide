/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.imp.lpg.actions;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.docBuilder.LPGDocBuilder;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class EnableDocBuilder implements IWorkbenchWindowActionDelegate {
    private IProject fProject;

    public EnableDocBuilder() { }

    public void dispose() { }

    public void init(IWorkbenchWindow window) { }

    public void run(IAction action) {
        try {
            IProjectDescription projDesc= fProject.getDescription();
            ICommand[] buildCmds= projDesc.getBuildSpec();

            // Check: is the builder already in this project?
            for(int i=0; i < buildCmds.length; i++) {
                if (buildCmds[i].getBuilderName().equals(LPGDocBuilder.BUILDER_ID))
                    return; // relevant command is already in there...
            }
            ICommand compilerCmd= projDesc.newCommand();

            compilerCmd.setBuilderName(LPGDocBuilder.BUILDER_ID);
            ICommand[] newCmds= new ICommand[buildCmds.length+1];

            System.arraycopy(buildCmds, 0, newCmds, 0, buildCmds.length);
            newCmds[buildCmds.length] = compilerCmd;
            projDesc.setBuildSpec(newCmds);
            fProject.setDescription(projDesc, null);
        } catch (CoreException e) {
            LPGRuntimePlugin.getInstance().logException("Error adding LPG Doc Builder to project description", e);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss= (IStructuredSelection) selection;
            Object first= ss.getFirstElement();

            if (first instanceof IProject) {
                fProject= (IProject) first;
            } else if (first instanceof IJavaProject) {
                fProject= ((IJavaProject) first).getProject();
            }
        }
    }
}
