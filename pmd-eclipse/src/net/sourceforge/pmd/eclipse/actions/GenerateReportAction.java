package net.sourceforge.pmd.eclipse.actions;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;
import net.sourceforge.pmd.eclipse.cmd.RenderReportCmd;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Process GenerateReport action menu.
 * Generate a HTML report on the current project.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/04/20 23:15:53  phherlin
 * Implement reports generation RFE#1177802
 *
 *
 */
public class GenerateReportAction implements IObjectActionDelegate {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.actions.GenerateReportAction");
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public final void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public final void run(final IAction action) {
        log.info("Generation Report action requested");
        ISelection sel = targetPart.getSite().getSelectionProvider().getSelection();
        if (sel instanceof IStructuredSelection) {
            try {
                IProject project = getProject((IStructuredSelection) sel);
                if (project != null) {
                    RenderReportCmd cmd1 = new RenderReportCmd();
                    cmd1.setProject(project);
                    cmd1.setRenderer(new HTMLRenderer());
                    cmd1.setReportName(PMDPluginConstants.HTML_REPORT_NAME);
                    cmd1.performExecute();
                    
                    RenderReportCmd cmd2 = new RenderReportCmd();                    
                    cmd2.setProject(project);
                    cmd2.setRenderer(new CSVRenderer());
                    cmd2.setReportName(PMDPluginConstants.CSV_REPORT_NAME);
                    cmd2.performExecute();
                    
                    RenderReportCmd cmd3 = new RenderReportCmd();                    
                    cmd3.setProject(project);
                    cmd3.setRenderer(new XMLRenderer());
                    cmd3.setReportName(PMDPluginConstants.XML_REPORT_NAME);
                    cmd3.performExecute();
                    
                    RenderReportCmd cmd4 = new RenderReportCmd();                    
                    cmd4.setProject(project);
                    cmd4.setRenderer(new TextRenderer());
                    cmd4.setReportName(PMDPluginConstants.TXT_REPORT_NAME);
                    cmd4.performExecute();
                }
            } catch (CommandException e) {
                PMDPlugin.getDefault().showError(
                    PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_PMD_EXCEPTION),
                    e);
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public final void selectionChanged(IAction action, ISelection selection) {
    }
    
    /**
     * Get a project from a selection
     * @param selection
     * @return
     */
    private IProject getProject(final IStructuredSelection selection) {
        IProject project = null;
        Object object = selection.getFirstElement();
        if ((object != null) && (object instanceof IAdaptable)) {
           IResource resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);            
           if (resource != null) {
               project = resource.getProject();
           }
        }
        
        return project;
    }

}
