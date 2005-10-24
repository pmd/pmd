package net.sourceforge.pmd.eclipse.views.actions;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.preferences.RuleDialog;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;


/**
 * Implements the call of the rule dialog to show rule data
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/10/24 22:45:01  phherlin
 * Integrating Sebastian Raffel's work
 *
 * Revision 1.1  2003/07/07 19:24:54  phherlin
 * Adding PMD violations view
 *
 */
public class ShowRuleAction extends ViolationSelectionAction {
	
    private Shell shell;
    
    
    /**
     * Constructor
     */
    public ShowRuleAction(TableViewer viewer, Shell shell) {
        super(viewer);
        
        setText(PMDPlugin.getDefault().getMessage(
        	PMDConstants.MSGKEY_VIEW_ACTION_SHOW_RULE));
        setToolTipText(PMDPlugin.getDefault().getMessage(
        	PMDConstants.MSGKEY_VIEW_TOOLTIP_SHOW_RULE));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        Rule selectedRule = getSelectedViolationRule();
        if (selectedRule != null) {
            RuleDialog ruleDialog = new RuleDialog(shell, selectedRule, false);
            ruleDialog.open();
        }
    }
    
    
    /**
     * Returns the rule from the first selected violation
     */
    public Rule getSelectedViolationRule() {
        Rule rule = null;
        try {
            IMarker[] markers = getSelectedViolations();
            if (markers != null) {
                rule =
                    PMDPlugin.getDefault().getRuleSet().getRuleByName(
                        markers[0].getAttribute(PMDPlugin.KEY_MARKERATT_RULENAME, ""));
            }
        } catch (RuntimeException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_RUNTIME_EXCEPTION, e);
        }

        return rule;
    }
}
