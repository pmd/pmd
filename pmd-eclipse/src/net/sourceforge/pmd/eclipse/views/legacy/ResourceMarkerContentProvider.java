package net.sourceforge.pmd.eclipse.views.legacy;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * Implements a content provider that return PMD markers for a resource
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/10/24 22:45:58  phherlin
 * Integrating Sebastian Raffel's work
 * Move orginal Violations view to legacy
 *
 * Revision 1.2  2003/08/13 20:10:20  phherlin
 * Refactoring private->protected to remove warning about non accessible member access in enclosing types
 *
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class ResourceMarkerContentProvider implements IStructuredContentProvider, IResourceChangeListener {
    private TableViewer tableViewer;
    private IResource resource;

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
     */
    public Object[] getElements(Object inputElement) {
        IMarker[] elements = null;
        IResource resource = (IResource) inputElement;
        try {
            elements = resource.findMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(PMDConstants.MSGKEY_ERROR_FIND_MARKER, e);
            elements = new IMarker[0];
        }

        return elements;
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        if (resource != null) {
            resource.getWorkspace().removeResourceChangeListener(this);
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        tableViewer = (TableViewer) viewer;

        if (resource != null) {
            resource.getWorkspace().removeResourceChangeListener(this);
        }

        resource = (IResource) newInput;
        if (resource != null) {
            resource.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
        }

    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        /*
         * This code is adapted from Eclipse TaskList source
         */
        IMarkerDelta[] markerDeltas = event.findMarkerDeltas(PMDPlugin.PMD_MARKER, true);
        final List additions = new ArrayList();
        final List removals = new ArrayList();
        final List changes = new ArrayList();

        /*
         * To have better performance on updating the table, 
         * select which marker have been added, deleted and only changed
         */
        for (int i = 0; i < markerDeltas.length; i++) {
            int iKind = markerDeltas[i].getKind();
            IMarker marker = markerDeltas[i].getMarker();
            if (iKind == IResourceDelta.ADDED) {
                additions.add(marker);
            } else if (iKind == IResourceDelta.REMOVED) {
                removals.add(marker);
            } else if (iKind == IResourceDelta.CHANGED) {
                changes.add(marker);
            }
        }

        /*
         * Updating the table MUST be in sync
         */
        tableViewer.getControl().getDisplay().syncExec(new Runnable() {
            public void run() {
                updateViewer(additions, removals, changes);
            }
        });
    }
    
    /**
     * Apply found updates on the table
     */
    protected void updateViewer(List additions, List removals, List changes) {
        // perform removals
        if (removals.size() > 0) {
            tableViewer.cancelEditing();
            tableViewer.remove(removals.toArray());
        }

        // perform additions
        if (additions.size() > 0) {
            tableViewer.add(additions.toArray());
        }

        // perform changes
        if (changes.size() > 0) {
            tableViewer.update(changes.toArray(), null);
        }
    }

}
