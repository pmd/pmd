package net.sourceforge.pmd.ui.preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.runtime.writer.WriterException;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

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
 * Revision 1.3  2007/06/24 15:54:54  phherlin
 * Fix 1710977 Null Pointer Exception on click of Add Rule (remove button)
 *
 * Revision 1.2  2007/01/24 22:46:17  hooperbloob
 * Cleanup rule description formatting & sorting bug crasher
 *
 * Revision 1.1  2006/05/22 21:23:39  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.15  2005/07/12 16:38:25  phherlin
 * RFE#1231112-Make the rule table columns sortable (thanks to Brian R)
 * BUG#1231108-Fix the resizing issue
 *
 * Revision 1.14  2005/06/07 22:40:06  phherlin
 * Implementing extra ruleset declaration
 *
 * Revision 1.13  2003/12/18 23:58:37  phherlin
 * Fixing malformed UTF-8 characters in generated xml files
 *
 * Revision 1.12  2003/11/30 22:57:44  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.11.2.1  2003/11/07 14:30:08  phherlin
 * Fixing : ruleset description may be null
 *
 * Revision 1.11  2003/10/16 22:26:37  phherlin
 * Fix bug #810858.
 * Complete refactoring of rule set generation. Using a DOM tree and the Xerces 2 serializer.
 *
 * Revision 1.10  2003/08/13 20:09:06  phherlin
 * Refactoring private->protected to remove warning about non accessible member access in enclosing types
 *
 * Revision 1.9  2003/07/07 19:27:10  phherlin
 * Making rules selectable from projects
 * Various refactoring and cleaning
 *
 * Revision 1.8  2003/07/01 20:20:30  phherlin
 * Correcting some PMD violations ! (empty catch stmt)
 *
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

    protected TableViewer ruleTableViewer;
    protected TableViewer rulePropertiesTableViewer;
    protected Button removeRuleButton;
    protected Button editRuleButton;
    protected Button addPropertyButton;
    protected RuleSet ruleSet;
    private boolean modified = false;

    private final RuleTableViewerSorter ruleTableViewerSorter = new RuleTableViewerSorter(RULE_NAME_COMPARATOR);

    /*
     * Column comparators used for sorting rule table
     */
    
    private static final Comparator RULE_NAME_COMPARATOR = new Comparator() {
        public int compare(Object e1, Object e2) {
        	return compareStrings(((Rule)e1).getName(), ((Rule)e2).getName());
        }
    };
    
    private static final Comparator RULE_PRIORITY_COMPARATOR = new Comparator() {
        public int compare(Object e1, Object e2) {
            return ((Rule) e1).getPriority() - (((Rule) e2).getPriority());
        }
    };
   
    private static final Comparator RULE_DESCRIPTION_COMPARATOR = new Comparator() {
        public int compare(Object e1, Object e2) {
        	return compareStrings(((Rule)e1).getDescription(), ((Rule)e2).getDescription());
            }
    };
   
    /**
     * Compare string pairs while handling nulls and trimming whitespace.
     * 
     * @param s1
     * @param s2
     * @return int
     */
    private static int compareStrings(String s1, String s2) {
    	String str1 = s1 == null ? "" : s1.trim().toUpperCase();
    	String str2 = s2 == null ? "" : s2.trim().toUpperCase();
     	return str1.compareTo(str2);
    }
    
    /**
     * @see IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        setDescription(getMessage(StringKeys.MSGKEY_PREF_RULESET_TITLE));
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
            updateRuleSet();
            rebuildProjects();
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
        Label ruleTableLabel = buildLabel(parent, StringKeys.MSGKEY_PREF_RULESET_LABEL_RULETABLE);
        Table ruleTable = buildRuleTableViewer(parent);
        Composite ruleTableButtons = buildRuleTableButtons(parent);
        Label dummyLabel = buildLabel(parent, null);
        Label rulePropertiesTableLabel = buildLabel(parent, StringKeys.MSGKEY_PREF_RULESET_LABEL_RULEPROPSTABLE);
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

        // buildAddRuleButton(composite);
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
        RowLayout rowLayout = new RowLayout();
        rowLayout.type = SWT.VERTICAL;
        rowLayout.wrap = false;
        rowLayout.pack = false;
        composite.setLayout(rowLayout);

        addPropertyButton = buildAddPropertyButton(composite);

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
                    ruleTableViewerSorter.setComparator(comparator);
                    refresh();
	            }
	        });
    	}
    }

    /**
     * Build rule table viewer
     */
    private Table buildRuleTableViewer(Composite parent) {
        int tableStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION;
        ruleTableViewer = new TableViewer(parent, tableStyle);

        Table ruleTable = ruleTableViewer.getTable();
        addColumnTo(ruleTable, SWT.LEFT, false, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_NAME), 200, RULE_NAME_COMPARATOR);
        addColumnTo(ruleTable, SWT.LEFT, false, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_PRIORITY), 110, RULE_PRIORITY_COMPARATOR);
        addColumnTo(ruleTable, SWT.LEFT, true, getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_DESCRIPTION), 200, RULE_DESCRIPTION_COMPARATOR);
        
        ruleTable.setLinesVisible(true);
        ruleTable.setHeaderVisible(true);

        ruleTableViewer.setContentProvider(new RuleSetContentProvider());
        ruleTableViewer.setLabelProvider(new RuleLabelProvider());
        ruleTableViewer.setSorter(this.ruleTableViewerSorter);
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
                new ComboBoxCellEditor(ruleTable, PMDUiPlugin.getDefault().getPriorityLabels()),
                new TextCellEditor(ruleTable)});

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
        propertyColumn.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_PROPERTY));
        propertyColumn.setWidth(100);

        TableColumn valueColumn = new TableColumn(rulePropertiesTable, SWT.LEFT);
        valueColumn.setResizable(true);
        valueColumn.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_COLUMN_VALUE));
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
     * Build the remove rule button
     */
    private Button buildRemoveRuleButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_REMOVERULE));
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection selection = (IStructuredSelection) ruleTableViewer.getSelection();
                Rule selectedRule = (Rule) selection.getFirstElement();
                ruleSet.getRules().remove(selectedRule);
                setModified(true);
                try {
                    refresh();
                } catch (Throwable t) {
                    ruleTableViewer.setSelection(null);
                }
            }
        });
        return button;
    }

    /**
     * Build the edit rule button
     */
    private Button buildEditRuleButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_EDITRULE));
        button.setEnabled(false);

        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection selection = (IStructuredSelection) ruleTableViewer.getSelection();
                Rule rule = (Rule) selection.getFirstElement();

                RuleDialog dialog = new RuleDialog(getShell(), rule);
                int result = dialog.open();
                if (result == RuleDialog.OK) {
                    setModified(true);
                    try {
                        refresh();
                    } catch (Throwable t) {
                        PMDUiPlugin.getDefault().logError("Exception when refreshing the rule table", t);
                    }
                }
            }
        });

        return button;
    }

    /**
     * Build the import ruleset button
     */
    private Button buildImportRuleSetButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_IMPORTRULESET));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                RuleSetSelectionDialog dialog = new RuleSetSelectionDialog(getShell());
                dialog.open();
                if (dialog.getReturnCode() == RuleSetSelectionDialog.OK) {
                    try {
                        RuleSet selectedRuleSet = dialog.getSelectedRuleSet();
                        ruleSet.addRuleSet(selectedRuleSet);
                        setModified(true);
                        try {
                            refresh();
                        } catch (Throwable t) {
                            PMDUiPlugin.getDefault().logError("Exception when refreshing the rule table", t);
                        }
                    } catch (RuntimeException e) {
                        PMDUiPlugin.getDefault().showError(getMessage(StringKeys.MSGKEY_ERROR_IMPORTING_RULESET), e);
                    }
                }
            }
        });

        return button;
    }

    /**
     * Build the export rule set button
     */
    private Button buildExportRuleSetButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_EXPORTRULESET));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionAdapter() {
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
                                    getMessage(StringKeys.MSGKEY_CONFIRM_TITLE),
                                    getMessage(StringKeys.MSGKEY_CONFIRM_RULESET_EXISTS));
                        }

                        InputDialog input = null;
                        if (flContinue) {
                            input =
                                new InputDialog(
                                    getShell(),
                                    getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_TITLE),
                                    getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_RULESET_DESCRIPTION),
                                    ruleSet.getDescription() == null ? "" : ruleSet.getDescription().trim(),
                                    null);
                            flContinue = input.open() == InputDialog.OK;
                        }

                        if (flContinue) {
                            ruleSet.setName(getFileNameWithoutExtension(file.getName()));
                            ruleSet.setDescription(input.getValue());
                            OutputStream out = new FileOutputStream(fileName);
                            IRuleSetWriter writer = PMDRuntimePlugin.getDefault().getRuleSetWriter();
                            writer.write(out, ruleSet);
                            out.close();
                            MessageDialog.openInformation(
                                getShell(),
                                getMessage(StringKeys.MSGKEY_INFORMATION_TITLE),
                                getMessage(StringKeys.MSGKEY_INFORMATION_RULESET_EXPORTED));
                        }
                    } catch (IOException e) {
                        PMDUiPlugin.getDefault().showError(getMessage(StringKeys.MSGKEY_ERROR_EXPORTING_RULESET), e);
                    } catch (WriterException e) {
                        PMDUiPlugin.getDefault().showError(getMessage(StringKeys.MSGKEY_ERROR_EXPORTING_RULESET), e);
                    }
                }
            }
        });

        return button;
    }

    /**
     * Build the clear all button
     */
    private Button buildClearAllButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_CLEARALL));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (MessageDialog
                    .openConfirm(
                        getShell(),
                        getMessage(StringKeys.MSGKEY_CONFIRM_TITLE),
                        getMessage(StringKeys.MSGKEY_CONFIRM_CLEAR_RULESET))) {
                    ruleSet.getRules().clear();
                    setModified(true);
                    try {
                        refresh();
                    } catch (Throwable t) {
                        PMDUiPlugin.getDefault().logError("Exception when refreshing the rule table", t);
                    }
                }
            }
        });

        return button;
    }

    /**
     * Build the add property button
     */
    private Button buildAddPropertyButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_BUTTON_ADDPROPERTY));
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                InputDialog input =
                    new InputDialog(
                        getShell(),
                        getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_TITLE),
                        getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_PROPERTY_NAME),
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
        });
        return button;
    }

    private static String asCleanString(String original) {
    	return original == null ? "" : original.trim();
    }
    
    /**
     * Populate the rule table
     */
    private void populateRuleTable() {
        RuleSet defaultRuleSet = PMDRuntimePlugin.getDefault().getPreferencesManager().getRuleSet();
        ruleSet = new RuleSet();
        ruleSet.addRuleSet(defaultRuleSet);
        ruleSet.setName(defaultRuleSet.getName());
        ruleSet.setDescription(asCleanString(defaultRuleSet.getDescription()));
        ruleTableViewer.setInput(ruleSet);
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDUiPlugin.getDefault().getStringTable().getString(key);
    }

    /**
     * Helper method to get a filename without its extension
     */
    protected String getFileNameWithoutExtension(String fileName) {
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
        return PMDUiPlugin.getDefault().getPreferenceStore();
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

    /**
     * Refresh the list
     */
    protected void refresh() {
        try {
            ruleTableViewer.getControl().setRedraw(false);
            ruleTableViewer.refresh();
        } catch (ClassCastException e) {
            PMDUiPlugin.getDefault().logError("Ignoring exception while refreshing table", e);
        } finally {
            ruleTableViewer.getControl().setRedraw(true);
        }
    }

    /**
     * Update the configured rule set
     * Update also all configured projects
     */
    private void updateRuleSet() {
        try {
            ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
            monitorDialog.run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    PMDRuntimePlugin.getDefault().getPreferencesManager().setRuleSet(ruleSet);
                }
            });
        } catch (InterruptedException e) {
            PMDUiPlugin.getDefault().logError("Exception updating all projects after a preference change", e);
        } catch (InvocationTargetException e) {
            PMDUiPlugin.getDefault().logError("Exception updating all projects after a preference change", e);
        }
    }

    /**
     * If user wants to, rebuild all projects
     */
    private void rebuildProjects() {
        if (MessageDialog
            .openQuestion(
                getShell(),
                getMessage(StringKeys.MSGKEY_QUESTION_TITLE),
                getMessage(StringKeys.MSGKEY_QUESTION_RULES_CHANGED))) {
            try {
                ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getShell());
                monitorDialog.run(true, true, new IRunnableWithProgress() {
                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        try {
                            ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
                        } catch (CoreException e) {
                            PMDUiPlugin.getDefault().logError("Exception building all projects after a preference change", e);
                        }
                    }
                });
            } catch (InterruptedException e) {
                PMDUiPlugin.getDefault().logError("Exception building all projects after a preference change", e);
            } catch (InvocationTargetException e) {
                PMDUiPlugin.getDefault().logError("Exception building all projects after a preference change", e);
            }
        }
    }

    /**
     * Select and show a particular rule in the table
     */
    protected void selectAndShowRule(Rule rule) {
        Table table = ruleTableViewer.getTable();
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++) {
            Rule itemRule = (Rule) items[i].getData();
            if (itemRule.equals(rule)) {
                table.setSelection(table.indexOf(items[i]));
                table.showSelection();
                break;
            }
        }
    }

}