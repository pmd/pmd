package net.sourceforge.pmd.eclipse.views;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.preferences.RuleDialog;

import org.eclipse.jface.action.Action;

/**
 * Implements the call of the rule dialog to show rule data
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class ShowRuleAction extends Action {
    private ViolationView violationView;
    
    /**
     * Constructor
     */
    public ShowRuleAction(ViolationView violationView) {
        this.violationView = violationView;
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        Rule selectedRule = violationView.getSelectedViolationRule();
        if (selectedRule != null) {
            RuleDialog ruleDialog = new RuleDialog(violationView.getSite().getShell(), selectedRule, false);
            ruleDialog.open();
        }
    }

}
