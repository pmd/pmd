package net.sourceforge.pmd.eclipse.views.legacy;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filter violations by priority
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/10/24 22:45:58  phherlin
 * Integrating Sebastian Raffel's work
 * Move orginal Violations view to legacy
 *
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class PriorityFilter extends ViewerFilter {
    private ViolationView violationView;

    /**
     * Constructor
     */
    public PriorityFilter(ViolationView violationView) {
        this.violationView = violationView;
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(Viewer, Object, Object)
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean flSelected = false;

        if (element instanceof IMarker) {
            IMarker marker = (IMarker) element;
            int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            int priority = marker.getAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);

            if (severity == IMarker.SEVERITY_ERROR) {
                flSelected =
                    ((priority == IMarker.PRIORITY_HIGH) && (violationView.isErrorHighFilterChecked()))
                        || ((priority == IMarker.PRIORITY_NORMAL) && (violationView.isErrorFilterChecked()));
            } else if (severity == IMarker.SEVERITY_WARNING) {
                flSelected =
                    ((priority == IMarker.PRIORITY_HIGH) && (violationView.isWarningHighFilterChecked()))
                        || ((priority == IMarker.PRIORITY_NORMAL) && (violationView.isWarningFilterChecked()));
            } else if (severity == IMarker.SEVERITY_INFO) {
                flSelected = violationView.isInformationFilterChecked();
            }
        }

        return flSelected;
    }

}
