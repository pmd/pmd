package net.sourceforge.pmd.eclipse.ui.views;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.actions.AbstractPMDAction;

public class DataflowResizeAction extends AbstractPMDAction {

	public DataflowResizeAction() {
	}

    protected String imageId() { return PMDUiConstants.ICON_BUTTON_CALCULATE; }
    
    protected String tooltipMsgId() { return StringKeys.VIEW_TOOLTIP_CALCULATE_STATS; }
	   
    /**
     * @return the Style, in which the Button is displayed
     */
    public int getStyle() {
        return AS_CHECK_BOX;
    }
	    /**
	     * Performs the Action
	     */
	public void run() {
	    
	}
}
	
	
