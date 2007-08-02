package org.eclipse.imp.lpg.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class LSearchContentProvider implements IStructuredContentProvider {
    protected static final Object[] EMPTY_ARR= new Object[0];

    protected LSearchResult fResult;

    protected LSearchResultPage fPage;

    public LSearchContentProvider(LSearchResultPage page) {
        fPage= page;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        initialize((LSearchResult) newInput);
    }

    protected void initialize(LSearchResult result) {
        fResult= result;
    }

    public abstract void elementsChanged(Object[] updatedElements);

    public abstract void clear();

    public void dispose() {
        // nothing to do
    }

    protected LSearchResultPage getPage() {
        return fPage;
    }
}
