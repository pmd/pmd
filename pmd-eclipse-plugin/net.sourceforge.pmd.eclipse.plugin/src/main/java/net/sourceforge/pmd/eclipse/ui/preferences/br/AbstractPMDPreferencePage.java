package net.sourceforge.pmd.eclipse.ui.preferences.br;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.editors.SWTUtil;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class AbstractPMDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    protected IPreferences 			preferences;
    private boolean 				modified;

	protected static PMDPlugin		plugin = PMDPlugin.getDefault();

	protected abstract String descriptionId();

	/**
	 * Returns the isModified.
	 * @return boolean
	 */
	public boolean isModified() {
		return modified;
	}

	public void setModified() {
		setModified(true);
	}
	
	/**
	 * Sets the isModified.
	 * @param isModified The isModified to set
	 */
	public void setModified(boolean isModified) {
		modified = isModified;

		SWTUtil.setEnabled(getApplyButton(), modified);
		SWTUtil.setEnabled(getDefaultsButton(), !modified);
	}

    /**
     * Insert the method's description here.
     * @see PreferencePage#init
     */

    public void init(IWorkbench workbench) {
  //  	setDescription(getMessage(descriptionId()));
        preferences = PMDPlugin.getDefault().loadPreferences();
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {

        preferences.sync();

        return super.performOk();
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

    /**
     * If user wants to, rebuild all projects
     */
    protected void rebuildProjects() {
        if (MessageDialog.openQuestion(getShell(), getMessage(StringKeys.QUESTION_TITLE),
                getMessage(StringKeys.QUESTION_RULES_CHANGED))) {
            try {
                ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
                monitorDialog.run(true, true, new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        try {
                            ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
                        } catch (CoreException e) {
                            plugin.logError("Exception building all projects after a preference change", e);
                        }
                    }
                });
            } catch (Exception e) {
                plugin.logError("Exception building all projects after a preference change", e);
            }
        }
    }

}
