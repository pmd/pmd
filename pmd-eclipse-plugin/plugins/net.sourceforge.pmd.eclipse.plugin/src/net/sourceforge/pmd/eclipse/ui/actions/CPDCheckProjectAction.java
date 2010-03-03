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
import net.sourceforge.pmd.eclipse.ui.dialogs.CPDCheckDialog;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.cpd.CPDView;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

/**
 * Process CPD action menu. Run CPD against the selected project.
 * 
 * @author Sven Jacob
 * @author David Craine
 * @author Philippe Herlin
 * 
 */
public class CPDCheckProjectAction extends AbstractUIAction {
    private static final Logger log = Logger.getLogger(CPDCheckProjectAction.class);
   
    private static final String XML_KEY = "XML";
    private static final String SIMPLE_KEY = "Simple Text";
    private static final String CSV_KEY = "CSV";

    /*
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(final IAction action) { // NOPMD:UnusedFormalParameter
        final IWorkbenchPartSite site = targetPartSite();
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
            final Iterator<?> i = ss.iterator();
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
            logError(getString(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION), e);
        }
    }

    /**
     * Shows the view.
     * @param matches
     */
    private CPDView showView() {
        CPDView view = null;
        try {
            final IWorkbenchPage workbenchPage = targetPartSite().getPage();
            view = (CPDView) workbenchPage.showView(PMDUiConstants.ID_CPDVIEW);
        } catch (PartInitException pie) {
            logError( getString(StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION), pie);
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
    
}
