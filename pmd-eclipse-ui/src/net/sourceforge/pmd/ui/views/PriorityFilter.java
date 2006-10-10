package net.sourceforge.pmd.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.PackageRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * The ViewerFilter for Priorities
 * 
 * @author SebastianRaffel ( 17.05.2005 )
 */
public class PriorityFilter extends ViewerFilter {
    private List priorityList;

    /**
     * Constructor
     * 
     * @author SebastianRaffel ( 29.06.2005 )
     */
    public PriorityFilter() {
        super();
        priorityList = new ArrayList(Arrays.asList(PMDUiPlugin.getDefault().getPriorityValues()));
    }

    /* @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object) */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        // the Element can be ...
        if (element instanceof PackageRecord) {
            // a Package (Overview)
            PackageRecord packageRec = (PackageRecord) element;

            // go through the List and search for Markers with the
            // given Priorities, if there is one, it is displayed
            for (int i = 0; i < priorityList.size(); i++) {
                Integer priority = (Integer) priorityList.get(i);
                if (packageRec.findMarkersByAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY, priority) != null)
                    return true;
            }
        } else if (element instanceof FileRecord) {
            // ... a File (Overview)
            FileRecord fileRec = (FileRecord) element;
            for (int i = 0; i < priorityList.size(); i++) {
                Integer priority = (Integer) priorityList.get(i);

                // go through the List and search for Markers with the
                // given Priorities, if there is one, it is displayed
                if (fileRec.findMarkersByAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY, priority) != null)
                    return true;
            }
        } else if (element instanceof IMarker) {
            // ... or a Marker (Outline)
            try {
                Integer markerPrio = (Integer) ((IMarker) element).getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY);

                // go through the List and search for Markers with the
                // given Priorities, if there is one, it is displayed
                // ** PHR note ** for unknown reason, markerPrio may be null
                if (markerPrio != null) {
                    for (int i = 0; i < priorityList.size(); i++) {
                        Integer priority = (Integer) priorityList.get(i);
                        if (markerPrio.equals(priority))
                            return true;
                    }
                }
            } catch (CoreException ce) {
                PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
            }
        }
        return false;
    }

    /**
     * Sets the List of Priorities to filter
     * 
     * @param newList, an ArrayLust of Integers
     */
    public void setPriorityFilterList(List newList) {
        this.priorityList = newList;
    }

    /**
     * Gets the FilterList with the Priorities
     * 
     * @return an ArrayList of Integers
     */
    public List getPriorityFilterList() {
        return this.priorityList;
    }

    /**
     * Adds a Priority to The List
     * 
     * @param priority
     */
    public void addPriorityToList(Integer priority) {
        priorityList.add(priority);
    }

    /**
     * Removes a Priority From the List
     * 
     * @param priority
     */
    public void removePriorityFromList(Integer priority) {
        priorityList.remove(priority);
    }

    /**
     * Loads a Priorityist out of a String, e.g. from "1,2,3" it builds up the List {1,2,3} (for use with Mementos)
     * 
     * @param newList, the List-String
     * @param splitter, the List splitter (in general ",")
     */
    public void setPriorityFilterListFromString(String newList, String splitter) {
        if (newList == null)
            return;

        String[] newArray = newList.split(splitter);
        ArrayList priorities = new ArrayList();

        for (int i = 0; i < newArray.length; i++) {
            priorities.add(new Integer(newArray[i]));
        }

        priorityList = priorities;
    }

    /**
     * Returns the FilterList as String with the given splitter, e.g. with "," the Priorities {1,4,5} would look like "1,4,5" (for
     * use with Mementos)
     * 
     * @param splitter, The String splitter (in general ",")
     * @return the List-String
     */
    public String getPriorityFilterListAsString(String splitter) {
        String listString = "";
        for (int i = 0; i < priorityList.size(); i++) {
            if (i > 0)
                listString += splitter;
            listString += priorityList.get(i).toString();
        }
        return listString;
    }
}
