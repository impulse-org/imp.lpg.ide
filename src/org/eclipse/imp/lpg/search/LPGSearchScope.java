package org.eclipse.imp.lpg.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class LSearchScope {
    Set fProjects= new HashSet();

    private LSearchScope() { }

    private LSearchScope(IProject p) {
        fProjects.add(p);
    }

    public void addProject(IProject project) {
        fProjects.add(project);
    }

    public Set/*<IProject>*/ getProjects() {
        return fProjects;
    }

    public static LSearchScope createWorkspaceScope() {
        LSearchScope scope= new LSearchScope();
        IProject[] projects= ResourcesPlugin.getWorkspace().getRoot().getProjects();

        for(int i= 0; i < projects.length; i++) {
            scope.addProject(projects[i]);
        }
        return scope;
    }

    public static LSearchScope createProjectScope(IProject project) {
        return new LSearchScope(project);
    }
}