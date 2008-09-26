package net.sourceforge.pmd.ui.views.actions;

import java.lang.reflect.InvocationTargetException;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.runtime.cmd.ReviewCodeCmd;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

/**
 * Action for reviewing one single resource.
 * @author Sven Jacob
 *
 */
public class ReviewResourceAction extends Action {
    private IProgressMonitor monitor;
    private IResource resource;
    
    /**
     * Constructor
     */
    public ReviewResourceAction(IResource resource) {
        super();
        setImageDescriptor(PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_REFRESH));
        setToolTipText(getString(StringKeys.MSGKEY_VIEW_TOOLTIP_REFRESH));
        this.resource = resource;
    }
    
    public void setResource(IResource resource) {
        this.resource = resource;
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            dialog.run(false, false, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    setMonitor(monitor);
                    monitor.beginTask(getString(StringKeys.MSGKEY_MONITOR_REVIEW), 5);
                    ReviewCodeCmd cmd = new ReviewCodeCmd();
                    cmd.addResource(resource);
                    cmd.setStepsCount(1);
                    cmd.setTaskMarker(true);
                    cmd.setUserInitiated(true);
                    try {
                        cmd.performExecute();
                    } catch (CommandException e) {
                        PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION), e);
                    }
                    monitor.done();
                }
            });
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError(getString(StringKeys.MSGKEY_ERROR_INTERRUPTED_EXCEPTION), e);
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
     * Helper mehod to retreive an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
