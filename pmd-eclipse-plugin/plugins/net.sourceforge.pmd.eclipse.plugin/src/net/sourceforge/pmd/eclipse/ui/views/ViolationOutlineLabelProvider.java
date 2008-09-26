package net.sourceforge.pmd.eclipse.ui.views;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provied the ViolationsOutlinePages with Texts and Labels
 * 
 * @author SebastianRaffel ( 08.05.2005 )
 */
public class ViolationOutlineLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final String KEY_IMAGE_ERR1 = "error1";
    private static final String KEY_IMAGE_ERR2 = "error2";
    private static final String KEY_IMAGE_ERR3 = "error3";
    private static final String KEY_IMAGE_ERR4 = "error4";
    private static final String KEY_IMAGE_ERR5 = "error5";

    /* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int) */
    public Image getColumnImage(Object element, int columnIndex) {
        IMarker marker = null;
        if (element instanceof IMarker)
            marker = (IMarker) element;
        else
            return null;

        if (columnIndex == 0) {
            Integer priority = new Integer(0);
            try {
                priority = (Integer) marker.getAttribute(PMDUiConstants.KEY_MARKERATT_PRIORITY);
            } catch (CoreException ce) {
                PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION + this.toString(), ce);
            }

            // set the Image by the Priority of the Error

            switch (priority.intValue()) {
            case 1:
                return getImage(KEY_IMAGE_ERR1, PMDUiConstants.ICON_LABEL_ERR1);
            case 2:
                return getImage(KEY_IMAGE_ERR2, PMDUiConstants.ICON_LABEL_ERR2);
            case 3:
                return getImage(KEY_IMAGE_ERR3, PMDUiConstants.ICON_LABEL_ERR3);
            case 4:
                return getImage(KEY_IMAGE_ERR4, PMDUiConstants.ICON_LABEL_ERR4);
            case 5:
                return getImage(KEY_IMAGE_ERR5, PMDUiConstants.ICON_LABEL_ERR5);
            }

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

    /**
     * 
     * @param key
     * @param iconPath
     * @return
     */
    private Image getImage(String key, String iconPath) {
        return PMDPlugin.getDefault().getImage(key, iconPath);
    }

}
