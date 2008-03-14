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

package org.eclipse.imp.lpg.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.graphics.Image;

public class LPGSearchResultPage extends AbstractTextSearchViewPage {
    private final class LPGLabelProvider implements ILabelProvider {
        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            IFile file= (IFile) element;
            return "<entity in '" + file.getName() + "'>";
        }
    }

    private LPGSearchContentProvider fContentProvider;

    public LPGSearchResultPage(int supportedLayouts) {
        super(supportedLayouts);
    }

    public LPGSearchResultPage() {
        super();
    }

    protected void elementsChanged(Object[] objects) {
        if (fContentProvider != null)
            fContentProvider.elementsChanged(objects);
    }

    protected void clear() {
        if (fContentProvider != null)
            fContentProvider.clear();
    }

    protected void configureTreeViewer(TreeViewer viewer) {
//        PostfixLabelProvider postfixLabelProvider= new PostfixLabelProvider(this);
        viewer.setUseHashlookup(true);
//        viewer.setSorter(new DecoratorIgnoringViewerSorter(postfixLabelProvider));
//        viewer.setLabelProvider(new ColorDecoratingLabelProvider(postfixLabelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
        viewer.setLabelProvider(new LPGLabelProvider());
        fContentProvider= new LPGSearchTreeContentProvider(this);
        viewer.setContentProvider(fContentProvider);
//        addDragAdapters(viewer);
    }

    protected void configureTableViewer(TableViewer viewer) {
        viewer.setUseHashlookup(true);
        // SortingLabelProvider sortingLabelProvider= new SortingLabelProvider(this);
        // viewer.setLabelProvider(new ColorDecoratingLabelProvider(sortingLabelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
        viewer.setLabelProvider(new LPGLabelProvider());
        fContentProvider= new LPGSearchTableContentProvider(this);
        viewer.setContentProvider(fContentProvider);
        // viewer.setSorter(new DecoratorIgnoringViewerSorter(sortingLabelProvider));
        // setSortOrder(fCurrentSortOrder);
        // addDragAdapters(viewer);
    }

    protected StructuredViewer getViewer() {
        // override so that it's visible in the package.
        return super.getViewer();
    }
}
