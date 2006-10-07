package net.sourceforge.pmd.ui.views;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.model.FileRecord;

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
	
	private FileRecord fileRecord;
	private TableViewer tableViewer;
	
	
	/* @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object) */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof FileRecord) {
			IMarker[] allMarkers = ((FileRecord) inputElement).findDFAMarkers();
            ArrayList markers = new ArrayList();
            
            // we only want to get the Markers for this Method,
            // so we need to "extract" them from the whole List
            for (int i=0; i<allMarkers.length; i++) {
                // the Marker should have valid Information in it
                if (!markerIsValid(allMarkers[i]))
                    continue;
                
                // ... and we don't want it twice, so we check,
                // if the Marker already exists
 //               if (!markerIsInList(allMarkers[i], markers))
                    markers.add(allMarkers[i]);
            }
            return markers.toArray();
		}
		return null;
	}
	
	/* @see org.eclipse.jface.viewers.IContentProvider#dispose() */
	public void dispose() {
	}
	
	/* @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object) */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (fileRecord != null) {
			fileRecord.getResource().getWorkspace()
				.removeResourceChangeListener(this);
		}
		if (newInput instanceof FileRecord) {
			fileRecord = (FileRecord) newInput;
			if (fileRecord != null) {
				fileRecord.getResource().getWorkspace()
					.addResourceChangeListener(this, 
					IResourceChangeEvent.POST_CHANGE);
			}
		}
		
		tableViewer = (TableViewer) viewer;
	}

	/* @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent) */
	public void resourceChanged(IResourceChangeEvent event) {
        IMarkerDelta[] markerDeltas = 
        	event.findMarkerDeltas(PMDRuntimeConstants.PMD_DFA_MARKER, true);
        
		if ((fileRecord == null)
                || (!fileRecord.getResource().exists())
				|| (markerDeltas == null))
			return;
		
		// the Viewer must be updated, when something 
		// is added, removed or has changed
        final List additions = new ArrayList();
        final List removals = new ArrayList();
        final List changes = new ArrayList();
        
        for (int i=0; i<markerDeltas.length; i++) {
        	if (!markerDeltas[i].getResource()
        			.equals(fileRecord.getResource()))
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
    
    /**
     * Returns a list of Attributes for a Dataflow Marker, 
     * (1.) the Error Message, (2.) the beginning Line of the Error,
     * (3.) the ending Line and (4.) the Variable (Marker Attribute)
     * 
     * @param marker
     * @return an Array of Attributes
     */
    private Object[] getMarkerAttributes(IMarker marker) {
        ArrayList values = new ArrayList();
        
        // add Message, default ""
        values.add(marker.getAttribute(IMarker.MESSAGE, ""));
        
        // add the Lines, default 0
        // the default-Values help preventing an Exception
        int line1 = 
            marker.getAttribute(IMarker.LINE_NUMBER, 0);
        int line2 = 
            marker.getAttribute(PMDUiConstants.KEY_MARKERATT_LINE2, 0);
        // exchange the Lines if begin > end 
        if (line2 < line1) {
            int temp = line1;
            line1 = line2;
            line2 = temp;
        }
        values.add(new Integer(line1));
        values.add(new Integer(line2));
        
        // add the Variable
        values.add(marker.getAttribute(
            PMDUiConstants.KEY_MARKERATT_VARIABLE, ""));
        
        return values.toArray();
    }
    
    /**
     * Checks, if a Marker is valid, meaning that it
     * has a Variable and Message set.
     * 
     * @param marker
     * @return true if the Marker is valid, false otherwise
     */
    private boolean markerIsValid(IMarker marker) {
        // get the Markers atrributes
        Object[] values = getMarkerAttributes(marker);
        
        // check the Values
        for (int k=0; k<values.length; k++) {
            if (values[k] instanceof String) {
                // if it is a String, it has to be the Variable
                // or Message, which shouldn't be empty
                if (((String) values[k]).equals(""))
                    return false;
            } else if (values[k] instanceof Integer) {
                // else it is one of the Lines (Line, Line2)
                // and they also should not be 0
                if (((Integer) values[k]).intValue() == 0)
                    return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if a Marker is already in a List
     * 
     * @param marker
     * @param list
     * @return true, is the marker exists in thelist, false otherwise
     */
    private boolean markerIsInList(IMarker marker, ArrayList list) {
        if ((list == null) || list.isEmpty())
            return false;
        
        // here we can't simply compare Objects, because the Dataflow
        // Anomaly Calculation sets different Markers for the same Error
        
        // get the Markers Attributes and compare with all other Markers
        Object[] markerAttr = getMarkerAttributes(marker);
        for (int i=0; i<list.size(); i++) {
            // get the Marker from the List and its Attributes 
            Object[] listAttr = 
                getMarkerAttributes((IMarker) list.get(i));
            
            boolean markersAreEqual = true; 
            for (int j=0; j<listAttr.length; j++) {
                // compare the String- and Integer-Values
                if (markerAttr[j] instanceof String) {
                    if (!((String) markerAttr[j]).equalsIgnoreCase(
                        (String)listAttr[j]) ) {
                        markersAreEqual = false;
                    }
                } else if (markerAttr[j] instanceof Integer) {
                    if (!((Integer) markerAttr[j]).equals(
                        (Integer) listAttr[j]) )
                        markersAreEqual = false;
                }
            }
            
            // markersAreEqual only stays true, when all Checks above fail
            // we need to do that to compare _all_ Attributes; if they all
            // are equal, the Marker exists, if not we check the next one 
            if (markersAreEqual)
                return true;
            else
                continue;
        }
        return false;
    }
}
