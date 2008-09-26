package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;


/**
 * Base Class for Actions that need to know,
 * whch Markers are selected
 * 
 * @author SebastianRaffel  ( 21.05.2005 )
 */
public class ViolationSelectionAction extends Action {
	
	private TableViewer tableViewer;
	
	
	/**
	 * Constructor
	 * 
	 * @param viewer
	 */
	public ViolationSelectionAction(TableViewer viewer) {
		tableViewer = viewer;
	}
	
	/**
	 * Return the selected Violations (Markers)
	 * 
	 * @return the Marker(s) currently selected
	 */
    public IMarker[] getSelectedViolations() {
        IMarker[] markers = null;
		ISelection selection = tableViewer.getSelection();
        if ((selection != null) && 
        		(selection instanceof IStructuredSelection)) {
        	
            IStructuredSelection structuredSelection = 
            	(IStructuredSelection) selection;
            
            markers = new IMarker[structuredSelection.size()];
            Iterator i = structuredSelection.iterator();
            int index = 0;
            while (i.hasNext()) {
                markers[index++] = (IMarker) i.next();
            }
        }

        return markers;
    }
}
