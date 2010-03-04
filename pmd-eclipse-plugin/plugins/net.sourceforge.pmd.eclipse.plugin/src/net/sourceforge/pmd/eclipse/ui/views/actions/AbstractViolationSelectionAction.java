package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.util.Iterator;

import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;


/**
 * Base Class for Actions that need to know,
 * which Markers are selected
 *
 * @author SebastianRaffel  ( 21.05.2005 )
 * @author Brian Remedios   (  4.03.2010 )
 */
public abstract class AbstractViolationSelectionAction extends AbstractPMDAction {

	private TableViewer tableViewer;

	/**
	 * Constructor
	 *
	 * @param viewer
	 */
	protected AbstractViolationSelectionAction(TableViewer viewer) {
		tableViewer = viewer;

		setEnabled(canExecute());
	}

	protected abstract String textId();
	
	protected boolean canExecute() {
		return hasSelections();
	}
	
	protected void setupWidget() {
		super.setupWidget();
		
		String textId = textId();
		if (textId != null) setText(getString(textId));
	}
	
	protected boolean hasSelections() {
		return !tableViewer.getSelection().isEmpty();
	}
	
	/**
	 * Return the selected Violations (Markers)
	 *
	 * @return the Marker(s) currently selected
	 */
    public IMarker[] getSelectedViolations() {

		ISelection selection = tableViewer.getSelection();
        if (selection != null && selection instanceof IStructuredSelection) {

            IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            IMarker[] markers = new IMarker[structuredSelection.size()];
            Iterator<IMarker> i = structuredSelection.iterator();
            int index = 0;
            while (i.hasNext()) {
                markers[index++] = i.next();
            }
            return markers;
        }

        return MarkerUtil.EMPTY_MARKERS;
    }
}
