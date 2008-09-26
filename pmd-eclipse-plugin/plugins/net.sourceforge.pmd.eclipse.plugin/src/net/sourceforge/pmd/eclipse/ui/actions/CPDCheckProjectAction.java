package net.sourceforge.pmd.eclipse.ui.actions;

import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.cpd.CSVRenderer;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Renderer;
import net.sourceforge.pmd.cpd.SimpleRenderer;
import net.sourceforge.pmd.cpd.XMLRenderer;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.cmd.DetectCutAndPasteCmd;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.dialogs.CPDCheckDialog;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.CPDView;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

/**
 * Process CPD action menu. Run CPD against the selected project.
 * 
 * @author Sven Jacob
 * @author David Craine
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.5  2006/11/18 14:47:13  holobender
 * some little improvements for cpd view
 *
 * Revision 1.4  2006/11/16 17:09:40  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 * Revision 1.3  2006/10/10 22:31:01  phherlin
 * Fix other PMD warnings
 *
 * Revision 1.2  2006/06/20 21:01:23  phherlin
 * Enable PMD and fix error level violations
 *
 * Revision 1.1  2006/05/22 21:23:56  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 * Revision 1.2 2006/05/02 18:34:23 phherlin Make CPD "working set aware"
 * 
 * Revision 1.1 2005/05/31 23:04:11 phherlin Fix Bug 1190624: refactor CPD integration
 * 
 * Revision 1.8 2004/04/19 22:25:12 phherlin Upgrading to PMD v1.6
 * 
 * Revision 1.7 2003/11/30 22:57:37 phherlin Merging from eclipse-v2 development branch
 * 
 * Revision 1.6.2.1 2003/11/04 16:27:19 phherlin Refactor to use the adaptable framework instead of downcasting
 * 
 * Revision 1.6 2003/06/30 20:16:06 phherlin Redesigning plugin configuration
 * 
 * Revision 1.5 2003/05/19 22:26:07 phherlin Updating PMD engine to v1.05 Fixing CPD usage to conform to new engine implementation
 * 
 */
public class CPDCheckProjectAction implements IObjectActionDelegate {
    private static final Logger log = Logger.getLogger(CPDCheckProjectAction.class);
    private IWorkbenchPart targetPart;

    private static final String XML_KEY = "XML";
    private static final String SIMPLE_KEY = "Simple Text";
    private static final String CSV_KEY = "CSV";
    
    /*
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) { // NOPMD:UnusedFormalParameter
        this.targetPart = targetPart;
    }

    /*
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(final IAction action) { // NOPMD:UnusedFormalParameter
        final IWorkbenchPartSite site = targetPart.getSite();
        final ISelection sel = site.getSelectionProvider().getSelection();
        final Shell shell = site.getShell();
        final String[] languages = LanguageFactory.supportedLanguages;
        
        final String[] formats = {
                SIMPLE_KEY,
                XML_KEY,
                CSV_KEY
        };
        
        final CPDCheckDialog dialog = new CPDCheckDialog(shell, languages, formats);
        
        if (dialog.open() == Dialog.OK && sel instanceof IStructuredSelection) {       
            final StructuredSelection ss = (StructuredSelection) sel;
            final Iterator i = ss.iterator();
            while (i.hasNext()) {
                final Object obj = i.next();
                if (obj instanceof IAdaptable) {
                    final IAdaptable adaptable = (IAdaptable) obj;
                    final IProject project = (IProject) adaptable.getAdapter(IProject.class);
                    
                    if (project == null) {
                        log.warn("The selected object cannot adapt to a project");
                        log.debug("   -> selected object : " + obj);
                    } else {
                        this.detectCutAndPaste(project, dialog);
                    }
                } else {
                    log.warn("The selected object is not adaptable");
                    log.debug("   -> selected object : " + obj);
                }
            }           
        }
    }


    /*
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(final IAction action, final ISelection selection) { // NOPMD:UnusedFormalParameter
    }

    /**
     * Run the DetectCutAndPaste command against the selected project 
     * and creates optionally the report file.
     * 
     * @param project a project
     * @param dialog the object of the dialog with the selected values
     * @throws CommandException 
     */
    private void detectCutAndPaste(final IProject project, CPDCheckDialog dialog) {
        final String selectedLanguage = dialog.getSelectedLanguage();
        final int tilesize = dialog.getTileSize();
        final boolean createReport = dialog.isCreateReportSelected();
        final Renderer selectedRenderer = this.createRenderer(dialog.getSelectedFormat());
        final String fileName = this.createFileName(dialog.getSelectedFormat());
        final CPDView view = showView();

        try {
            final DetectCutAndPasteCmd detectCmd = new DetectCutAndPasteCmd();
            detectCmd.setProject(project);
            detectCmd.setCreateReport(createReport);
            detectCmd.setLanguage(selectedLanguage);
            detectCmd.setMinTileSize(tilesize);
            detectCmd.setRenderer(selectedRenderer);
            detectCmd.setReportName(fileName);
            detectCmd.setUserInitiated(true);
            detectCmd.addPropertyListener(view);
            detectCmd.performExecute();
        } catch (CommandException e) {
            PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION), e);
        }
    }

    /**
     * Shows the view.
     * @param matches
     */
    private CPDView showView() {
        CPDView view = null;
        try {
            final IWorkbenchPage workbenchPage = targetPart.getSite().getPage();
            view = (CPDView) workbenchPage.showView(PMDUiConstants.ID_CPDVIEW);
        } catch (PartInitException pie) {
            PMDPlugin.getDefault().logError(
                getString(StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION), pie);
        } 
        return view;
    }
      
    /**
     * Creates a renderer from a key.
     * @param rendererKey xml, simple or cvs key
     * @return Renderer
     */
    private Renderer createRenderer(final String rendererKey) {
        Renderer renderer = null;
        if (XML_KEY.equals(rendererKey)) {
            renderer = new XMLRenderer();
        } else if (SIMPLE_KEY.equals(rendererKey)) {
            renderer = new SimpleRenderer();
        } else if (CSV_KEY.equals(rendererKey)) {
            renderer = new CSVRenderer();
        }
        return renderer;
    }
    
    /**
     * Creates a filename according to the renderer.
     * @param rendererKey xml, simple or cvs key
     * @return file name
     */
    private String createFileName(String rendererKey) {
        String fileName = null;
        if (XML_KEY.equals(rendererKey)) {
            fileName = PMDRuntimeConstants.XML_CPDREPORT_NAME;
        } else if (CSV_KEY.equals(rendererKey)) {
            fileName = PMDRuntimeConstants.CSV_CPDREPORT_NAME;
        } else {
            fileName = PMDRuntimeConstants.SIMPLE_CPDREPORT_NAME;
        }
        return fileName;
    }
    
    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
