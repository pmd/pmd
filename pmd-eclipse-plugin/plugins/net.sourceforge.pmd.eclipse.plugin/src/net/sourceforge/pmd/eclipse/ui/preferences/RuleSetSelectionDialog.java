package net.sourceforge.pmd.eclipse.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleColumnDescriptor;
import net.sourceforge.pmd.eclipse.ui.preferences.br.RuleLabelProvider;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Implements a dialog for the user to select a rule set to import
 *
 * @author Philippe Herlin
 * @author Brian Remedios
 *
 */
public class RuleSetSelectionDialog extends Dialog {
	
    private Combo		inputCombo;
    private Button		referenceButton;
    private Button		copyButton;
    private String		importedRuleSetName;
    private RuleSet		selectedRuleSet;
    private boolean		importByReference;
    
    private Label		warningField;
    private CheckboxTableViewer ruleTable;
    
    private RuleSet		checkedRules;
    
    private final RuleDupeChecker dupeChecker;
    private final String title;
    private final RuleSet[] ruleSets;
    private final String[] ruleSetNames;
    private final RuleColumnDescriptor[] columns;
    
    private static String labelFor(RuleSet rs) {
    	
    	Collection<Rule> rules = rs.getRules();
    	String lang = rules.iterator().next().getLanguage().getShortName();
    	return lang + " - " + rs.getName() + "  (" + rules.size() + ")";
    }
    
    /**
     * Constructor for RuleSetSelectionDialog.
     * @param parentdlgArea
     */
    public RuleSetSelectionDialog(Shell parent, String theTitle, RuleColumnDescriptor[] theColumns, RuleDupeChecker theDupeChecker) {
        super(parent);
        
        title = theTitle;
        dupeChecker = theDupeChecker;
        columns = theColumns;
        
        setShellStyle(getShellStyle() | SWT.RESIZE );
        
        Set<RuleSet> registeredRuleSets = PMDPlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
        SortedSet<RuleSet> sortedRuleSets = new TreeSet<RuleSet>(new Comparator<RuleSet>() {
            public int compare(RuleSet ruleSet1, RuleSet ruleSet2) {
                return labelFor(ruleSet1).compareToIgnoreCase(labelFor(ruleSet2));
            }
        });
        sortedRuleSets.addAll(registeredRuleSets);

        ruleSets = new RuleSet[sortedRuleSets.size()];
        ruleSetNames = new String[sortedRuleSets.size()];
        Iterator<RuleSet> i = sortedRuleSets.iterator();
        int index = 0;
        while (i.hasNext()) {
            ruleSets[index] = i.next();
            ruleSetNames[index] = ruleSets[index].getName();
            if (!ruleSets[index].getRules().isEmpty()) {
                ruleSetNames[index] = labelFor(ruleSets[index]);
            }
            index++;
        }
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        shell.setSize(500, 400);
    }
    
	protected boolean isResizable() {
		return true;
	}
	
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
    	
        Composite dlgArea = new Composite(parent, SWT.NULL);
        dlgArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        // Layout controls
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        dlgArea.setLayout(gridLayout);

        // Create controls (order is important)
        Label enterRuleSetLabel = buildLabel(dlgArea, getMessage(StringKeys.PREF_RULESETSELECTION_LABEL_ENTER_RULESET));
        GridData data = new GridData();
        data.horizontalSpan = 3;
        data.widthHint = 200;
        enterRuleSetLabel.setLayoutData(data);

        inputCombo = buildInputCombo(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        inputCombo.setLayoutData(data);

        buildBrowseButton(dlgArea);

        referenceButton = buildReferenceButton(dlgArea);

        copyButton = buildCopyButton(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        copyButton.setLayoutData(data);
        
        ruleTable = CheckboxTableViewer.newCheckList(dlgArea, SWT.BORDER);
        data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 3;
        ruleTable.getTable().setLayoutData(data);
        setupRuleTable();
        
        warningField = new Label(dlgArea, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 3;
        data.grabExcessHorizontalSpace = true;
    	warningField.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
        warningField.setLayoutData(data);
        
        getShell().setText(title);
        return dlgArea;
    }

	protected void createCheckBoxColumn(Table table) {
		
		TableColumn tc = new TableColumn(table, 0);
		tc.setWidth(30);
		tc.setResizable(false);
		tc.pack();
		
//        tc.addListener(SWT.Selection, new Listener() {
//            public void handleEvent(Event e) {
//               sortByCheckedItems();
//            }
//          });
	}
	
    private void setupRuleTable() {
    	
    	Table tbl = ruleTable.getTable();
    	tbl.setLinesVisible(true);
		tbl.setHeaderVisible(true);

    	ruleTable.setContentProvider( new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				RuleSet rs = selectedRuleset();
				return rs == null ? new Object[0] : rs.getRules().toArray();
			}

			public void dispose() {	}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	} 		
    	});
       	
    	ruleTable.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				ruleChecked();				
			}} );
    	
    	createCheckBoxColumn(tbl);
    	
    	for (int i=0; i<columns.length; i++) {
    		columns[i].newTableColumnFor(tbl, i+1, null, null);
    	}
       	
    	ruleTable.setLabelProvider( new RuleLabelProvider(columns) );
    }
    
	private void checkNonDupes() {

		RuleSet rs = selectedRuleset();
		if (rs == null) {
			ruleTable.setCheckedElements(new Object[0]);
			return;
		}
		
		List<Rule> nonDupes = new ArrayList<Rule>();

		for (Rule rule : rs.getRules()) {
			if (dupeChecker.isDuplicate(rule)) continue;
			nonDupes.add(rule);
		}

		ruleTable.setCheckedElements(nonDupes.toArray());
	}
    
    public void create() {
    	super.create();
        updateControls();
    }
    
    /**
     * Build the labels
     */
    private Label buildLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        return label;
    }

    /**
     * Build the input combo box
     */
    private Combo buildInputCombo(Composite parent) {
        Combo combo = new Combo(parent, SWT.NONE);
        combo.setItems(ruleSetNames);
        combo.setText("");
        combo.setToolTipText(getMessage(StringKeys.PREF_RULESETSELECTION_TOOLTIP_RULESET));
        combo.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
               ruleSetChanged();
            }
        });
        return combo;
    }
    
    /**
     * Build the browse push button
     */
    private Button buildBrowseButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(getMessage(StringKeys.PREF_RULESETSELECTION_BUTTON_BROWSE));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                String fileName = dialog.open();
                if (StringUtil.isNotEmpty(fileName)) {
                    inputCombo.setText(fileName);
                    ruleSetChanged();
                }
            }
        });
        return button;
    }

    /**
     * Build the reference button
     */
    private Button buildReferenceButton(Composite parent) {
        final Button button = new Button(parent, SWT.RADIO);
        button.setText(getMessage(StringKeys.PREF_RULESETSELECTION_BUTTON_REFERENCE));
        button.setSelection(true);
        importByReference = true;
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	importByReference = true;
            }
        });

        return button;
    }

    /**
     * Build the copy button
     */
    private Button buildCopyButton(Composite parent) {
        final Button button = new Button(parent, SWT.RADIO);
        button.setText(getMessage(StringKeys.PREF_RULESETSELECTION_BUTTON_COPY));
        button.setSelection(false);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	importByReference = false;
            }
        });

        return button;
    }

    private void ruleSetChanged() {
    	updateRuleTable();
    	checkNonDupes();
 		warningField.setText("");
 		adjustOKButton();
    }
    
    /**
     * Returns the importedRuleSetName.
     * @return String
     */
    public String getImportedRuleSetName() {
        return importedRuleSetName;
    }

    private boolean hasCheckedRules() {
	    return ruleTable.getCheckedElements().length > 0;
    }
    
    /**
     * Return the effective ruleset as the ones selected by the user.
     * 
     * @return
     */
    private RuleSet getSelectedRules() {
    	
    	RuleSet rs = new RuleSet();
    	rs.setFileName( selectedRuleSet.getFileName() );
    	rs.addExcludePatterns( selectedRuleSet.getExcludePatterns() );
    	rs.addIncludePatterns( selectedRuleSet.getIncludePatterns() );
    	
    	for (Object rul : ruleTable.getCheckedElements()) {
    		rs.addRule((Rule) rul);
    	}
    	
    	return rs;
    }
    
    /**
     * @return import by reference
     */
    public boolean isImportByReference() {
    	return importByReference;
    }
    
    private void adjustOKButton() {
    	boolean hasChecks = hasCheckedRules();
    	getButton(IDialogConstants.OK_ID).setEnabled(hasChecks);
    }
    
    private void ruleChecked() {
    	updateWarningField();
    	adjustOKButton();
    }
    
    private void updateControls() {
  	   	
    	updateWarningField();
    	adjustOKButton();
    }
    
    private void updateRuleTable() {
    	
    	RuleSet candidateRS = selectedRuleset();
    	if (candidateRS == null) {
 //   		warningField.setText("");
    		ruleTable.getTable().clearAll();
    		return;
    	}
    	
    	showRules(candidateRS);
    }
    
    private boolean updateWarningField() {
    	
    	int dupeCount = 0;
    	
    	for (Object rule : ruleTable.getCheckedElements()) {
    		if (dupeChecker.isDuplicate((Rule)rule)) dupeCount++;
    	}
    	 
    	warningField.setText(
    			dupeCount == 0 ? "" :
    			"Warning, " + dupeCount + " checked rules already exist in your ruleset");
    	
    	return dupeCount > 0;
    }
    
    private void showRules(RuleSet rs) {
    	
    	ruleTable.setInput(rs);
		
    	TableColumn[] columns = ruleTable.getTable().getColumns();
		for (TableColumn column : columns) column.pack();
    }
    
    private RuleSet selectedRuleset() {
    	
    	 int selectionIndex = inputCombo.getSelectionIndex();

         if (selectionIndex == -1) {
             importedRuleSetName = inputCombo.getText();
             if (StringUtil.isNotEmpty(importedRuleSetName)) {
            	 try {
            		 RuleSetFactory factory = new RuleSetFactory();
            		 RuleSets rs = factory.createRuleSets(importedRuleSetName);
            		 return rs.getAllRuleSets()[0];   
            	 	} catch (RuleSetNotFoundException rsnfe) {
            	 		warningField.setText( rsnfe.getMessage() );
            	 		return null;
            	 	}
             }
        } 
         
        return ruleSets[selectionIndex];
    }
    
    public RuleSet checkedRules() {
    	return checkedRules;
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
    	
    	// get the selections before the widget goes away
    	selectedRuleSet = selectedRuleset();
    	checkedRules = getSelectedRules();    	

        super.okPressed();
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

}
