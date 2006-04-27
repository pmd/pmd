package net.sourceforge.pmd.eclipse.views.legacy;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Sort violations according to user's choice
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/04/27 20:35:54  phherlin
 * On the legacy Violations View, add new sorting option on line numbers and add reverse sorting on all columns (contribution of Pablo Alba)
 * Revision 1.1 2005/10/24 22:45:58 phherlin Integrating Sebastian Raffel's work Move orginal Violations view to legacy
 * 
 * Revision 1.1 2003/07/07 19:24:54 phherlin Adding PMD violations view
 * 
 */
public class ViolationSorter extends ViewerSorter {
    private ViolationView violationView;

    /**
     * Constructor
     */
    public ViolationSorter(ViolationView violationView) {
        this.violationView = violationView;
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerSorter#compare(Viewer, Object, Object)
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        int result = 0;
        int sorterFlag = violationView.getSorterFlag();
        if ((e1 instanceof IMarker) && (e2 instanceof IMarker)) {
            ITableLabelProvider labelProvider = (ITableLabelProvider) ((TableViewer) viewer).getLabelProvider();
            if (sorterFlag == ViolationView.SORTER_PRIORITY) {
                result = labelProvider.getColumnText(e1, 0).compareTo(labelProvider.getColumnText(e2, 0));
            } else if (sorterFlag == ViolationView.SORTER_RULE) {
                result = labelProvider.getColumnText(e1, 2).compareTo(labelProvider.getColumnText(e2, 2));
            } else if (sorterFlag == ViolationView.SORTER_CLASS) {
                result = labelProvider.getColumnText(e1, 3).compareTo(labelProvider.getColumnText(e2, 3));
                if (result == 0) {
                    // Same class, order by line
                    result = compareLines(labelProvider.getColumnText(e1, 6), labelProvider.getColumnText(e2, 6));
                }
            } else if (sorterFlag == ViolationView.SORTER_PACKAGE) {
                result = labelProvider.getColumnText(e1, 4).compareTo(labelProvider.getColumnText(e2, 4));
                if (result == 0) {
                    // Same package, order by class
                    result = labelProvider.getColumnText(e1, 3).compareTo(labelProvider.getColumnText(e2, 3));
                    if (result == 0) {
                        // Same class, order by line
                        result = compareLines(labelProvider.getColumnText(e1, 6), labelProvider.getColumnText(e2, 6));
                    }
                }
            } else if (sorterFlag == ViolationView.SORTER_PROJECT) {
                result = labelProvider.getColumnText(e1, 5).compareTo(labelProvider.getColumnText(e2, 5));
                if (result == 0) {
                    // Same project, order by package
                    result = labelProvider.getColumnText(e1, 4).compareTo(labelProvider.getColumnText(e2, 4));
                    if (result == 0) {
                        // Same package, order by class
                        result = labelProvider.getColumnText(e1, 3).compareTo(labelProvider.getColumnText(e2, 3));
                        if (result == 0) {
                            // Same class, order by line
                            result = compareLines(labelProvider.getColumnText(e1, 6), labelProvider.getColumnText(e2, 6));
                        }
                    }
                }

            } else if (sorterFlag == ViolationView.SORTER_LINE) {
                result = compareLines(labelProvider.getColumnText(e1, 6), labelProvider.getColumnText(e2, 6));
            }
        }

        if (violationView.getReverseSorterFlag()) {
            result = -result;
        }

        return result;
    }

    /**
     * Compare lines of two violations
     * 
     * @param line1
     * @param line2
     * @return
     */
    private int compareLines(String line1, String line2) {
        int value1;
        int value2;
        try {
            value1 = Integer.parseInt(line1);
        } catch (NumberFormatException nfe) {
            value1 = 0;
        }
        try {
            value2 = Integer.parseInt(line2);
        } catch (NumberFormatException nfe) {
            value2 = 0;
        }
        return (value1 - value2);
    }
}
