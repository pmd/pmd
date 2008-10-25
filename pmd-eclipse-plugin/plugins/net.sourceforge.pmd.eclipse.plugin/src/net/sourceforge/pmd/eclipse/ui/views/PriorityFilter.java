package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileToMarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.MarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.PackageRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

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
    private List<Integer> priorityList;

    /**
     * Constructor
     *
     * @author SebastianRaffel ( 29.06.2005 )
     */
    public PriorityFilter() {
        super();
        priorityList = new ArrayList<Integer>(Arrays.asList(PMDPlugin.getDefault().getPriorityValues()));
    }

    /* @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object) */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean select = false;

        if (element instanceof PackageRecord) {
            // ViolationOverview
            select = hasMarkersToShow((PackageRecord)element);
        } else if (element instanceof FileRecord) {
            // ViolationOverview
            select = hasMarkersToShow((FileRecord)element);
        } else if (element instanceof IMarker) {
            // ViolationOutline
            try {
                final IMarker marker = (IMarker) element;
                final Integer markerPrio = (Integer) marker.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY);
                select = isPriorityEnabled(markerPrio);
            } catch (CoreException ce) {
                PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
            }
        } else if (element instanceof MarkerRecord) {
            // ViolationOverview
            final MarkerRecord markerRec = (MarkerRecord) element;
            for (int i = 0; i < priorityList.size(); i++) {
                final Integer priority = priorityList.get(i);

                if (markerRec.getPriority() == priority.intValue()) {
                    select = true;
                    break;
                }
            }
        } else if (element instanceof FileToMarkerRecord) {
            select = true;
        }
        return select;
    }

    private boolean isPriorityEnabled(Integer markerPrio) {
        boolean isEnabled = false;
        // for some unknown reasons markerPrio may be null.
        if (markerPrio != null) {
            for (int i = 0; i < priorityList.size(); i++) {
                final Integer priority = priorityList.get(i);
                if (markerPrio.equals(priority)) {
                    isEnabled = true;
                    break;
                }
            }
        }
        return isEnabled;
    }

    private boolean hasMarkersToShow(AbstractPMDRecord record) {
        boolean hasMarkers = false;
        for (int i = 0; i < priorityList.size(); i++) {
            final Integer priority = priorityList.get(i);
            final IMarker[] markers = record.findMarkersByAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY, priority);
            if (markers.length > 0) {
                hasMarkers = true;
                break;
            }
        }
        return hasMarkers;
    }


    /**
     * Sets the List of Priorities to filter
     *
     * @param newList, an ArrayLust of Integers
     */
    public void setPriorityFilterList(List<Integer> newList) {
        this.priorityList = newList;
    }

    /**
     * Gets the FilterList with the Priorities
     *
     * @return an ArrayList of Integers
     */
    public List<Integer> getPriorityFilterList() {
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
     * Loads a PriorityList out of a String, e.g. from "1,2,3" it builds up the List {1,2,3} (for use with Mementos)
     *
     * @param newList, the List-String
     * @param splitter, the List splitter (in general ",")
     */
    public void setPriorityFilterListFromString(String newList, String splitter) {
        if (newList != null) {
            final String[] newArray = newList.split(splitter);
            final ArrayList<Integer> priorities = new ArrayList<Integer>();

            for (String element : newArray) {
                priorities.add(Integer.valueOf(element)); // NOPMD by Sven on 13.11.06 11:53
            }

            priorityList = priorities;
        }
    }

    /**
     * Returns the FilterList as String with the given splitter, e.g. with "," the Priorities {1,4,5} would look like "1,4,5" (for
     * use with Mementos)
     *
     * @param splitter, The String splitter (in general ",")
     * @return the List-String
     */
    public String getPriorityFilterListAsString(String splitter) {
        final StringBuffer listString = new StringBuffer();
        for (int i = 0; i < priorityList.size(); i++) {
            if (i > 0) {
                listString.append(splitter);
            }
            listString.append(priorityList.get(i));
        }
        return listString.toString();
    }
}
