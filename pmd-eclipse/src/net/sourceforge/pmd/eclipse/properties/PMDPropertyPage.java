package net.sourceforge.pmd.eclipse.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.builder.PMDNature;
import net.sourceforge.pmd.eclipse.preferences.PMDPreferencePage;
import net.sourceforge.pmd.eclipse.preferences.RuleLabelProvider;
import net.sourceforge.pmd.eclipse.preferences.RuleSetContentProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Property page to enable or disable PMD on a project
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
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
    private TableViewer availableRulesTableViewer;

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

            buildLabel(composite, PMDConstants.MSGKEY_PROPERTY_LABEL_SELECT_RULE);
            Table availableRulesTable = buildAvailableRulesTableViewer(composite);
            GridData data = new GridData();
            data.grabExcessHorizontalSpace = true;
            data.grabExcessVerticalSpace = true;
            data.horizontalAlignment = GridData.FILL;
            data.verticalAlignment = GridData.FILL;
            data.heightHint = 50;
            availableRulesTable.setLayoutData(data);
        } else {
            setValid(false);
        }

        return composite;
    }

    /**
     * Create the checkbox button
     * @param parent the parent composite
     */
    private Button buildEnablePMDButton(Composite parent) {
        Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(PMDConstants.MSGKEY_PROPERTY_BUTTON_ENABLE));
        button.setSelection(isEnabled());

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

        return ruleTable;
    }

    /**
     * Populate the rule table
     */
    private void populateAvailableRulesTable() {
        RuleSet configuredRuleSet = PMDPlugin.getDefault().getRuleSet();
        availableRulesTableViewer.setInput(configuredRuleSet);

        RuleSet activeRuleSet = PMDPlugin.getDefault().getRuleSetForResource((IResource) getElement(), true);
        Set activeRules = activeRuleSet.getRules();

        TableItem[] itemList = availableRulesTableViewer.getTable().getItems();
        for (int i = 0; i < itemList.length; i++) {
            Object rule = itemList[i].getData();
            if (activeRules.contains(rule)) {
                itemList[i].setChecked(true);
            }
        }
    }

    /**
     * User press OK Button
     */
    public boolean performOk() {
        log.info("Properties editing accepted");
        if (((IProject) getElement()).isAccessible()) {

            boolean flForceRebuild = storeRuleSelection();
            boolean fEnabled = enablePMDButton.getSelection();
            if (fEnabled) {
                addPMDNature(flForceRebuild);
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
        boolean flNeedRebuild = false;
        RuleSet activeRuleSet = PMDPlugin.getDefault().getRuleSetForResource((IResource) getElement(), true);
        Set activeRules = null;
        activeRules = activeRuleSet.getRules();

        RuleSet selectedRuleSet = new RuleSet();
        TableItem[] rulesList = availableRulesTableViewer.getTable().getItems();
        for (int i = 0; i < rulesList.length; i++) {
            if (rulesList[i].getChecked()) {
                Rule rule = (Rule) rulesList[i].getData();
                selectedRuleSet.addRule(rule);
                if (!flNeedRebuild && (!activeRules.contains(rule))) {
                    flNeedRebuild = true;
                }
            }
        }

        PMDPlugin.getDefault().storeRuleSetForResource((IResource) getElement(), selectedRuleSet);

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

        return flNeedRebuild;
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
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }

        return fEnabled;
    }

    /**
     * Add the PMD Nature to the project
     * @return false if nature cannot be added
     */
    private boolean addPMDNature(boolean flForceRebuild) {
        boolean fNatureAdded = false;
        try {
            if (getElement() instanceof IProject) {
                IProject project = (IProject) getElement();
                log.info("Adding PMD nature to the project " + project.getName());
                ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(getShell());
                progressDialog.run(true, true, new AddNatureTask(project, flForceRebuild));
                fNatureAdded = true;
            }
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Adding PMD nature interrupted", e);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().showError("Error adding PMD nature", e);
        }

        return fNatureAdded;
    }

    /**
     * Remove a PMD Nature from the project
     */
    private void removePMDNature() {
        try {
            if (getElement() instanceof IProject) {
                IProject project = (IProject) getElement();
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
     * Private inner class to add nature to project
     */
    private class AddNatureTask implements IRunnableWithProgress {
        private IProject project;
        private boolean flForceRebuild;

        public AddNatureTask(IProject project, boolean flForceRebuild) {
            this.project = project;
            this.flForceRebuild = flForceRebuild;
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
                    flForceRebuild = true;
                }

                if (flForceRebuild) {
                    project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
                }

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
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
     */
    public boolean performCancel() {
        log.info("Properties editing canceled");
        return super.performCancel();
    }

}
