package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.util.Collection;
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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;

public class DisableRuleAction extends AbstractViolationSelectionAction {

	private final IPreferences preferences = PMDPlugin.getDefault().loadPreferences();
    
    public static void disableRulesFor(Collection<Rule> rules, IPreferences preferences) {
    	
           for (Rule rule : rules) {
           		preferences.isActive(rule.getName(), false);
           }
           
           preferences.sync();
    }
    
    public static void removeViolationsOf(Collection<Rule> rules, Set<IProject> projects) {
    	         
         int deletions = 0;
         for (IProject project : projects) {
 	        for (Rule rule : rules) {
 	        	deletions += MarkerUtil.deleteViolationsOf(rule.getName(), project);
 	        }
         }
         
         System.out.println("Violations deleted: " + deletions);
    }
    
	public DisableRuleAction(TableViewer viewer) {
		super(viewer);
	}

	protected String textId() { return StringKeys.VIEW_ACTION_DISABLE_RULE; }
 	
 	protected String imageId() { return PMDUiConstants.ICON_BUTTON_DISABLE; }
    
    protected String tooltipMsgId() { return StringKeys.VIEW_TOOLTIP_DISABLE; } 
  
    public boolean hasActiveRules() {
    	
    	final IMarker[] markers = getSelectedViolations();
        if (markers == null) return false;
        
        List<Rule> rules = MarkerUtil.rulesFor(markers);
        for (Rule rule : rules) {
        	if (preferences.isActive(rule.getName())) return true;
        }
        
        return false;
    }
    
    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
    	
        final IMarker[] markers = getSelectedViolations();
        if (markers == null) return;
        
       runWith(markers, preferences, true);                
    }
    
    public static void runWith(final IMarker[] markers, final IPreferences preferences, final boolean removeViolations) {

        try {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            workspace.run(new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {                	
                    Collection<Rule> rules = MarkerUtil.rulesFor(markers);
                    disableRulesFor(rules, preferences);        
                    if (removeViolations) removeViolationsOf(rules, MarkerUtil.commonProjectsOf(markers) );
                }
            }, null);
        } catch (CoreException ce) {
        	logErrorByKey(StringKeys.ERROR_CORE_EXCEPTION, ce);
        }
                
    }
}
