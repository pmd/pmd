package net.sourceforge.pmd.eclipse.ui.views.actions;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleDialog;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Implements the call of the rule dialog to show rule data
 *
 * @author Philippe Herlin
 *
 */
public class ShowRuleAction extends AbstractViolationSelectionAction {

    private Shell shell;

    /**
     * Constructor
     */
    public ShowRuleAction(TableViewer viewer, Shell shell) {
        super(viewer);
    }

 	protected String textId() { return StringKeys.MSGKEY_VIEW_ACTION_SHOW_RULE; }
 	
 	protected String imageId() { return null; }
    
    protected String tooltipMsgId() { return StringKeys.MSGKEY_VIEW_TOOLTIP_SHOW_RULE; } 
    
    private String ruleNameFor(IMarker marker) {
    	return marker.getAttribute(PMDUiConstants.KEY_MARKERATT_RULENAME, "");
    }
    
    protected boolean canExecute() {
    	return super.canExecute() && allSelectionsDenoteSameRule();
    }
    
    private boolean allSelectionsDenoteSameRule() {
    	
    	IMarker[] markers = getSelectedViolations();
    	String ruleName = ruleNameFor(markers[0]);
    	for (int i=1; i<markers.length; i++) {
    		if (!ruleName.equals(ruleNameFor(markers[i]))) return false;
    	}
    	
    	return true;
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
                rule = PMDPlugin.getDefault().getPreferencesManager().getRuleSet().getRuleByName(
                        markers[0].getAttribute(PMDUiConstants.KEY_MARKERATT_RULENAME, ""));
            }
        } catch (RuntimeException e) {
        	logErrorByKey(StringKeys.MSGKEY_ERROR_RUNTIME_EXCEPTION, e);
        }

        return rule;
    }
}
