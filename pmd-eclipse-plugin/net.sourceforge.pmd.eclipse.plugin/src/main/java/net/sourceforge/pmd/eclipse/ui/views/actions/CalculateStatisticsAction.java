package net.sourceforge.pmd.eclipse.ui.views.actions;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.model.PackageRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.views.ViolationOverview;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class CalculateStatisticsAction extends AbstractPMDAction {
	
    public final ViolationOverview violationView;
    
    public CalculateStatisticsAction(ViolationOverview view) {
        super();
        
        violationView = view;
    }
    
    protected String imageId() { return PMDUiConstants.ICON_BUTTON_CALCULATE; }
    
    protected String tooltipMsgId() { return StringKeys.VIEW_TOOLTIP_CALCULATE_STATS; }
    
    private TreeViewer getViewer() {
    	return violationView.getViewer();
    }
    
    /**
     * Executes the Action
     */
    public void run() {
        try {
            final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            dialog.setCancelable(true);

            dialog.run(false, false, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    final TreeItem[] items = getViewer().getTree().getItems();
                    final int unitCount = calculateWorkUnits(items);
                    monitor.beginTask(getString(StringKeys.MONITOR_CALC_STATS_TASK), unitCount);
                    for (TreeItem item : items) {
                        if (monitor.isCanceled()) {
                            break;
                        }
                        if (item.getData() instanceof PackageRecord) {
                            final PackageRecord record = (PackageRecord) item.getData();
                            final AbstractPMDRecord[] children = record.getChildren();
                            monitor.subTask(getString(StringKeys.MONITOR_CALC_STATS_OF_PACKAGE) + ": " + record.getName());
                            for (AbstractPMDRecord kid : children) {
                                if (kid instanceof FileRecord) {
                                    calculateFileRecord((FileRecord)kid);
                                    monitor.worked(1);
                                }
                            }
                        } else if (item.getData() instanceof FileRecord) {
                            calculateFileRecord((FileRecord) item.getData());
                            monitor.worked(1);                            
                        }
                    }
                    
                    getViewer().refresh(true);
                }
            });
        } catch (InvocationTargetException e) {
            logErrorByKey(StringKeys.ERROR_INVOCATIONTARGET_EXCEPTION, e);
        } catch (InterruptedException e) {
        	logErrorByKey(StringKeys.ERROR_INTERRUPTED_EXCEPTION, e);
        }
    }
    
    private void calculateFileRecord(FileRecord fileRecord) {
        if (fileRecord.getLOC() == 0) {
            fileRecord.calculateLinesOfCode();
            fileRecord.calculateNumberOfMethods();
        }
    }

    private int calculateWorkUnits(TreeItem[] items) {
        int count = 0;
        for (TreeItem item : items) {
        	Object data = item.getData();
            if (data instanceof PackageRecord) {
                count += ((PackageRecord) data).getChildren().length;
            } else if (data instanceof FileRecord) {
            	count++;
            }
        }
        return count;
    }

}
