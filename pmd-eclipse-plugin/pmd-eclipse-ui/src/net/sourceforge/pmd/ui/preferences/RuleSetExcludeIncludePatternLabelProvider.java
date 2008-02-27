package net.sourceforge.pmd.ui.preferences;

/**
 * Implements a label provider for the item of the ruleset exclude/include
 * pattern tables of the PMD Preference page.
 * 
 * @version $Revision$
 * 
 * $Log$
 */
public class RuleSetExcludeIncludePatternLabelProvider extends AbstractTableLabelProvider {

	/**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";
        if (element instanceof RuleSetExcludeIncludePattern) {
        	RuleSetExcludeIncludePattern pattern = (RuleSetExcludeIncludePattern) element;
            if (columnIndex == 0) {
            	return pattern.getPattern();
            }
        }
        return result;
    }
}
