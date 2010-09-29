package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.UISettings;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleUIUtil;

/**
 * Implements a label provider for the rules item to be displayed in the
 * rule table of the PMD Preference page
 * 
 * @author Philippe Herlin
 */
public class RuleLabelProvider extends AbstractTableLabelProvider {

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";
        if (element instanceof Rule) {
            Rule rule = (Rule) element;
            if (columnIndex == 0) {
                result = rule.getLanguage().getShortName();
            } else if (columnIndex == 1) {
                result = RuleUIUtil.ruleSetNameFrom(rule);
                /*
                if (rule instanceof RuleReference) {
                	RuleReference ruleReference = (RuleReference)rule;
                	String fileName = ruleReference.getRuleSetReference().getRuleSetFileName();
                	if (fileName != null) {
                		result = result + " : " + fileName;
                	}
                }
                */
            } else if (columnIndex == 2) {
                result = rule.getName();
            } else if (columnIndex == 3) {
                result = rule.getSince();
                result = (result == null) ? "n/a" : result;
            } else if (columnIndex == 4) {
                result = UISettings.labelFor(rule.getPriority());
            } else if (columnIndex == 5) {
                result = rule.getDescription();
                result = (result == null) ? "" : result.trim();
            }
        }

        return result;
    }
}
