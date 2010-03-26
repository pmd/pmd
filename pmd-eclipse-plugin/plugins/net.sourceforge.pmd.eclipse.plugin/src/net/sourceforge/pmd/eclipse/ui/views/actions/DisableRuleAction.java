package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.TableViewer;

public class DisableRuleAction extends AbstractViolationSelectionAction {

	public DisableRuleAction(TableViewer viewer) {
		super(viewer);
	}

	protected String textId() { return StringKeys.MSGKEY_VIEW_ACTION_DISABLE; }
 	
 	protected String imageId() { return PMDUiConstants.ICON_BUTTON_DISABLE; }
    
    protected String tooltipMsgId() { return StringKeys.MSGKEY_VIEW_TOOLTIP_DISABLE; } 
  
    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        final IMarker[] markers = getSelectedViolations();
        if (markers == null) return;
        

        List<Rule> rules = MarkerUtil.rulesFor(markers);
               
    }

    
}
