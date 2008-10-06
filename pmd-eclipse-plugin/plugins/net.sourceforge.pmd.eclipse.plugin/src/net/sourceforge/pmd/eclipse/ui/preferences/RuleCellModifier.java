package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

/**
 * Implements a cell modifier for rule properties editing in the rule table
 * of the PMD Preference page
 * 
 * @author Philippe Herlin
 *
 */
public class RuleCellModifier implements ICellModifier {
    private TableViewer tableViewer;

    /**
     * Constructor
     */
    public RuleCellModifier(TableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(Object, String)
     */
    public boolean canModify(Object element, String property) {
        return property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_PRIORITY)
            || property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_DESCRIPTION);
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(Object, String)
     */
    public Object getValue(Object element, String property) {
        Object result = null;

        if (element instanceof Rule) {
            Rule rule = (Rule) element;
            if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_RULESET_NAME)) {
                result = rule.getRuleSetName();
            } else if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_RULE_NAME)) {
                result = rule.getName();
            } else if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_PRIORITY)) {
                result = new Integer(rule.getPriority() - 1);
            } else if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_DESCRIPTION)) {
                result = rule.getDescription();
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#modify(Object, String, Object)
     */
    public void modify(Object element, String property, Object value) {
        TableItem item = (TableItem) element;

        try {
            if (item.getData() instanceof Rule) {
                Rule rule = (Rule) item.getData();
                if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_PRIORITY)) {
                    rule.setPriority(((Integer) value).intValue() + 1);
                    PMDPreferencePage.getActiveInstance().setModified(true);
                    //tableViewer.update(rule, new String[] { "priority" });
                    tableViewer.refresh();
                } else if (property.equalsIgnoreCase(PMDPreferencePage.PROPERTY_DESCRIPTION)) {
                    rule.setDescription((String) value);
                    PMDPreferencePage.getActiveInstance().setModified(true);
                    //tableViewer.update(rule, new String[] { "description" });
                    tableViewer.refresh();
                }
            }
        } catch (Throwable t) {
            // Bug in JFace for Eclipse 2.0x
            // Ignore exception
            PMDPlugin.getDefault().logError(
                "Exception when notifying a modification in a cell of the rule table in the preference page",
                t);
        }
    }

}
