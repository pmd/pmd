package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.preferences.RuleDialog;

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
 * Revision 1.1  2006/05/22 21:23:54  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 * Revision 1.1 2005/10/24 22:45:01 phherlin Integrating Sebastian Raffel's work
 *
 * Revision 1.1 2003/07/07 19:24:54 phherlin Adding PMD violations view
 *
 */
public class ShowRuleAction extends ViolationSelectionAction {

    private Shell shell;

    /**
     * Constructor
     */
    public ShowRuleAction(TableViewer viewer, Shell shell) {
        super(viewer);

        setText(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_ACTION_SHOW_RULE));
        setToolTipText(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_TOOLTIP_SHOW_RULE));
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
            PMDPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_RUNTIME_EXCEPTION, e);
        }

        return rule;
    }
}
