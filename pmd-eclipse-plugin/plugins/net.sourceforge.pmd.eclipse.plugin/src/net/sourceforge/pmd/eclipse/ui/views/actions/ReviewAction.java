package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Date;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Mark a violation as reviewed
 *
 * @author Philippe Herlin
 *
 */
public class ReviewAction extends ViolationSelectionAction {
    private static final Logger log = Logger.getLogger(ReviewAction.class);
    private IProgressMonitor monitor;

    /**
     * Constructor
     */
    public ReviewAction(TableViewer viewer) {
        super(viewer);

        setImageDescriptor(PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_REVIEW));
        setText(getString(StringKeys.MSGKEY_VIEW_ACTION_REVIEW));
        setToolTipText(getString(StringKeys.MSGKEY_VIEW_TOOLTIP_REVIEW));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        final IMarker[] markers = getSelectedViolations();
        final boolean reviewPmdStyle = PMDPlugin.getDefault().loadPreferences().isReviewPmdStyleEnabled();

        if (markers != null) {

            // Get confirmation if multiple markers are selected
            // Not necessary when using PMD style
            boolean go = true;
            if (markers.length > 1 && !reviewPmdStyle) {
                String title = getString(StringKeys.MSGKEY_CONFIRM_TITLE);
                String message = getString(StringKeys.MSGKEY_CONFIRM_REVIEW_MULTIPLE_MARKERS);
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
                            monitor.beginTask(getString(StringKeys.MSGKEY_MONITOR_REVIEW), 5);
                            insertReview(markers[0], reviewPmdStyle);
                            monitor.done();
                        }
                    });
                } catch (InvocationTargetException e) {
                    PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
                } catch (InterruptedException e) {
                    PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_INTERRUPTED_EXCEPTION), e);
                }
            }
        }
    }

    /**
     * Do the insertion of the review comment
     *
     * @param marker
     */
    protected void insertReview(IMarker marker, boolean reviewPmdStyle) {
        try {
            IResource resource = marker.getResource();
            if (resource instanceof IFile) {
                IFile file = (IFile) resource;
                if (file.exists()) {
                    String sourceCode = readFile(file);

                    monitorWorked();

                    int offset = getMarkerLineStart(sourceCode, marker.getAttribute(IMarker.LINE_NUMBER, 0));

                    monitorWorked();

                    if (reviewPmdStyle) {
                        sourceCode = addPmdReviewComment(sourceCode, offset, marker);
                    } else {
                        sourceCode = addPluginReviewComment(sourceCode, offset, marker);
                    }

                    monitorWorked();

                    file.setContents(new ByteArrayInputStream(sourceCode.getBytes()), false, true, getMonitor());

                    monitorWorked();
                } else {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), getString(StringKeys.MSGKEY_ERROR_TITLE),
                            "The file " + file.getName()
                                    + " doesn't exists ! Review aborted. Try to refresh the workspace and retry.");
                }

            }
        } catch (JavaModelException e) {
            IJavaModelStatus status = e.getJavaModelStatus();
            PMDPlugin.getDefault().logError(status);
            log.warn("Ignoring Java Model Exception : " + status.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("   code : " + status.getCode());
                log.debug("   severity : " + status.getSeverity());
                IJavaElement[] elements = status.getElements();
                for (int i = 0; i < elements.length; i++) {
                    log.debug("   element : " + elements[i].getElementName() + " (" + elements[i].getElementType() + ")");
                }
            }
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION), e);
        } catch (IOException e) {
            PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_IO_EXCEPTION), e);
        }
    }

    /**
     * Get the monitor
     *
     * @return
     */
    protected IProgressMonitor getMonitor() {
        return monitor;
    }

    /**
     * Set the monitor
     *
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
     * Insert a review comment with the Plugin style
     */
    private String addPluginReviewComment(String sourceCode, int offset, IMarker marker) {
        String additionalCommentPattern = PMDPlugin.getDefault().loadPreferences().getReviewAdditionalComment();
        String additionalComment = MessageFormat.format(additionalCommentPattern, new Object[] {
                System.getProperty("user.name", ""), new Date() });

        // Copy the source code until the violation line not included
        StringBuffer sb = new StringBuffer(sourceCode.substring(0, offset));

        // Add the review comment
        sb.append(computeIndent(sourceCode, offset));
        sb.append(PMDRuntimeConstants.PLUGIN_STYLE_REVIEW_COMMENT);
        sb.append(marker.getAttribute(PMDUiConstants.KEY_MARKERATT_RULENAME, ""));
        sb.append(": ");
        sb.append(additionalComment);
        sb.append(System.getProperty("line.separator"));

        // Copy the rest of the source code
        sb.append(sourceCode.substring(offset));

        return sb.toString();
    }

    /**
     * Insert a review comment with the PMD style
     */
    private String addPmdReviewComment(String sourceCode, int offset, IMarker marker) {
        String result = sourceCode;
        String additionalCommentPattern = PMDPlugin.getDefault().loadPreferences().getReviewAdditionalComment();
        String additionalComment = MessageFormat.format(additionalCommentPattern, new Object[] {
                System.getProperty("user.name", ""), new Date() });

        // Find the end of line
        int index = sourceCode.substring(offset).indexOf('\r');
        if (index == -1) {
            index = sourceCode.substring(offset).indexOf('\n');
            if (index == -1) {
                index = sourceCode.substring(offset).length();
            }
        }
        index += offset;

        // Insert comment only if it does not already exist
        if (sourceCode.substring(offset, index).indexOf(PMDRuntimeConstants.PMD_STYLE_REVIEW_COMMENT) == -1) {

            // Copy the source code until the violation line included
            StringBuffer sb = new StringBuffer(sourceCode.substring(0, index));

            // Add the review comment
            sb.append(' ');
            sb.append(PMDRuntimeConstants.PMD_STYLE_REVIEW_COMMENT);
            sb.append(' ');
            sb.append(additionalComment);

            // Copy the rest of the code
            sb.append(sourceCode.substring(index));
            result = sb.toString();
        }

        return result;
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

    /**
     * Helper mehod to retreive an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
