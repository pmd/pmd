package net.sourceforge.pmd.eclipse.actions;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Process PMDGenerateAST action menu.
 * Generate a AST from the selected file.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2003/03/30 20:48:59  phherlin
 * Adding logging
 * Displaying error dialog in a thread safe way
 *
 */
public class PMDGenerateASTAction implements IObjectActionDelegate {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.actions.PMDGenerateASTAction");
    private IWorkbenchPart targetPart;
    private static final String INDENT = "\t";

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
            IStructuredSelection structuredSel = (IStructuredSelection) sel;
            for (Iterator i = structuredSel.iterator(); i.hasNext();) {
                Object element = i.next();
                if (element instanceof IFile) {
                    generateAST((IFile) element);
                } else if (element instanceof ICompilationUnit) {
                    IResource resource = ((ICompilationUnit) element).getResource();
                    generateAST((IFile) resource);
                } // else no processing for other types
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
            SimpleNode root = parser.CompilationUnit();
            StringWriter sr = new StringWriter();
            PrintWriter pr = new PrintWriter(sr);
            pr.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            dump(root, pr, "");
            pr.flush();

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
                ByteArrayInputStream astInputStream = new ByteArrayInputStream(sr.toString().getBytes());
                astFile.create(astInputStream, false, null);
            }

        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        } catch (ParseException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_PMD_EXCEPTION), e);
        }
    }

    /**
     * Dump a node and its subnodes -> recursive
     * @param node a node
     * @param out a print writer to write into
     * @param prefix texte to print before the name of the node
     */
    private void dump(SimpleNode node, PrintWriter out, String prefix) {
        StringBuffer sb = new StringBuffer(prefix);
        sb.append("<").append(node.toString());

        if (node.jjtGetNumChildren() == 0) {
            if (node.getImage() != null) {
                sb.append(">").append(node.getImage()).append("</").append(node.toString()).append(">");
            } else {
                sb.append("/>");
            }
            out.println(sb.toString());
        } else {
            sb
                .append(" beginLine=\"")
                .append(node.getBeginLine())
                .append("\"")
                .append(" beginColumn=\"")
                .append(node.getBeginColumn())
                .append("\"")
                .append(" endLine=\"")
                .append(node.getEndLine())
                .append("\"")
                .append(" endColumn=\"")
                .append(node.getEndColumn())
                .append("\"")
                .append(">");

            if (node.getImage() != null) {
                sb.append(System.getProperty("line.separator")).append(prefix).append(INDENT).append(node.getImage());
            }
            out.println(sb.toString());

            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                dump((SimpleNode) node.jjtGetChild(i), out, prefix + INDENT);
            }

            out.println(prefix + "</" + node.toString() + ">");
        }
    }

}
