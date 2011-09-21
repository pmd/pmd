package net.sourceforge.pmd.eclipse.ui.actions;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.runtime.cmd.RenderReportsCmd;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.reports.ReportManager;
import net.sourceforge.pmd.renderers.Renderer;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Process GenerateReport action menu.
 * Generate a HTML report on the current project.
 *
 * @author Philippe Herlin
 * @author Brian Remedios
 */
public class GenerateReportAction extends AbstractUIAction {
	
    private static final Logger log = Logger.getLogger(GenerateReportAction.class);
    
    private static final String DefaultReportName = "pmd-report";
    
    private void registerRenderers(RenderReportsCmd cmd) {
    	
    	for (Renderer renderer : ReportManager.instance.activeRenderers()) {
    	   cmd.registerRenderer(renderer, DefaultReportName + "." + renderer.defaultFileExtension());
       }
    }
    
    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public final void run(final IAction action) {
        log.info("Generation Report action requested");
        final ISelection sel = targetSelection();
        if (sel instanceof IStructuredSelection) {
            try {
                final IProject project = getProject((IStructuredSelection) sel);
                if (project != null) {
                    final RenderReportsCmd cmd = new RenderReportsCmd();
                    cmd.setProject(project);
                    cmd.setUserInitiated(true);
                    registerRenderers(cmd);
                    cmd.performExecute();
                }
            } catch (CommandException e) {
                showErrorById(StringKeys.ERROR_PMD_EXCEPTION, e);
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public final void selectionChanged(IAction action, ISelection selection) {
        // nothing to do
    }

    /**
     * Get a project from a selection
     * @param selection
     * @return
     */
    private static IProject getProject(final IStructuredSelection selection) {
        IProject project = null;
        final Object object = selection.getFirstElement();
        if (object != null && object instanceof IAdaptable) {
           final IResource resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);
           if (resource != null) {
               project = resource.getProject();
           }
        }

        return project;
    }

}
