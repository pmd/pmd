package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.dfa.DaaRuleViolation;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author SebastianRaffel ( 07.06.2005 ), Sven Jacob
 */
public class DataflowAnomalyTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final String KEY_IMAGE_DFA = "error_dfa";

    /* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int) */
    public Image getColumnImage(Object element, int columnIndex) {
        // set the Image for the Anomaly
        Image image = null;
        if (columnIndex == 0) {
            image = PMDPlugin.getDefault().getImage(KEY_IMAGE_DFA, PMDUiConstants.ICON_LABEL_ERR_DFA);
        }
        return image;
    }

    /* @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int) */
    public String getColumnText(Object element, int columnIndex) {
        String columnText = "";
        if (element instanceof IRuleViolation) {
            final IRuleViolation violation = (IRuleViolation) element;
            switch (columnIndex) {
            case 0:
                // show the Type of Anomalym which is saved as Message here
                columnText = ((DaaRuleViolation)violation).getType();
                break;
            case 1:
                // show the (first and last) Line                
                int line1 = violation.getBeginLine();
                int line2 = violation.getEndLine();

                // show only one Line if they are equal
                if ((line1 == line2) || (line2 == 0)) {
                    columnText = String.valueOf(line1);
                } else {
                    // ... or twist them if needed
                    // and show something like "11, 12"
                    if (line2 < line1) {
                        final int temp = line1;
                        line1 = line2;
                        line2 = temp;
                    }
                    columnText = line1 + ", " + line2;                    
                }

                break;
            case 2:
                // show the Variable                
                columnText = violation.getVariableName();
                break;

            case 3:
                // show the Method name                
                columnText = violation.getMethodName();
                break;
                
            default:
                break;
            }
        }
        
        return columnText;
    }
}
