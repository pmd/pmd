package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author SebastianRaffel ( 07.06.2005 )
 */
public class DataflowAnomalyTableLabelProvider implements ITableLabelProvider {

    private static final String KEY_IMAGE_DFA = "error_dfa";

    /* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int) */
    public Image getColumnImage(Object element, int columnIndex) {
        // set the Image for the Anomaly
        if (columnIndex == 0) {
            return PMDUiPlugin.getDefault().getImage(KEY_IMAGE_DFA, PMDUiConstants.ICON_LABEL_ERR_DFA);
        }
        return null;
    }

    /* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int) */
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof IMarker))
            return "";

        IMarker marker = (IMarker) element;
        switch (columnIndex) {
        // show the Type of Anomalym which is saved as Message here
        case 0:
            return marker.getAttribute(IMarker.MESSAGE, "");
        // show the (first and last) Line
        case 1:
            int line1 = marker.getAttribute(IMarker.LINE_NUMBER, 0);
            int line2 = marker.getAttribute(PMDUiConstants.KEY_MARKERATT_LINE2, 0);

            // show only one Line if they are equal
            if ((line1 == line2) || (line2 == 0))
                return String.valueOf(line1);

            // ... or twist them if needed
            // and show something like "11, 12"
            if (line2 < line1) {
                int temp = line1;
                line1 = line2;
                line2 = temp;
            }
            return line1 + ", " + line2;
        // show the Variable
        case 2:
            return marker.getAttribute(PMDUiConstants.KEY_MARKERATT_VARIABLE, "");
        // show the Method name
        case 3:
            return marker.getAttribute(PMDUiConstants.KEY_MARKERATT_METHODNAME, "");
        }

        return "";
    }

    /* @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener) */
    public void addListener(ILabelProviderListener listener) {
    }

    /* @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose() */
    public void dispose() {
    }

    /* @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String) */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /* @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener) */
    public void removeListener(ILabelProviderListener listener) {
    }
}
