package net.sourceforge.pmd.eclipse.properties;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.builder.PMDNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Property page to enable or disable PMD on a project
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/03/18 23:28:37  phherlin
 * *** keyword substitution change ***
 *
 */
public class PMDPropertyPage extends PropertyPage {
    private Button enablePMDButton;

    /**
     * Constructor for SamplePropertyPage.
     */
    public PMDPropertyPage() {
        super();
    }

    /**
     * Create the checkbox section
     * @param parent the parent composite
     */
    private void createWidgets(Composite parent) {
        enablePMDButton = new Button(parent, SWT.CHECK);
        enablePMDButton.setText(getMessage(PMDConstants.MSGKEY_ENABLE_BUTTON_LABEL));
        enablePMDButton.setSelection(isEnabled());
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        noDefaultAndApplyButton();
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new RowLayout());
        createWidgets(composite);
        return composite;
    }

    /**
     * User press OK Button
     */
    public boolean performOk() {
        boolean fEnabled = enablePMDButton.getSelection();
        if (fEnabled) {
            addPMDNature();
        } else {
            removePMDNature();
        }

        return true;
    }

    /**
     * Test if PMD is enable for this project
     */
    private boolean isEnabled() {
        boolean fEnabled = false;
        try {
            if (getElement() instanceof IProject) {
                IProject project = (IProject) getElement();
                fEnabled = project.hasNature(PMDNature.PMD_NATURE);
            }
        } catch (CoreException e) {
            fEnabled = false;
            MessageDialog.openError(
                getShell(),
                getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION) + e.toString());
        } finally {
            return fEnabled;
        }
    }

    /**
     * Add the PMD Nature to the project
     * @return false if nature cannot be added
     */
    private boolean addPMDNature() {
        boolean fNatureAdded = false;
        try {
            if (getElement() instanceof IProject) {
                IProject project = (IProject) getElement();
                ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
                progressDialog.run(true, true, new AddNatureTask(project));
                fNatureAdded = true;
            }
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Adding PMD nature interrupted", e);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().logError("Error adding PMD nature", e);
        } finally {
            return fNatureAdded;
        }
    }

    /**
     * Remove a PMD Nature from the project
     */
    private void removePMDNature() {
        try {
            if (getElement() instanceof IProject) {
                IProject project = (IProject) getElement();
                ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
                progressDialog.run(true, true, new RemoveNatureTask(project));
            }
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Removing PMD nature interrupted", e);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().logError("Error removing PMD nature", e);
        }
    }

    // For debug purpose
    //    private void traceProjectNaturesAndCommands(IProject project) {
    //        try {
    //            IProjectDescription description = project.getDescription();
    //            String[] natureIds = description.getNatureIds();
    //            System.out.println("Natures : ");
    //            for (int i = 0; i < natureIds.length; i++) {
    //                System.out.println("   " + natureIds[i]);
    //            }
    //            
    //            ICommand[] commands = description.getBuildSpec();
    //            System.out.println("Commands : ");
    //            for (int i = 0; i < commands.length; i++) {
    //                System.out.println("   " + commands[i].getBuilderName());
    //            }
    //        } catch (CoreException e) {
    //            e.printStackTrace();
    //        }
    //
    //    }

    /**
     * Private inner class to add nature to project
     */
    private class AddNatureTask implements IRunnableWithProgress {
        private IProject project;

        public AddNatureTask(IProject project) {
            this.project = project;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                if (!project.hasNature(PMDNature.PMD_NATURE)) {
                    IProjectDescription description = project.getDescription();
                    String[] natureIds = description.getNatureIds();
                    String[] newNatureIds = new String[natureIds.length + 1];
                    System.arraycopy(natureIds, 0, newNatureIds, 0, natureIds.length);
                    newNatureIds[natureIds.length] = PMDNature.PMD_NATURE;
                    description.setNatureIds(newNatureIds);
                    project.setDescription(description, monitor);
                }
            } catch (CoreException e) {
                MessageDialog.openError(
                    getShell(),
                    getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                    getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION) + e.toString());
            }
        }
    };

    /**
     * Private inner class to remove nature from project
     */
    private class RemoveNatureTask implements IRunnableWithProgress {
        private IProject project;

        public RemoveNatureTask(IProject project) {
            this.project = project;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                if (project.hasNature(PMDNature.PMD_NATURE)) {
                    IProjectDescription description = project.getDescription();
                    String[] natureIds = description.getNatureIds();
                    String[] newNatureIds = new String[natureIds.length - 1];
                    for (int i = 0, j = 0; i < natureIds.length; i++) {
                        if (!natureIds[i].equals(PMDNature.PMD_NATURE)) {
                            newNatureIds[j++] = natureIds[i];
                        }
                    }
                    description.setNatureIds(newNatureIds);
                    project.setDescription(description, monitor);
                    project.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                }
            } catch (CoreException e) {
                MessageDialog.openError(
                    getShell(),
                    getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                    getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION) + e.toString());
            }
        }
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }
}
