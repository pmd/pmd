package net.sourceforge.pmd.ui.preferences;

import org.apache.log4j.Logger;

/**
 * Implements a label provider for the item of the rule properties table of
 * the PMD Preference page.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:23:41  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.2  2004/04/19 22:25:50  phherlin
 * Fixing UTF-8 encoding
 *
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RulePropertyLabelProvider extends AbstractTableLabelProvider {
    private static final Logger log = Logger.getLogger(RulePropertyLabelProvider.class);

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
}
