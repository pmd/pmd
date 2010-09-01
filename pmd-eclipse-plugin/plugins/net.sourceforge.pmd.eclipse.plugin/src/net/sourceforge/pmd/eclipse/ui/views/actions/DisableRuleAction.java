package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TableViewer;

public class DisableRuleAction extends AbstractViolationSelectionAction {

	private IPreferences preferences = PMDPlugin.getDefault().loadPreferences();
	
	public DisableRuleAction(TableViewer viewer) {
		super(viewer);
	}

	protected String textId() { return StringKeys.VIEW_ACTION_DISABLE_RULE; }
 	
 	protected String imageId() { return PMDUiConstants.ICON_BUTTON_DISABLE; }
    
    protected String tooltipMsgId() { return StringKeys.MSGKEY_VIEW_TOOLTIP_DISABLE; } 
  
    public boolean hasActiveRules() {
    	
    	final IMarker[] markers = getSelectedViolations();
        if (markers == null) return false;
        
        List<Rule> rules = MarkerUtil.rulesFor(markers);
        for (Rule rule : rules) {
        	if (preferences.isActive(rule.getName())) return true;
        }
        
        return false;
    }
    
    private void removeViolationsOf(List<Rule> rules, Set<IProject> projects) {
    	         
         int deletions = 0;
         for (IProject project : projects) {
 	        for (Rule rule : rules) {
 	        	deletions += MarkerUtil.deleteViolationsOf(rule.getName(), project);
 	        }
         }
         
         System.out.println("Violations deleted: " + deletions);
    }
    
    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
    	
        final IMarker[] markers = getSelectedViolations();
        if (markers == null) return;
        
        List<Rule> rules = MarkerUtil.rulesFor(markers);
        for (Rule rule : rules) {
        	preferences.isActive(rule.getName(), false);
        }
        
        preferences.sync();
        
        removeViolationsOf(rules, MarkerUtil.commonProjectsOf(markers) );
    }
}
