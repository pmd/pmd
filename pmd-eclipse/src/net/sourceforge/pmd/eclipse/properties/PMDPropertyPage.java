/*
 * Created on 18 nov. 2004
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.pmd.eclipse.properties;

import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.preferences.PMDPreferencePage;
import net.sourceforge.pmd.eclipse.preferences.RuleLabelProvider;
import net.sourceforge.pmd.eclipse.preferences.RuleSetContentProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.PreferencePage;
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
 * Revision 1.13  2004/11/18 23:54:27  phherlin
 * Refactoring to apply MVC.
 * The goal is to test the refactoring before a complete refactoring for all GUI
 *
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
    private PMDPropertyPageController controller;
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
        controller = new PMDPropertyPageController(this);
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        log.info("PMD properties editing requested");
        controller.setElement(this.getElement());

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
        button.setSelection(controller.isPMDEnabled());

        return button;
    }

    /**
     * Create the checkbox for storing configuration in a project file
     * @param parent the parent composite
     */
    private Button buildStoreRuleSetInProjectButton(Composite parent) {
        Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(PMDConstants.MSGKEY_PROPERTY_BUTTON_STORE_RULESET_PROJECT));
        button.setSelection(controller.isRuleSetStoredInProject());

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
        this.selectedWorkingSet = controller.getProjectWorkingSet();

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
        ruleTable.setEnabled(!controller.isRuleSetStoredInProject());

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
        availableRulesTableViewer.setInput(controller.getAvailableRules());
        RuleSet activeRuleSet = controller.getProjectRules();
        if (activeRuleSet != null) {
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
        return controller.performOk();
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
     */
    public boolean performCancel() {
        log.info("Properties editing canceled");
        return super.performCancel();
    }
    
    /**
     * Return the store rule set in project check box state
     * @return whether the user has checked the store rule set in project
     *         check box 
     */
    public boolean isRuleSetStoredInProject() {
        return this.ruleSetStoredInProjectButton.getSelection();
    }

    /**
     * Check if PMD is enabled
     * @return the state of the Enable PMD check box
     */
    public boolean isPMDEnabled() {
        return this.enablePMDButton.getSelection();
    }
    
    /**
     * @return the rules table content
     */
    public TableItem[] getRulesList() {
        return this.availableRulesTableViewer.getTable().getItems();
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
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
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

}
