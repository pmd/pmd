package net.sourceforge.pmd.eclipse.views;

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
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
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
            ITableLabelProvider labelProvider = (ITableLabelProvider) ((TableViewer)viewer).getLabelProvider();
            if (sorterFlag == ViolationView.SORTER_PRIORITY) {
                result = labelProvider.getColumnText(e1, 0).compareTo(labelProvider.getColumnText(e2, 0));
            } else if (sorterFlag == ViolationView.SORTER_RULE) {
                result = labelProvider.getColumnText(e1, 2).compareTo(labelProvider.getColumnText(e2, 2));
            } else if (sorterFlag == ViolationView.SORTER_CLASS) {
                result = labelProvider.getColumnText(e1, 3).compareTo(labelProvider.getColumnText(e2, 3));
            } else if (sorterFlag == ViolationView.SORTER_PACKAGE) {
                result = labelProvider.getColumnText(e1, 4).compareTo(labelProvider.getColumnText(e2, 4));
            } else if (sorterFlag == ViolationView.SORTER_PROJECT) {
                result = labelProvider.getColumnText(e1, 5).compareTo(labelProvider.getColumnText(e2, 5));
            }
        }

        return result;
    }
}
