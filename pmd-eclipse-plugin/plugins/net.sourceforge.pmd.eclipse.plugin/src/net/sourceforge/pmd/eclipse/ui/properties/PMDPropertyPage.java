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
package net.sourceforge.pmd.eclipse.ui.properties;

import java.util.Collection;
import java.util.Comparator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.PMDPreferencePage;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleLabelProvider;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleSetContentProvider;
import net.sourceforge.pmd.eclipse.ui.preferences.RuleTableViewerSorter;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Property page to enable or disable PMD on a project
 * 
 * @author Philippe Herlin
 *
 */
public class PMDPropertyPage extends PropertyPage {
    private static final Logger log = Logger.getLogger(PMDPropertyPage.class);
    private PMDPropertyPageController controller;
    private PMDPropertyPageBean model;
    private Button enablePMDButton;
    protected TableViewer availableRulesTableViewer;
    private IWorkingSet selectedWorkingSet;
    private Label selectedWorkingSetLabel;
    private Button deselectWorkingSetButton;
    private Button includeDerivedFilesButton;
    protected Button ruleSetStoredInProjectButton;
    protected Text ruleSetFileText;
    protected Button ruleSetBrowseButton;

    private final RuleTableViewerSorter availableRuleTableViewerSorter = new RuleTableViewerSorter(RuleTableViewerSorter.RULE_DEFAULT_COMPARATOR);

    /**
     * @see PropertyPage#createContents(Composite)
     */
    protected Control createContents(final Composite parent) {
        log.info("PMD properties editing requested");
        this.controller = new PMDPropertyPageController(this.getShell());
        final IProject project = (IProject) this.getElement().getAdapter(IProject.class);
        this.controller.setProject(project);
        this.model = controller.getPropertyPageBean();

        Composite composite = null;
        noDefaultAndApplyButton();

        if ((project.isAccessible()) && (this.model != null)) {
            composite = new Composite(parent, SWT.NONE);

            final GridLayout layout = new GridLayout();
            composite.setLayout(layout);

            this.enablePMDButton = buildEnablePMDButton(composite);

            Label separator = new Label(composite, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
            GridData data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            separator.setLayoutData(data);
            
            this.includeDerivedFilesButton = buildIncludeDerivedFilesButton(composite);

            separator = new Label(composite, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
            data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            separator.setLayoutData(data);

            this.selectedWorkingSetLabel = buildSelectedWorkingSetLabel(composite);
            data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            selectedWorkingSetLabel.setLayoutData(data);

            final Composite workingSetPanel = new Composite(composite, SWT.NONE);
            RowLayout rowLayout = new RowLayout();
            rowLayout.type = SWT.HORIZONTAL;
            rowLayout.justify = true;
            rowLayout.pack = false;
            rowLayout.wrap = false;
            workingSetPanel.setLayout(rowLayout);
            buildSelectWorkingSetButton(workingSetPanel);
            this.deselectWorkingSetButton = buildDeselectWorkingSetButton(workingSetPanel);

            separator = new Label(composite, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
            data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            separator.setLayoutData(data);

            buildLabel(composite, StringKeys.MSGKEY_PROPERTY_LABEL_SELECT_RULE);
            final Table availableRulesTable = buildAvailableRulesTableViewer(composite);
            data = new GridData();
            data.grabExcessHorizontalSpace = true;
            data.grabExcessVerticalSpace = true;
            data.horizontalAlignment = GridData.FILL;
            data.verticalAlignment = GridData.FILL;
            data.heightHint = 50;
            availableRulesTable.setLayoutData(data);

            final Composite ruleSetPanel = new Composite(composite, SWT.NONE);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            ruleSetPanel.setLayout(gridLayout);
            data = new GridData();
            data.grabExcessHorizontalSpace = true;
            data.horizontalAlignment = GridData.FILL;
            ruleSetPanel.setLayoutData(data);
            
            this.ruleSetStoredInProjectButton = buildStoreRuleSetInProjectButton(ruleSetPanel);
            this.ruleSetFileText = buildRuleSetFileText(ruleSetPanel);
            this.ruleSetBrowseButton = buildRuleSetBrowseButton(ruleSetPanel);

            data = new GridData(SWT.FILL, SWT.NONE, true, false);
            ruleSetFileText.setLayoutData(data);
            
            refreshRuleSetInProject();

        } else {
            setValid(false);
        }
        
        log.debug("Property page created");
        return composite;
    }

    /**
     * Create the enable PMD checkbox
     * @param parent the parent composite
     */
    private Button buildEnablePMDButton(final Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PROPERTY_BUTTON_ENABLE));
        button.setSelection(model.isPmdEnabled());

        return button;
    }

    /**
     * Create the include derived files checkbox
     * @param parent the parent composite
     */
    private Button buildIncludeDerivedFilesButton(final Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PROPERTY_BUTTON_INCLUDE_DERIVED_FILES));
        button.setSelection(model.isIncludeDerivedFiles());

        return button;
    }

    /**
     * Create the checkbox for storing configuration in a project file
     * @param parent the parent composite
     */
    private Button buildStoreRuleSetInProjectButton(final Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PROPERTY_BUTTON_STORE_RULESET_PROJECT));
        button.setSelection(model.isRuleSetStoredInProject());

        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	refreshRuleSetInProject();
            }
        });       

        return button;
    }

    /**
     * Create the the rule set file name text.
     * @param parent the parent composite
     */
    private Text buildRuleSetFileText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        String ruleSetFile = model.getRuleSetFile();
        if (ruleSetFile != null) {
        	text.setText(ruleSetFile);
        }
        return text;
    }

    /**
     * Create the button for browsing for a ruleset file.
     * @param parent the parent composite
     */
    private Button buildRuleSetBrowseButton(final Composite parent) {
        final Button button = new Button(parent, SWT.PUSH);
        button.setText(getMessage(StringKeys.MSGKEY_PROPERTY_BUTTON_RULESET_BROWSE));

        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	// TODO EMF's ResourceDialog would be better.
            	FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
            	String path = fileDialog.open();
				if (path != null) {
					ruleSetFileText.setText(path);
				}
            }
        });

        return button;
    }

    /**
     * Build a label
     */
    private Label buildLabel(final Composite parent, final String msgKey) {
        final Label label = new Label(parent, SWT.NONE);
        label.setText(msgKey == null ? "" : this.getMessage(msgKey));
        return label;
    }

    /**
     * Build a label
     */
    private Label buildSelectedWorkingSetLabel(final Composite parent) {
        this.selectedWorkingSet = model.getProjectWorkingSet();

        final Label label = new Label(parent, SWT.NONE);
        label.setText(
            this.selectedWorkingSet == null
                ? getMessage(StringKeys.MSGKEY_PROPERTY_LABEL_NO_WORKINGSET)
                : (getMessage(StringKeys.MSGKEY_PROPERTY_LABEL_SELECTED_WORKINGSET) + selectedWorkingSet.getName()));

        return label;
    }

    /**
     * Build rule table viewer
     */
    private Table buildAvailableRulesTableViewer(final Composite parent) {
        final int tableStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK;
        availableRulesTableViewer = new TableViewer(parent, tableStyle);

        final Table ruleTable = availableRulesTableViewer.getTable();

        addColumnTo(ruleTable, SWT.LEFT, true, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_RULESET_NAME), 110, RuleTableViewerSorter.RULE_RULESET_NAME_COMPARATOR);
        addColumnTo(ruleTable, SWT.LEFT, true, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_RULE_NAME), 170, RuleTableViewerSorter.RULE_NAME_COMPARATOR);
		addColumnTo(ruleTable, SWT.LEFT, false, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_SINCE), 40, RuleTableViewerSorter.RULE_SINCE_COMPARATOR);
        addColumnTo(ruleTable, SWT.LEFT, false, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_PRIORITY), 80, RuleTableViewerSorter.RULE_PRIORITY_COMPARATOR);
        addColumnTo(ruleTable, SWT.LEFT, true, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_DESCRIPTION), 300, RuleTableViewerSorter.RULE_DESCRIPTION_COMPARATOR);

        ruleTable.setLinesVisible(true);
        ruleTable.setHeaderVisible(true);

        this.availableRulesTableViewer.setContentProvider(new RuleSetContentProvider());
        this.availableRulesTableViewer.setLabelProvider(new RuleLabelProvider());
        this.availableRulesTableViewer.setSorter(this.availableRuleTableViewerSorter);
        this.availableRulesTableViewer.setColumnProperties(
            new String[] {
                PMDPreferencePage.PROPERTY_RULESET_NAME,
                PMDPreferencePage.PROPERTY_RULE_NAME,
                PMDPreferencePage.PROPERTY_SINCE,
                PMDPreferencePage.PROPERTY_PRIORITY,
                PMDPreferencePage.PROPERTY_DESCRIPTION });

        populateAvailableRulesTable();

        return ruleTable;
    }
    
    /**
     * Helper method to add new table columns
     */
    private void addColumnTo(Table table, int alignment, boolean resizable, String text, int width, final Comparator comparator) {
        
    	TableColumn newColumn = new TableColumn(table, alignment);
    	newColumn.setResizable(resizable);
    	newColumn.setText(text);
    	newColumn.setWidth(width);
    	if (comparator != null) {
	    	newColumn.addSelectionListener(new SelectionAdapter() {
	            public void widgetSelected(SelectionEvent e) {
                    availableRuleTableViewerSorter.setComparator(comparator);
                    refresh();
	            }
	        });
    	}
    }

    /**
     * Build the working set selection button
     * @param parent
     */
    private void buildSelectWorkingSetButton(final Composite parent) {
        final Button workingSetButton = new Button(parent, SWT.PUSH);
        workingSetButton.setText(getMessage(StringKeys.MSGKEY_PROPERTY_BUTTON_SELECT_WORKINGSET));
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
    private Button buildDeselectWorkingSetButton(final Composite parent) {
        final Button button = new Button(parent, SWT.PUSH);
        button.setText(getMessage(StringKeys.MSGKEY_PROPERTY_BUTTON_DESELECT_WORKINGSET));
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
        final RuleSet activeRuleSet = model.getProjectRuleSet();
        if (activeRuleSet != null) {
            final Collection activeRules = activeRuleSet.getRules();

            final TableItem[] itemList = availableRulesTableViewer.getTable().getItems();
            for (int i = 0; i < itemList.length; i++) {
                final Object rule = itemList[i].getData();
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
        this.model.setPmdEnabled(this.enablePMDButton.getSelection());
        this.model.setProjectWorkingSet(this.selectedWorkingSet);
        this.model.setProjectRuleSet(this.getProjectRuleSet());
        this.model.setRuleSetStoredInProject(this.ruleSetStoredInProjectButton.getSelection());
        this.model.setRuleSetFile(this.ruleSetFileText.getText());
        this.model.setIncludeDerivedFiles(this.includeDerivedFilesButton.getSelection());
        
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
     * @return a RuleSet object from the selected table item
     */
    private RuleSet getProjectRuleSet() {
        final RuleSet ruleSet = new RuleSet();
        final TableItem[] rulesList = this.availableRulesTableViewer.getTable().getItems();

        for (int i = 0; i < rulesList.length; i++) {
            if (rulesList[i].getChecked()) {
                final Rule rule = (Rule) rulesList[i].getData();
                ruleSet.addRule(rule);
//                log.debug("Adding rule " + rule.getName() + " in the project ruleset");
            }
        }

        final RuleSet activeRuleSet = model.getProjectRuleSet();
        ruleSet.addExcludePatterns(activeRuleSet.getExcludePatterns());
        ruleSet.addIncludePatterns(activeRuleSet.getIncludePatterns());

        return ruleSet;
    }
    
    /**
     * Help user to select a working set for PMD
     *
     */
    protected void selectWorkingSet() {
        log.info("Select working set");
        this.setSelectedWorkingSet(this.controller.selectWorkingSet(this.selectedWorkingSet));
    }

    /**
     * Help user to deselect a working set for PMD
     *
     */
    protected void deselectWorkingSet() {
        log.info("Deselect working set");
        setSelectedWorkingSet(null);
    }

    /**
     * Process the change of the selected working set
     * @param a newly selected working set
     */
    private void setSelectedWorkingSet(final IWorkingSet workingSet) {
        this.selectedWorkingSet = workingSet;
        this.selectedWorkingSetLabel.setText(
            this.selectedWorkingSet == null
                ? getMessage(StringKeys.MSGKEY_PROPERTY_LABEL_NO_WORKINGSET)
                : (getMessage(StringKeys.MSGKEY_PROPERTY_LABEL_SELECTED_WORKINGSET) + this.selectedWorkingSet.getName()));
        deselectWorkingSetButton.setEnabled(this.selectedWorkingSet != null);
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(final String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

    /**
     * Refresh the list
     */
    protected void refresh() {
        try {
            availableRulesTableViewer.getControl().setRedraw(false);
        	// Preserve the checked rules across a refresh.  Checked rules seem to be cleared when table is sorted.
            Collection rules = getProjectRuleSet().getRules();
            availableRulesTableViewer.refresh();
            TableItem[] items = availableRulesTableViewer.getTable().getItems();
            for (int i = 0; i < items.length; i++) {
            	if (rules.contains(items[i].getData())) {
            		items[i].setChecked(true);
            	}
            }
        } catch (ClassCastException e) {
            PMDPlugin.getDefault().logError("Ignoring exception while refreshing table", e);
        } finally {
        	availableRulesTableViewer.getControl().setRedraw(true);
        }
    }

    /**
     * Refresh based up whether using rule set in project or not
     */
    protected void refreshRuleSetInProject() {
        final Table ruleTable = availableRulesTableViewer.getTable();
        if (ruleSetStoredInProjectButton.getSelection()) {
            ruleTable.setEnabled(false);
            ruleSetBrowseButton.setEnabled(true);
            ruleSetFileText.setEnabled(true);
        } else {
            ruleTable.setEnabled(true);
            ruleSetBrowseButton.setEnabled(false);
            ruleSetFileText.setEnabled(false);
        }
    }
}
