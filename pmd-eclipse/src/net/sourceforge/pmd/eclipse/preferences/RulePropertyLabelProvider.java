package net.sourceforge.pmd.eclipse.preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Implements a label provider for the item of the rule properties table of
 * the PMD Preference page.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RulePropertyLabelProvider implements ITableLabelProvider {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.preferences.RulePropertyLabelProvider");

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";
        if (element instanceof RuleProperty) {
            RuleProperty ruleProperty = (RuleProperty) element;
            if (columnIndex == 0) {
                result = ruleProperty.getProperty();
                log.debug("Retour du nom de la propriété : " + result);
            } else if (columnIndex == 1) {
                result = ruleProperty.getValue();
                log.debug("Retour de la valeur de la propriété : " + result);
            }
        }
        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object, String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
    }

}
