package net.sourceforge.pmd.eclipse.preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.RuleSetWriter;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.7  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */

public class PMDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_PRIORITY = "priority";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_PROPERTY = "property";
    public static final String PROPERTY_VALUE = "value";

    public static PMDPreferencePage activeInstance = null;

    private TableViewer ruleTableViewer;
    private TableViewer rulePropertiesTableViewer;
    private Button removeRuleButton;
    private Button editRuleButton;
    private Button addPropertyButton;
    private RuleSet ruleSet;
    private boolean modified = false;

    /**
     * 
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
        setDescription(getMessage(PMDConstants.MSGKEY_PREF_RULESET_TITLE));
        activeInstance = this;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        populateRuleTable();
        super.performDefaults();
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        if (modified) {
            PMDPlugin.getDefault().setRuleSet(ruleSet);
            if (MessageDialog
                .openQuestion(
                    getShell(),
                    getMessage(PMDConstants.MSGKEY_QUESTION_TITLE),
                    getMessage(PMDConstants.MSGKEY_QUESTION_RULES_CHANGED))) {
                try {
                    ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
                    monitorDialog.run(true, true, new IRunnableWithProgress() {
                        /**
                         * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
                         */
                        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                            try {
                                ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
                            } catch (CoreException e) {
                            }
                        }
                    });
                } catch (InterruptedException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }

        return super.performOk();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        layoutControls(composite);
        return composite;
    }

    /**
     * Main layout
     */
    private void layoutControls(Composite parent) {

        // Create the controls (order is important !)
        Label ruleTableLabel = buildLabel(parent, PMDConstants.MSGKEY_PREF_RULESET_LABEL_RULETABLE);
        Table ruleTable = buildRuleTableViewer(parent);
        Composite ruleTableButtons = buildRuleTableButtons(parent);
        Label dummyLabel = buildLabel(parent, null);
        Label rulePropertiesTableLabel = buildLabel(parent, PMDConstants.MSGKEY_PREF_RULESET_LABEL_RULEPROPSTABLE);
        Table rulePropertiesTable = buildRulePropertiesTableViewer(parent);
        Composite rulePropertiesTableButton = buildRulePropertiesTableButtons(parent);

        // Place controls on the layout
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        parent.setLayout(gridLayout);

        GridData data = new GridData();
        data.horizontalSpan = 2;
        ruleTableLabel.setLayoutData(data);

        data = new GridData();
        data.heightHint = 200;
        data.widthHint = 350;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        ruleTable.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        ruleTableButtons.setLayoutData(data);

        data = new GridData();
        data.horizontalSpan = 2;
        dummyLabel.setLayoutData(data);

        data = new GridData();
        data.horizontalSpan = 2;
        rulePropertiesTableLabel.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.heightHint = 100;
        data.widthHint = 300;
        rulePropertiesTable.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.verticalAlignment = GridData.FILL;
        rulePropertiesTableButton.setLayoutData(data);
    }

    /**
     * Create buttons for rule table management
     */
    private Composite buildRuleTableButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        RowLayout rowLayout = new RowLayout();
        rowLayout.type = SWT.VERTICAL;
        rowLayout.wrap = false;
        rowLayout.pack = false;
        composite.setLayout(rowLayout);

        buildAddRuleButton(composite);
        removeRuleButton = buildRemoveRuleButton(composite);
        editRuleButton = buildEditRuleButton(composite);
        buildImportRuleSetButton(composite);
        buildExportRuleSetButton(composite);
        buildClearAllButton(composite);

        return composite;
    }

    /**
     * Create buttons for rule properties table management
     */
    private Composite buildRulePropertiesTableButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);

        addPropertyButton = buildAddPropertyButton(composite);
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        addPropertyButton.setLayoutData(data);

        return composite;
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
    private Table buildRuleTableViewer(Composite parent) {
        int tableStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION;
        ruleTableViewer = new TableViewer(parent, tableStyle);

        Table ruleTable = ruleTableViewer.getTable();
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

        ruleTableViewer.setContentProvider(new RuleSetContentProvider());
        ruleTableViewer.setLabelProvider(new RuleLabelProvider());

        ruleTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
             */
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Rule selectedRule = (Rule) selection.getFirstElement();
                rulePropertiesTableViewer.setInput(selectedRule);
                removeRuleButton.setEnabled(selectedRule != null);
                editRuleButton.setEnabled(selectedRule != null);
                addPropertyButton.setEnabled(selectedRule != null);
            }
        });

        ruleTableViewer.setColumnProperties(new String[] { PROPERTY_NAME, PROPERTY_PRIORITY, PROPERTY_DESCRIPTION });
        ruleTableViewer.setCellModifier(new RuleCellModifier(ruleTableViewer));
        ruleTableViewer.setCellEditors(
            new CellEditor[] {
                null,
                new ComboBoxCellEditor(ruleTable, PMDPlugin.getDefault().getPriorityLabels()),
                new TextCellEditor(ruleTable)});

        ruleTableViewer.setSorter(new ViewerSorter() {
            public int compare(Viewer viewer, Object e1, Object e2) {
                int result = 0;                
                if ((e1 instanceof Rule) && (e2 instanceof Rule)) {
                    result = ((Rule) e1).getName().compareTo(((Rule) e2).getName());
                }
                return result;
            }
            
            public boolean isSorterProperty(Object element, String property) {
                return property.equals(PROPERTY_NAME);
            }
        });

        populateRuleTable();

        return ruleTable;
    }

    /**
     * Build the rule properties table viewer
     */
    private Table buildRulePropertiesTableViewer(Composite parent) {
        int tableStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION;
        rulePropertiesTableViewer = new TableViewer(parent, tableStyle);

        Table rulePropertiesTable = rulePropertiesTableViewer.getTable();
        TableColumn propertyColumn = new TableColumn(rulePropertiesTable, SWT.LEFT);
        propertyColumn.setResizable(true);
        propertyColumn.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_COLUMN_PROPERTY));
        propertyColumn.setWidth(100);

        TableColumn valueColumn = new TableColumn(rulePropertiesTable, SWT.LEFT);
        valueColumn.setResizable(true);
        valueColumn.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_COLUMN_VALUE));
        valueColumn.setWidth(350);

        rulePropertiesTable.setLinesVisible(true);
        rulePropertiesTable.setHeaderVisible(true);

        rulePropertiesTableViewer.setContentProvider(new RulePropertiesContentProvider());
        rulePropertiesTableViewer.setLabelProvider(new RulePropertyLabelProvider());
        rulePropertiesTableViewer.setColumnProperties(new String[] { PROPERTY_PROPERTY, PROPERTY_VALUE });
        rulePropertiesTableViewer.setCellModifier(new RulePropertyCellModifier(rulePropertiesTableViewer));
        rulePropertiesTableViewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(rulePropertiesTable)});

        return rulePropertiesTable;
    }

    /**
     * Build the add rule button
     */
    private Button buildAddRuleButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_BUTTON_ADDRULE));
        button.setEnabled(true);

        button.addSelectionListener(new SelectionListener() {
            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
             */
            public void widgetSelected(SelectionEvent event) {
                RuleDialog dialog = new RuleDialog(getShell());
                int result = dialog.open();
                if (result == RuleDialog.OK) {
                    ruleSet.addRule(dialog.getRule());
                    setModified(true);
                    try {
                        ruleTableViewer.refresh();
                    } catch (Throwable t) {
                    }
                }
            }

            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        return button;
    }

    /**
     * Build the remove rule button
     */
    private Button buildRemoveRuleButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_BUTTON_REMOVERULE));
        button.setEnabled(false);
        button.addSelectionListener(new SelectionListener() {
            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
             */
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection selection = (IStructuredSelection) ruleTableViewer.getSelection();
                Rule selectedRule = (Rule) selection.getFirstElement();
                ruleSet.getRules().remove(selectedRule);
                setModified(true);
                try {
                    ruleTableViewer.refresh();
                } catch (Throwable t) {
                    ruleTableViewer.setSelection(null);
                }
            }

            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        return button;
    }

    /**
     * Build the edit rule button
     */
    private Button buildEditRuleButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_BUTTON_EDITRULE));
        button.setEnabled(false);

        button.addSelectionListener(new SelectionListener() {
            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
             */
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection selection = (IStructuredSelection) ruleTableViewer.getSelection();
                Rule rule = (Rule) selection.getFirstElement();

                RuleDialog dialog = new RuleDialog(getShell(), rule);
                int result = dialog.open();
                if (result == RuleDialog.OK) {
                    setModified(true);
                    try {
                        ruleTableViewer.refresh();
                    } catch (Throwable t) {
                    }
                }
            }

            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        return button;
    }

    /**
     * Build the import ruleset button
     */
    private Button buildImportRuleSetButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_BUTTON_IMPORTRULESET));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                RuleSetSelectionDialog dialog = new RuleSetSelectionDialog(getShell());
                dialog.open();
                if (dialog.getReturnCode() == RuleSetSelectionDialog.OK) {
                    try {
                        RuleSetFactory factory = new RuleSetFactory();
                        RuleSet importedRuleSet = factory.createRuleSet(dialog.getImportedRuleSetName());
                        ruleSet.addRuleSet(importedRuleSet);
                        setModified(true);
                        try {
                            ruleTableViewer.refresh();
                        } catch (Throwable t) {
                        }
                    } catch (RuleSetNotFoundException e) {
                        PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_RULESET_NOT_FOUND), e);
                    } catch (RuntimeException e) {
                        PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_IMPORTING_RULESET), e);
                    }
                }
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });

        return button;
    }

    /**
     * Build the export rule set button
     */
    private Button buildExportRuleSetButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_BUTTON_EXPORTRULESET));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
                String fileName = dialog.open();
                if (fileName != null) {
                    try {
                        File file = new File(fileName);
                        boolean flContinue = true;
                        if (file.exists()) {
                            flContinue =
                                MessageDialog.openConfirm(
                                    getShell(),
                                    getMessage(PMDConstants.MSGKEY_CONFIRM_TITLE),
                                    getMessage(PMDConstants.MSGKEY_CONFIRM_RULESET_EXISTS));
                        }

                        InputDialog input = null;
                        if (flContinue) {
                            input =
                                new InputDialog(
                                    getShell(),
                                    getMessage(PMDConstants.MSGKEY_PREF_RULESET_DIALOG_TITLE),
                                    getMessage(PMDConstants.MSGKEY_PREF_RULESET_DIALOG_RULESET_DESCRIPTION),
                                    ruleSet.getDescription().trim(),
                                    null);
                            flContinue = input.open() == InputDialog.OK;
                        }

                        if (flContinue) {
                            ruleSet.setName(getFileNameWithoutExtension(file.getName()));
                            ruleSet.setDescription(input.getValue());
                            OutputStream out = new FileOutputStream(fileName);
                            RuleSetWriter writer = new RuleSetWriter(out);
                            writer.write(ruleSet);
                            out.close();
                            MessageDialog.openInformation(
                                getShell(),
                                getMessage(PMDConstants.MSGKEY_INFORMATION_TITLE),
                                getMessage(PMDConstants.MSGKEY_INFORMATION_RULESET_EXPORTED));
                        }
                    } catch (IOException e) {
                        PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_EXPORTING_RULESET), e);
                    }
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        return button;
    }

    /**
     * Build the clear all button
     */
    private Button buildClearAllButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_BUTTON_CLEARALL));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                if (MessageDialog
                    .openConfirm(
                        getShell(),
                        getMessage(PMDConstants.MSGKEY_CONFIRM_TITLE),
                        getMessage(PMDConstants.MSGKEY_CONFIRM_CLEAR_RULESET))) {
                    ruleSet.getRules().clear();
                    setModified(true);
                    try {
                        ruleTableViewer.refresh();
                    } catch (Throwable t) {
                    }
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        return button;
    }

    /**
     * Build the add property button
     */
    private Button buildAddPropertyButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_BUTTON_ADDPROPERTY));
        button.setEnabled(false);
        button.addSelectionListener(new SelectionListener() {
            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
             */
            public void widgetSelected(SelectionEvent event) {
                InputDialog input =
                    new InputDialog(
                        getShell(),
                        getMessage(PMDConstants.MSGKEY_PREF_RULESET_DIALOG_TITLE),
                        getMessage(PMDConstants.MSGKEY_PREF_RULESET_DIALOG_PROPERTY_NAME),
                        "",
                        null);
                int result = input.open();
                if (result == InputDialog.OK) {
                    IStructuredSelection selection = (IStructuredSelection) ruleTableViewer.getSelection();
                    Rule selectedRule = (Rule) selection.getFirstElement();
                    selectedRule.addProperty(input.getValue(), "");
                    setModified(true);
                    rulePropertiesTableViewer.refresh();
                }
            }

            /**
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        return button;
    }

    /**
     * Populate the rule table
     */
    private void populateRuleTable() {
        RuleSet defaultRuleSet = PMDPlugin.getDefault().getRuleSet();
        ruleSet = new RuleSet();
        ruleSet.addRuleSet(defaultRuleSet);
        ruleSet.setName(defaultRuleSet.getName());
        ruleSet.setDescription(defaultRuleSet.getDescription());
        ruleTableViewer.setInput(ruleSet);
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
     * Helper method to get a filename without its extension
     */
    private String getFileNameWithoutExtension(String fileName) {
        String name = fileName;

        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            name = fileName.substring(0, index);
        }

        return name;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
     */
    protected IPreferenceStore doGetPreferenceStore() {
        return PMDPlugin.getDefault().getPreferenceStore();
    }

    /**
     * Returns the activeInstance.
     * @return PMDPreferencePage
     */
    public static PMDPreferencePage getActiveInstance() {
        return activeInstance;
    }

    /**
     * Returns the isModified.
     * @return boolean
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Sets the isModified.
     * @param isModified The isModified to set
     */
    public void setModified(boolean isModified) {
        this.modified = isModified;
    }

}