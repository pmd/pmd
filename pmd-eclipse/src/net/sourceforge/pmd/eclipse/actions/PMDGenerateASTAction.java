package net.sourceforge.pmd.eclipse.actions;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.eclipse.ASTWriter;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDEclipseException;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.WriterAbstractFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Process PMDGenerateAST action menu.
 * Generate a AST from the selected file.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.7  2003/11/30 22:57:37  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.6.2.1  2003/11/04 16:27:19  phherlin
 * Refactor to use the adaptable framework instead of downcasting
 *
 * Revision 1.6  2003/10/27 20:14:13  phherlin
 * Refactoring AST generation. Using a ASTWriter.
 *
 * Revision 1.5  2003/06/19 20:59:45  phherlin
 * In the generated XML AST, put the image information on an image attribute instead of the tag body
 *
 * Revision 1.4  2003/05/19 22:27:33  phherlin
 * Refactoring to improve performance
 *
 * Revision 1.3  2003/03/30 20:48:59  phherlin
 * Adding logging
 * Displaying error dialog in a thread safe way
 *
 */
public class PMDGenerateASTAction implements IObjectActionDelegate, IRunnableWithProgress {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.actions.PMDGenerateASTAction");
    private IWorkbenchPart targetPart;
    private IStructuredSelection structuredSelection;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        log.info("Generation AST action requested");
        ISelection sel = targetPart.getSite().getSelectionProvider().getSelection();
        if (sel instanceof IStructuredSelection) {
            this.structuredSelection = (IStructuredSelection) sel;
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
            try {
                dialog.run(false, false, this);
            } catch (InvocationTargetException e) {
                PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
            } catch (InterruptedException e) {
                PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_INTERRUPTED_EXCEPTION), e);
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Generate a AST for a file
     * @param file a file
     */
    private void generateAST(IFile file) {
        log.info("Genrating AST for file " + file.getName());
        try {
            JavaParser parser = new JavaParser(file.getContents());
            ASTCompilationUnit compilationUnit = parser.CompilationUnit();
            StringWriter stringWriter = new StringWriter();
            ASTWriter astWriter = WriterAbstractFactory.getFactory().getASTWriter();
            astWriter.write(stringWriter, compilationUnit);
            stringWriter.flush();

            String name = file.getName();
            int dotPosition = name.indexOf('.');
            String astName = name.substring(0, dotPosition) + ".ast";

            IFile astFile = null;
            IContainer parent = file.getParent();
            if (parent instanceof IFolder) {
                astFile = ((IFolder) parent).getFile(astName);
            } else if (parent instanceof IProject) {
                astFile = ((IProject) parent).getFile(astName);
            }

            if (astFile != null) {
                if (astFile.exists()) {
                    astFile.delete(false, null);
                }
                ByteArrayInputStream astInputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
                astFile.create(astInputStream, false, null);
            }

        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        } catch (ParseException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_PMD_EXCEPTION), e);
        } catch (PMDEclipseException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_PMD_EXCEPTION), e);
        }
    }

    /**
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("", this.structuredSelection.size());
        for (Iterator i = this.structuredSelection.iterator(); i.hasNext();) {
            Object element = i.next();
            if (element instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) element;
                IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                if (resource != null) {
                    monitor.subTask(resource.getName());
                    generateAST((IFile) resource);
                    monitor.worked(1);
                } else {
                    log.warn("The selected object cannot adapt to a resource");
                    log.debug("   -> selected object : " + element);
                }
            } else {
                log.warn("The selected object is not adaptable");
                log.debug("   -> selected object : " + element);
            }
        }
    }

}
