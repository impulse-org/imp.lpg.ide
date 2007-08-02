package org.eclipse.imp.lpg.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class LPGSearchContentProvider implements IStructuredContentProvider {
    protected static final Object[] EMPTY_ARR= new Object[0];

    protected LPGSearchResult fResult;

    protected LPGSearchResultPage fPage;

    public LPGSearchContentProvider(LPGSearchResultPage page) {
        fPage= page;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        initialize((LPGSearchResult) newInput);
    }

    protected void initialize(LPGSearchResult result) {
        fResult= result;
    }

    public abstract void elementsChanged(Object[] updatedElements);

    public abstract void clear();

    public void dispose() {
        // nothing to do
    }

    protected LPGSearchResultPage getPage() {
        return fPage;
    }
}
