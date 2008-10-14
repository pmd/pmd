package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

/**
 * Implements a label provider for the rules item to be displayed in the
 * rule table of the PMD Preference page
 * 
 * @author Philippe Herlin
 *
 */
public class RuleLabelProvider extends AbstractTableLabelProvider {
    private static final String PRIORITY_ILLEGAL = "* illegal *";
    private static final String[] PRIORITY_LABEL = PMDPlugin.getDefault().getPriorityLabels();

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";
        if (element instanceof Rule) {
            Rule rule = (Rule) element;
            if (columnIndex == 0) {
                result = rule.getRuleSetName();
                /*
                if (rule instanceof RuleReference) {
                	RuleReference ruleReference = (RuleReference)rule;
                	String fileName = ruleReference.getRuleSetReference().getRuleSetFileName();
                	if (fileName != null) {
                		result = result + " : " + fileName;
                	}
                }
                */
            } else if (columnIndex == 1) {
                result = rule.getName();
            } else if (columnIndex == 2) {
                result = rule.getSince();
                result = (result == null) ? "n/a" : result;
            } else if (columnIndex == 3) {
                if ((rule.getPriority().getPriority() <= PRIORITY_LABEL.length) && (rule.getPriority().getPriority() > 0)) {
                    result = PRIORITY_LABEL[rule.getPriority().getPriority() - 1];
                } else {
                    result = PRIORITY_ILLEGAL;
                }
            } else if (columnIndex == 4) {
                result = rule.getDescription();
                result = (result == null) ? "" : result.trim();
            }
        }

        return result;
    }
}
