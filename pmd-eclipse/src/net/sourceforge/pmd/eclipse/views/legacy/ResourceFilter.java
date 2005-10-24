package net.sourceforge.pmd.eclipse.views.legacy;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters violation by resource or project
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
public class ResourceFilter extends ViewerFilter {
    private ViolationView violationView;

    /**
     * Constructor
     */
    public ResourceFilter(ViolationView violationView) {
        this.violationView = violationView;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(Viewer, Object, Object)
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean flSelected = true;
        IResource focusResource = violationView.getFocusResource();

        if ((focusResource != null) && (element instanceof IMarker)) {
            IResource resource = ((IMarker) element).getResource();
            if ((violationView.isFileSelection()) && (!resource.equals(focusResource))) {
                flSelected = false;
            } else if ((violationView.isProjectSelection()) && (!resource.getProject().equals(focusResource.getProject()))) {
                flSelected = false;
            }
        }

        return flSelected;
    }

}
