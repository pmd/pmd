package net.sourceforge.pmd.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.model.DataflowMethodRecord;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * Provides the Content for the DataflowAnomalyTable
 * 
 * @author SebastianRaffel  ( 07.06.2005 )
 */
public class DataflowAnomalyTableContentProvider implements 
		IStructuredContentProvider, IResourceChangeListener {
	
	private DataflowMethodRecord methodRecord;
	private TableViewer tableViewer;
	
	
	/* @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object) */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof DataflowMethodRecord) {
			return ((DataflowMethodRecord) inputElement).getMarkers();
		}
		return null;
	}
	
	/* @see org.eclipse.jface.viewers.IContentProvider#dispose() */
	public void dispose() {
	}
	
	/* @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object) */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (methodRecord != null) {
			methodRecord.getResource().getWorkspace()
				.removeResourceChangeListener(this);
		}
		if (newInput instanceof DataflowMethodRecord) {
			methodRecord = (DataflowMethodRecord) newInput;
			if (methodRecord != null) {
				methodRecord.getResource().getWorkspace()
					.addResourceChangeListener(this, 
					IResourceChangeEvent.POST_CHANGE);
			}
		}
		
		tableViewer = (TableViewer) viewer;
	}

	/* @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent) */
	public void resourceChanged(IResourceChangeEvent event) {
        IMarkerDelta[] markerDeltas = 
        	event.findMarkerDeltas(PMDPlugin.PMD_DFA_MARKER, true);
        
		if ((!methodRecord.getResource().exists())
				|| (methodRecord == null)
				|| (markerDeltas == null))
			return;
		
		// the Viewer must be updated, when something 
		// is added, removed or has changed
        final List additions = new ArrayList();
        final List removals = new ArrayList();
        final List changes = new ArrayList();
        
        for (int i=0; i<markerDeltas.length; i++) {
        	if (!markerDeltas[i].getResource()
        			.equals(methodRecord.getResource()))
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
     * Applies found updates on the table, 
     * adapted from Philippe Herlin
	 * 
	 * @param additions
	 * @param removals
	 * @param changes
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
        
        tableViewer.refresh();
    }
}
