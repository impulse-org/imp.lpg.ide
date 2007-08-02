package org.eclipse.imp.lpg.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.lpg.builder.LPGNature;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class EnableBuilder implements IWorkbenchWindowActionDelegate {
    private IProject fProject;

    public EnableBuilder() {}

    public void dispose() {}

    public void init(IWorkbenchWindow window) {}

    public void run(IAction action) {
	new LPGNature().addToProject(fProject);
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
