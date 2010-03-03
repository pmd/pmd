package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.util.Util;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * Provides the ViolationOutlinePages with Content
 *
 * @author SebastianRaffel  ( 08.05.2005 )
 */
public class ViolationOutlineContentProvider implements
		IStructuredContentProvider, IResourceChangeListener {

	private ViolationOutlinePage outlinePage;
	private TableViewer tableViewer;
	private FileRecord resource;


	/**
	 * Constructor
	 *
	 * @param page
	 */
	public ViolationOutlineContentProvider(ViolationOutlinePage page) {
		outlinePage = page;
		tableViewer = page.getTableViewer();
	}


	/* @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object) */
	public Object[] getElements(Object inputElement) {
	    
		if (inputElement instanceof FileRecord) {
			return ((FileRecord) inputElement).findMarkers();
		}
		return Util.EMPTY_ARRAY;
	}


	/* @see org.eclipse.jface.viewers.IContentProvider#dispose() */
	public void dispose() {
	}


	/* @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object) */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (resource != null) {
			resource.getResource().getWorkspace().removeResourceChangeListener(this);
		}

		// we create a new FileRecord
		resource = (FileRecord) newInput;
		if (resource != null) {
			resource.getResource().getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		}
		tableViewer = (TableViewer) viewer;
	}


	/* @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent) */
	public void resourceChanged(IResourceChangeEvent event) {
        IMarkerDelta[] markerDeltas = event.findMarkerDeltas(PMDRuntimeConstants.PMD_MARKER, true);

		if (!resource.getResource().exists()
				|| resource == null
				|| markerDeltas == null)
			return;

		// we search for removed, added or changed Markers
        final List<IMarker> additions = new ArrayList<IMarker>();
        final List<IMarker> removals = new ArrayList<IMarker>();
        final List<IMarker> changes = new ArrayList<IMarker>();

        for (int i=0; i<markerDeltas.length; i++) {
        	if (!markerDeltas[i].getResource().equals(resource.getResource()))
        		continue;
        	IMarker marker = markerDeltas[i].getMarker();
        	switch (markerDeltas[i].getKind()) {
    			case IResourceDelta.ADDED:
    				additions.add(marker);
    				break;
    			case IResourceDelta.REMOVED:
    				removals.add(marker);
    				break;
    			case IResourceDelta.CHANGED:
    				changes.add(marker);
    				break;
        	}
        }

        // updating the table MUST be in sync
        tableViewer.getControl().getDisplay().syncExec(new Runnable() {
        	public void run() {
        		updateViewer(additions, removals, changes);
        	}
        });
	}

	/**
     * Applies found updates on the table,
     * adapted from Philippe Herlin
	 *
	 * @param additions
	 * @param removals
	 * @param changes
	 */
    protected void updateViewer(List<IMarker> additions, List<IMarker> removals, List<IMarker> changes) {
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

        outlinePage.refresh();
    }
}


