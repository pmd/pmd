package net.sourceforge.pmd.eclipse.properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDEclipseException;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.RuleSetWriter;
import net.sourceforge.pmd.eclipse.WriterAbstractFactory;
import net.sourceforge.pmd.eclipse.builder.PMDNature;
import net.sourceforge.pmd.eclipse.preferences.PMDPreferencePage;
import net.sourceforge.pmd.eclipse.preferences.RuleLabelProvider;
import net.sourceforge.pmd.eclipse.preferences.RuleSetContentProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Property page to enable or disable PMD on a project
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.12  2003/12/18 23:58:37  phherlin
 * Fixing malformed UTF-8 characters in generated xml files
 *
 * Revision 1.11  2003/11/30 22:57:44  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.9.2.6  2003/11/25 21:48:18  phherlin
 * Delete import javax.mail.Session !
 *
 * Revision 1.9.2.5  2003/11/07 14:33:57  phherlin
 * Implementing the "project ruleset" feature
 *
 * Revision 1.9.2.4  2003/11/04 16:27:19  phherlin
 * Refactor to use the adaptable framework instead of downcasting
 *
 * Revision 1.9.2.3  2003/11/04 13:26:38  phherlin
 * Implement the working set feature (working set filtering)
 *
 * Revision 1.9.2.2  2003/10/31 16:53:55  phherlin
 * Implementing lazy check feature
 *
 * Revision 1.9.2.1  2003/10/29 14:26:06  phherlin
 * Refactoring JDK 1.3 compatibility feature. Now use the compiler compliance option.
 *
 * Revision 1.9  2003/09/29 22:38:09  phherlin
 * Adding and implementing "JDK13 compatibility" property.
 *
 * Revision 1.8  2003/08/13 20:09:40  phherlin
 * Refactoring private->protected to remove warning about non accessible member access in enclosing types
 *
 * Revision 1.7  2003/07/07 19:27:52  phherlin
 * Making rules selectable from projects
 *
 * Revision 1.6  2003/07/01 20:22:16  phherlin
 * Make rules selectable from projects
 *
 * Revision 1.5  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 * Revision 1.4  2003/06/19 21:01:13  phherlin
 * Force a rebuild when PMD properties have changed
 *
 * Revision 1.3  2003/03/30 20:52:17  phherlin
 * Adding logging
 * Displaying error dialog in a thread safe way
 *
 */
public class PMDPropertyPage extends PropertyPage {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.properties.PMDPropertyPage");
    private Button enablePMDButton;
    protected TableViewer availableRulesTableViewer;
    private IWorkingSet selectedWorkingSet;
    private Label selectedWorkingSetLabel;
    private Button deselectWorkingSetButton;
    protected Button ruleSetStoredInProjectButton;

    /**
     * Constructor for SamplePropertyPage.
     */
    public PMDPropertyPage() {
        super();
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        log.info("PMD properties editing requested");
        Composite composite = null;
        noDefaultAndApplyButton();

        if (((IProject) getElement()).isAccessible()) {
            composite = new Composite(parent, SWT.NONE);

            GridLayout layout = new GridLayout();
            composite.setLayout(layout);

            enablePMDButton = buildEnablePMDButton(composite);

            Label separator = new Label(composite, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
            GridData data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            separator.setLayoutData(data);

            selectedWorkingSetLabel = buildSelectedWorkingSetLabel(composite);
            data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            selectedWorkingSetLabel.setLayoutData(data);

            Composite workingSetPanel = new Composite(composite, SWT.NONE);
            RowLayout rowLayout = new RowLayout();
            rowLayout.type = SWT.HORIZONTAL;
            rowLayout.justify = true;
            rowLayout.pack = false;
            rowLayout.wrap = false;
            workingSetPanel.setLayout(rowLayout);
            buildSelectWorkingSetButton(workingSetPanel);
            deselectWorkingSetButton = buildDeselectWorkingSetButton(workingSetPanel);

            separator = new Label(composite, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
            data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            separator.setLayoutData(data);

            buildLabel(composite, PMDConstants.MSGKEY_PROPERTY_LABEL_SELECT_RULE);
            Table availableRulesTable = buildAvailableRulesTableViewer(composite);
            data = new GridData();
            data.grabExcessHorizontalSpace = true;
            data.grabExcessVerticalSpace = true;
            data.horizontalAlignment = GridData.FILL;
            data.verticalAlignment = GridData.FILL;
            data.heightHint = 50;
            availableRulesTable.setLayoutData(data);

            ruleSetStoredInProjectButton = buildStoreRuleSetInProjectButton(composite);
        } else {
            setValid(false);
        }

        return composite;
    }

    /**
     * Create the enable PMD checkbox
     * @param parent the parent composite
     */
    private Button buildEnablePMDButton(Composite parent) {
        Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(PMDConstants.MSGKEY_PROPERTY_BUTTON_ENABLE));
        button.setSelection(isEnabled());

        return button;
    }

    /**
     * Create the checkbox for storing configuration in a project file
     * @param parent the parent composite
     */
    private Button buildStoreRuleSetInProjectButton(Composite parent) {
        Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(PMDConstants.MSGKEY_PROPERTY_BUTTON_STORE_RULESET_PROJECT));
        button.setSelection(isRuleSetStoredInProject());

        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Table ruleTable = availableRulesTableViewer.getTable();
                ruleTable.setEnabled(!ruleSetStoredInProjectButton.getSelection());
            }
        });

        return button;
    }

    /**
     * Build a label
     */
    private Label buildLabel(Composite parent, String msgKey) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(msgKey == null ? "" : getMessage(msgKey));
        return label;
    }

    /**
     * Build a label
     */
    private Label buildSelectedWorkingSetLabel(Composite parent) {
        if (getElement() instanceof IProject) {
            this.selectedWorkingSet = PMDPlugin.getDefault().getProjectWorkingSet((IProject) getElement());
        }

        Label label = new Label(parent, SWT.NONE);
        label.setText(
            this.selectedWorkingSet == null
                ? getMessage(PMDConstants.MSGKEY_PROPERTY_LABEL_NO_WORKINGSET)
                : (getMessage(PMDConstants.MSGKEY_PROPERTY_LABEL_SELECTED_WORKINGSET) + selectedWorkingSet.getName()));

        return label;
    }

    /**
     * Build rule table viewer
     */
    private Table buildAvailableRulesTableViewer(Composite parent) {
        int tableStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK;
        availableRulesTableViewer = new TableViewer(parent, tableStyle);

        Table ruleTable = availableRulesTableViewer.getTable();
        TableColumn ruleNameColumn = new TableColumn(ruleTable, SWT.LEFT);
        ruleNameColumn.setResizable(true);
        ruleNameColumn.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_COLUMN_NAME));
        ruleNameColumn.setWidth(200);

        TableColumn rulePriorityColumn = new TableColumn(ruleTable, SWT.LEFT);
        rulePriorityColumn.setResizable(true);
        rulePriorityColumn.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_COLUMN_PRIORITY));
        rulePriorityColumn.setWidth(110);

        TableColumn ruleDescriptionColumn = new TableColumn(ruleTable, SWT.LEFT);
        ruleDescriptionColumn.setResizable(true);
        ruleDescriptionColumn.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_COLUMN_DESCRIPTION));
        ruleDescriptionColumn.setWidth(200);

        ruleTable.setLinesVisible(true);
        ruleTable.setHeaderVisible(true);

        availableRulesTableViewer.setContentProvider(new RuleSetContentProvider());
        availableRulesTableViewer.setLabelProvider(new RuleLabelProvider());
        availableRulesTableViewer.setColumnProperties(
            new String[] {
                PMDPreferencePage.PROPERTY_NAME,
                PMDPreferencePage.PROPERTY_PRIORITY,
                PMDPreferencePage.PROPERTY_DESCRIPTION });

        availableRulesTableViewer.setSorter(new ViewerSorter() {
            public int compare(Viewer viewer, Object e1, Object e2) {
                int result = 0;
                if ((e1 instanceof Rule) && (e2 instanceof Rule)) {
                    result = ((Rule) e1).getName().compareTo(((Rule) e2).getName());
                }
                return result;
            }

            public boolean isSorterProperty(Object element, String property) {
                return property.equals(PMDPreferencePage.PROPERTY_NAME);
            }
        });

        populateAvailableRulesTable();
        ruleTable.setEnabled(!isRuleSetStoredInProject());

        return ruleTable;
    }

    /**
     * Build the working set selection button
     * @param parent
     */
    private void buildSelectWorkingSetButton(Composite parent) {
        Button workingSetButton = new Button(parent, SWT.PUSH);
        workingSetButton.setText(getMessage(PMDConstants.MSGKEY_PROPERTY_BUTTON_SELECT_WORKINGSET));
        workingSetButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                selectWorkingSet();
            }
        });
    }

    /**
     * Build the working set deselect button
     * @param parent
     */
    private Button buildDeselectWorkingSetButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(getMessage(PMDConstants.MSGKEY_PROPERTY_BUTTON_DESELECT_WORKINGSET));
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                deselectWorkingSet();
            }
        });

        button.setEnabled(selectedWorkingSet != null);

        return button;
    }

    /**
     * Populate the rule table
     */
    private void populateAvailableRulesTable() {
        RuleSet configuredRuleSet = PMDPlugin.getDefault().getRuleSet();
        availableRulesTableViewer.setInput(configuredRuleSet);

        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            RuleSet activeRuleSet = PMDPlugin.getDefault().getRuleSetForResourceFromProperties(resource, true);
            Set activeRules = activeRuleSet.getRules();

            TableItem[] itemList = availableRulesTableViewer.getTable().getItems();
            for (int i = 0; i < itemList.length; i++) {
                Object rule = itemList[i].getData();
                if (activeRules.contains(rule)) {
                    itemList[i].setChecked(true);
                }
            }
        }
    }

    /**
     * User press OK Button
     */
    public boolean performOk() {
        log.info("Properties editing accepted");
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if ((resource != null) && (resource.isAccessible())) {

            boolean forceRebuild = storeRuleSetStoredInProject();
            boolean flag = storeRuleSelection();
            if (!this.ruleSetStoredInProjectButton.getSelection()) {
                forceRebuild |= flag;
            }

            storeSelectedWorkingSet();
            boolean fEnabled = enablePMDButton.getSelection();

            if (fEnabled) {
                forceRebuild |= addPMDNature();
                if (forceRebuild) {
                    rebuildProject();
                }
            } else {
                removePMDNature();
            }
        }

        return true;
    }

    /**
     * Store the rules selection in project property
     */
    private boolean storeRuleSelection() {
        log.debug("Storing the rule selection");
        boolean flNeedRebuild = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            RuleSet activeRuleSet = PMDPlugin.getDefault().getRuleSetForResourceFromProperties(resource, true);
            Set activeRules = null;
            activeRules = activeRuleSet.getRules();

            RuleSet selectedRuleSet = new RuleSet();
            TableItem[] rulesList = availableRulesTableViewer.getTable().getItems();
            for (int i = 0; i < rulesList.length; i++) {
                if (rulesList[i].getChecked()) {
                    Rule rule = (Rule) rulesList[i].getData();
                    selectedRuleSet.addRule(rule);
                    log.debug("Adding rule " + rule.getName() + " in the active ruleset");
                    if (!flNeedRebuild && (!activeRules.contains(rule))) {
                        flNeedRebuild = true;
                    }
                }
            }

            log.debug("Ask to store the rule selection for resource " + resource.getName());
            PMDPlugin.getDefault().storeRuleSetForResource(resource, selectedRuleSet);

            if (!flNeedRebuild) {
                Iterator i = activeRules.iterator();
                Set selectedRules = selectedRuleSet.getRules();
                while ((i.hasNext()) && (!flNeedRebuild)) {
                    Rule rule = (Rule) i.next();
                    if (!selectedRules.contains(rule)) {
                        flNeedRebuild = true;
                    }
                }
            }
        }

        return flNeedRebuild;
    }

    /**
     * Store the selected workingset
     *
     */
    private void storeSelectedWorkingSet() {
        boolean flStore = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            IProject project = resource.getProject();
            IWorkingSet configuredWorkingSet = PMDPlugin.getDefault().getProjectWorkingSet(project);

            if ((configuredWorkingSet == null) && (selectedWorkingSet != null)) {
                flStore = true;
            } else if ((configuredWorkingSet != null) && (selectedWorkingSet == null)) {
                flStore = true;
            } else if (
                (configuredWorkingSet != null)
                    && (selectedWorkingSet != null)
                    && (!configuredWorkingSet.getName().equals(selectedWorkingSet.getName()))) {
                flStore = true;
            }

            if (flStore) {
                PMDPlugin.getDefault().setProjectWorkingSet(project, selectedWorkingSet);
            }
        }
    }

    /**
     * Store the store_ruleset_project property
     *
     */
    private boolean storeRuleSetStoredInProject() {
        boolean needRebuild = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            IProject project = resource.getProject();
            boolean configuredProperty = PMDPlugin.getDefault().isRuleSetStoredInProject(project);

            if (configuredProperty != this.ruleSetStoredInProjectButton.getSelection()) {
                PMDPlugin.getDefault().setRuleSetStoredInProject(
                    project,
                    new Boolean(this.ruleSetStoredInProjectButton.getSelection()));
                needRebuild = true;
            }

            if (this.ruleSetStoredInProjectButton.getSelection()) {
                checkProjectRuleSetFile(project);
                try {
                    project.setSessionProperty(PMDPlugin.SESSION_PROPERTY_RULESET_MODIFICATION_STAMP, null);
                } catch (CoreException e) {
                    log.error("Core exception when nullify the ruleset timestamp");
                    log.debug("", e);
                }
            }
        }

        return needRebuild;
    }

    /**
     * Test if PMD is enable for this project
     */
    private boolean isEnabled() {
        boolean fEnabled = false;
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                fEnabled = project.hasNature(PMDNature.PMD_NATURE);
            }
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }

        return fEnabled;
    }

    /**
     * Test if the configured ruleset should be store in a project file
     * @return
     */
    private boolean isRuleSetStoredInProject() {
        boolean ruleSetStoredInProject = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            ruleSetStoredInProject = PMDPlugin.getDefault().isRuleSetStoredInProject(resource.getProject());
        }

        return ruleSetStoredInProject;
    }

    /**
     * Add the PMD Nature to the project
     * @return false if nature cannot be added
     */
    private boolean addPMDNature() {
        boolean natureAdded = false;
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                if (!project.hasNature(PMDNature.PMD_NATURE)) {
                    log.info("Adding PMD nature to the project " + project.getName());
                    ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
                    AddNatureTask task = new AddNatureTask(project);
                    progressDialog.run(true, true, task);
                    natureAdded = task.natureAdded;
                    log.debug("Nature added = " + natureAdded);
                }
            }
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Adding PMD nature interrupted", e);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }

        return natureAdded;
    }

    /**
     * Perform a full rebuild of the project
     *
     */
    private void rebuildProject() {
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                log.info("Adding PMD nature to the project " + project.getName());
                boolean rebuild =
                    MessageDialog.openQuestion(
                        getShell(),
                        getMessage(PMDConstants.MSGKEY_QUESTION_TITLE),
                        getMessage(PMDConstants.MSGKEY_QUESTION_REBUILD_PROJECT));
                if (rebuild) {
                    log.info("Full rebuild of the project " + project.getName());
                    ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
                    progressDialog.run(true, true, new RebuildTask(project));
                }
            }
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Rebuilding project interrupted", e);
        }
    }

    /**
     * Remove a PMD Nature from the project
     */
    private void removePMDNature() {
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                log.info("Removing PMD nature from the project " + project.getName());
                ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
                progressDialog.run(true, true, new RemoveNatureTask(project));
            }
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Removing PMD nature interrupted", e);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().showError("Error removing PMD nature", e);
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
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
     */
    public boolean performCancel() {
        log.info("Properties editing canceled");
        return super.performCancel();
    }

    /**
     * Help user to select a working set for PMD
     *
     */
    protected void selectWorkingSet() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkingSetManager workingSetManager = workbench.getWorkingSetManager();
        IWorkingSetSelectionDialog selectionDialog = workingSetManager.createWorkingSetSelectionDialog(getShell(), false);
        if (selectedWorkingSet != null) {
            selectionDialog.setSelection(new IWorkingSet[] { selectedWorkingSet });
        }

        if (selectionDialog.open() == Window.OK) {
            if (selectionDialog.getSelection().length != 0) {
                setSelectedWorkingSet(selectionDialog.getSelection()[0]);
                log.info("Working set " + getSelectedWorkingSet().getName() + " selected");
            } else {
                setSelectedWorkingSet(null);
                log.info("Deselect working set");
            }
        }
    }

    /**
     * Help user to deselect a working set for PMD
     *
     */
    protected void deselectWorkingSet() {
        setSelectedWorkingSet(null);
        log.info("Deselect working set");
        deselectWorkingSetButton.setEnabled(false);
    }

    /**
     * @return
     */
    public IWorkingSet getSelectedWorkingSet() {
        return selectedWorkingSet;
    }

    /**
     * @param string
     */
    public void setSelectedWorkingSet(IWorkingSet workingSet) {
        selectedWorkingSet = workingSet;
        selectedWorkingSetLabel.setText(
            workingSet == null
                ? getMessage(PMDConstants.MSGKEY_PROPERTY_LABEL_NO_WORKINGSET)
                : (getMessage(PMDConstants.MSGKEY_PROPERTY_LABEL_SELECTED_WORKINGSET) + selectedWorkingSet.getName()));
        deselectWorkingSetButton.setEnabled(workingSet != null);
    }

    /**
     * Check if the project has a ruleset file. If not propose to create a default one
     * @param project
     */
    private void checkProjectRuleSetFile(IProject project) {
        IFile ruleSetFile = project.getFile(".ruleset");
        if (!ruleSetFile.exists()) {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            if (MessageDialog
                .openQuestion(
                    shell,
                    getMessage(PMDConstants.MSGKEY_QUESTION_TITLE),
                    getMessage(PMDConstants.MSGKEY_QUESTION_CREATE_RULESET_FILE))) {
                storeRuleSelection();
                RuleSet ruleSet = PMDPlugin.getDefault().getRuleSetForResourceFromProperties(project, false);
                ruleSet.setName("project rulset");
                ruleSet.setDescription("Generated by PMD Plugin for Eclipse");
                try {
                    OutputStream out = new FileOutputStream(ruleSetFile.getLocation().toOSString());
                    RuleSetWriter writer = WriterAbstractFactory.getFactory().getRuleSetWriter();
                    writer.write(out, ruleSet);
                    out.flush();
                    out.close();
                    ruleSetFile.refreshLocal(IResource.DEPTH_INFINITE, null);
                } catch (IOException e) {
                    PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_EXPORTING_RULESET), e);
                } catch (PMDEclipseException e) {
                    PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_EXPORTING_RULESET), e);
                } catch (CoreException e) {
                    PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_EXPORTING_RULESET), e);
                }
            }
        }
    }

    /**
     * Private inner class to add nature to project
     */
    private class AddNatureTask implements IRunnableWithProgress {
        private IProject project;
        public boolean natureAdded = false;

        public AddNatureTask(IProject project) {
            this.project = project;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                IProjectDescription description = project.getDescription();
                String[] natureIds = description.getNatureIds();
                String[] newNatureIds = new String[natureIds.length + 1];
                System.arraycopy(natureIds, 0, newNatureIds, 0, natureIds.length);
                newNatureIds[natureIds.length] = PMDNature.PMD_NATURE;
                description.setNatureIds(newNatureIds);
                this.project.setDescription(description, monitor);
                this.natureAdded = true;
            } catch (CoreException e) {
                PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
            }
        }
    };

    /**
     * Inner class to rebuild the project
     */
    private class RebuildTask implements IRunnableWithProgress {
        private IProject project;

        public RebuildTask(IProject project) {
            this.project = project;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
            } catch (CoreException e) {
                PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
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
                PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
            }
        }
    };

}
