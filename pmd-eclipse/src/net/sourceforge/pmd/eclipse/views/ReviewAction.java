package net.sourceforge.pmd.eclipse.views;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Date;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Mark a violation as reviewed
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2003/12/09 00:14:59  phherlin
 * Merging from v2 development
 *
 * Revision 1.3  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/11/30 21:16:16  phherlin
 * Adapting to Eclipse v3
 *
 * Revision 1.1  2003/08/14 16:10:42  phherlin
 * Implementing Review feature (RFE#787086)
 *
 */
public class ReviewAction extends Action {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.views.ReviewAction");
    private IProgressMonitor monitor;
    private ViolationView violationView;

    /**
     * Constructor
     */
    public ReviewAction(ViolationView violationView) {
        this.violationView = violationView;
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        final IMarker[] markers = violationView.getSelectedViolations();
        if (markers != null) {

            // Get confirmation if multiple markers are selected
            boolean go = true;
            if (markers.length > 1) {
                String title = PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_CONFIRM_TITLE);
                String message = PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_CONFIRM_REVIEW_MULTIPLE_MARKERS);
                Shell shell = Display.getCurrent().getActiveShell();
                go = MessageDialog.openConfirm(shell, title, message);
            }

            // If only one marker selected or user has confirmed, review violation
            if (go) {
                try {
                    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
                    dialog.run(false, false, new IRunnableWithProgress() {
                        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                            setMonitor(monitor);
                            monitor.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_MONITOR_REVIEW, ""), 5);
                            insertReview(markers[0]);
                            monitor.done();
                        }
                    });
                } catch (InvocationTargetException e) {
                    PMDPlugin.getDefault().logError(getMessage(PMDConstants.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
                } catch (InterruptedException e) {
                    PMDPlugin.getDefault().logError(getMessage(PMDConstants.MSGKEY_ERROR_INTERRUPTED_EXCEPTION), e);
                }
            }
        }
    }

    /**
     * Do the insertion of the review comment
     * @param marker
     */
    protected void insertReview(IMarker marker) {
        try {
            IResource resource = marker.getResource();
            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                if (file.exists()) {
                    String sourceCode = readFile(file);

                    monitorWorked();

                    int offset = getMarkerLineStart(sourceCode, marker.getAttribute(IMarker.LINE_NUMBER, 0));

                    monitorWorked();

                    sourceCode = addReviewComment(sourceCode, offset, marker);

                    monitorWorked();

                    file.setContents(new ByteArrayInputStream(sourceCode.getBytes()), false, true, getMonitor());

                    monitorWorked();
                } else {
                    MessageDialog.openError(
                        Display.getCurrent().getActiveShell(),
                        getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                        "The file " + file.getName() + " doesn't exists ! Review aborted. Try to refresh the workspace and retry.");
                }

            }
        } catch (JavaModelException e) {
            IJavaModelStatus status = e.getJavaModelStatus();
            PMDPlugin.getDefault().logError(status);
            log.warn("Ignoring Java Model Exception : " + status.getMessage() );
            if (log.isDebugEnabled()) {
                log.debug("   code : " + status.getCode());
                log.debug("   severity : " + status.getSeverity());
                IJavaElement[] elements = status.getElements();
                for (int i = 0; i < elements.length; i++) {
                    log.debug("   element : " + elements[i].getElementName() + " (" + elements[i].getElementType() + ")");
                }
            }
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        } catch (IOException e) {
            PMDPlugin.getDefault().logError(getMessage(PMDConstants.MSGKEY_ERROR_IO_EXCEPTION), e);
        }
    }

    /**
     * Get the monitor
     * @return
     */
    protected IProgressMonitor getMonitor() {
        return monitor;
    }

    /**
     * Set the monitor
     * @param monitor
     */
    protected void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
    
    /**
     * Progress monitor
     */
    private void monitorWorked() {
        if (getMonitor() != null) {
            getMonitor().worked(1);
        }
    }

    /**
     * Renvoie la position dans le code source du début de la ligne du marqueur
     */
    private int getMarkerLineStart(String sourceCode, int lineNumber) {
        int lineStart = 0;
        int currentLine = 1;
        for (lineStart = 0; lineStart < sourceCode.length(); lineStart++) {
            if (currentLine == lineNumber) {
                break;
            } else {
                if (sourceCode.charAt(lineStart) == '\n') {
                    currentLine++;
                }
            }
        }

        if (sourceCode.charAt(lineStart) == '\r') {
            lineStart++;
        }

        return lineStart;
    }

    /**
     * Insère un commentaire de révision juste au dessus de la ligne du marker
     */
    private String addReviewComment(String sourceCode, int offset, IMarker marker) {
        String additionalCommentPattern = PMDPlugin.getDefault().getReviewAdditionalComment();
        String additionalComment =
            MessageFormat.format(additionalCommentPattern, new Object[] { System.getProperty("user.name", ""), new Date()});

        StringBuffer sb = new StringBuffer(sourceCode.substring(0, offset));
        sb.append(computeIndent(sourceCode, offset));
        sb.append(PMDPlugin.REVIEW_MARKER);
        sb.append(marker.getAttribute(PMDPlugin.KEY_MARKERATT_RULENAME, ""));
        sb.append(": ");
        sb.append(additionalComment);
        sb.append(System.getProperty("line.separator"));
        sb.append(sourceCode.substring(offset));

        return sb.toString();
    }

    /**
     * Calcul l'indentation employée sur la ligne du marker
     */
    private String computeIndent(String sourceCode, int offset) {
        StringBuffer indent = new StringBuffer();
        int i = 0;

        while (Character.isWhitespace(sourceCode.charAt(offset + i))) {
            indent.append(sourceCode.charAt(offset + i));
            i++;
        }

        return indent.toString();
    }

    /**
     * Helper method to find message string
     * @param messageId a message id
     * @return the localized message OR the id if not found
     */    
    private String getMessage(String messageId) {
        return PMDPlugin.getDefault().getMessage(messageId, messageId);
    }
    
    private String readFile(IFile file) throws IOException, CoreException {
        InputStream contents = file.getContents(true);
        InputStreamReader reader = new InputStreamReader(contents);
        
        try {
            char[] buffer = new char[4096];
            StringBuffer stringBuffer = new StringBuffer(4096);
            while (reader.ready()) {
                int readCount = reader.read(buffer);
                if (readCount != -1) {
                    stringBuffer.append(buffer, 0, readCount);
                }
            }
        
            return stringBuffer.toString();

        } finally {
            reader.close();
        }
    }

}
