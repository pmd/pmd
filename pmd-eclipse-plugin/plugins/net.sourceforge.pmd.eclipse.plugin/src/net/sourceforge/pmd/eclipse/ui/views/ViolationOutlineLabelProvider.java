package net.sourceforge.pmd.eclipse.ui.views;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.util.NumericConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * Provides the ViolationsOutlinePages with labels and images
 * 
 * @author SebastianRaffel ( 08.05.2005 )
 */
public class ViolationOutlineLabelProvider extends AbstractViolationLabelProvider {

    /* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int) */
    public Image getColumnImage(Object element, int columnIndex) {
        IMarker marker = null;
        if (element instanceof IMarker)
            marker = (IMarker) element;
        else
            return null;

        if (columnIndex == 0) {
            Integer priority = NumericConstants.ZERO;
            try {
                priority = (Integer) marker.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY);
            } catch (CoreException ce) {
                PMDPlugin.getDefault().logError(StringKeys.ERROR_CORE_EXCEPTION + toString(), ce);
            }

            return getPriorityImage(priority);
        }

        return null;
    }

    /* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int) */
    public String getColumnText(Object element, int columnIndex) {
        IMarker marker = null;
        if (element instanceof IMarker)
            marker = (IMarker) element;
        else
            return null;

        switch (columnIndex) {
        // show the Message
        case 1:
            return marker.getAttribute(IMarker.MESSAGE, PMDUiConstants.KEY_MARKERATT_RULENAME);
        // show the Line
        case 2:
            return String.valueOf(marker.getAttribute(IMarker.LINE_NUMBER, 0));
        }

        return "";
    }

}
