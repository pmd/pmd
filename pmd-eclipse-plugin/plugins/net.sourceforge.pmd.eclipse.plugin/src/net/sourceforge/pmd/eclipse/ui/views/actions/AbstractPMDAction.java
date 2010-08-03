package net.sourceforge.pmd.eclipse.ui.views.actions;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;

import org.eclipse.jface.action.Action;

/**
 * 
 * @author Brian Remedios
 */
public abstract class AbstractPMDAction extends Action {

	protected AbstractPMDAction() {
		setupWidget();
	}

	protected abstract String imageId();
	protected abstract String tooltipMsgId();
	
	public static String getString(String messageId) {
		return PMDPlugin.getDefault().getStringTable().getString(messageId);
	}
	
	protected void setupWidget() {
		
		String imageId = imageId();
		if (imageId != null) setImageDescriptor(PMDPlugin.getImageDescriptor(imageId));
		
		String toolTipMsgId = tooltipMsgId();
		if (toolTipMsgId != null) setToolTipText(getString(toolTipMsgId));
	}
	
	protected static IPreferences loadPreferences() {
		return PMDPlugin.getDefault().loadPreferences();
	}
	
	protected void logErrorByKey(String errorId, Throwable error) {
		PMDPlugin.getDefault().logError(getString(errorId), error);
	}
}
