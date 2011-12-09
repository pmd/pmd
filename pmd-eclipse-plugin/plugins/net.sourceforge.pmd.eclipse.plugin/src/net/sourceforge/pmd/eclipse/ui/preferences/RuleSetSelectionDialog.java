package net.sourceforge.pmd.eclipse.ui.preferences;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

/**
 * Implements a dialog for the user to select a rule set to import
 *
 * @author Philippe Herlin
 *
 */
public class RuleSetSelectionDialog extends Dialog {
	
    private Combo		inputCombo;
    private Button		referenceButton;
    private Button		copyButton;
    private String		importedRuleSetName;
    private RuleSet		selectedRuleSet;
    private boolean		importByReference;
    
    private final RuleSet[] ruleSets;
    private final String[] ruleSetNames;
    
    private static String labelFor(RuleSet rs) {
    	String lang = rs.getRules().iterator().next().getLanguage().getShortName();
    	return lang + " - " + rs.getName();
    }
    
    /**
     * Constructor for RuleSetSelectionDialog.
     * @param parentdlgArea
     */
    public RuleSetSelectionDialog(Shell parent) {
        super(parent);
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

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dlgArea = new Composite(parent, SWT.NULL);

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

        // Set the window title
        getShell().setText(getMessage(StringKeys.PREF_RULESET_DIALOG_TITLE));
        
        return dlgArea;
    }

    protected Control createContents(Composite parent) {
    	Control ctrl = super.createContents(parent);
        updateControls();        
        return ctrl;
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
                updateControls();
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
                if (fileName != null) {
                    inputCombo.setText(fileName);
                }
                updateControls();
            }
        });
        return button;
    }

    /**
     * Build the reference button
     */
    private Button buildReferenceButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.PREF_RULESETSELECTION_BUTTON_REFERENCE));
        button.setSelection(true);
        importByReference = true;
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
            	copyButton.setSelection(false);
            	importByReference = true;
            }
        });

        return button;
    }

    /**
     * Build the copy button
     */
    private Button buildCopyButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.PREF_RULESETSELECTION_BUTTON_COPY));
        button.setSelection(false);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
            	referenceButton.setSelection(false);
            	importByReference = false;
            }
        });

        return button;
    }

    /**
     * Returns the importedRuleSetName.
     * @return String
     */
    public String getImportedRuleSetName() {
        return importedRuleSetName;
    }

    /**
     * @return the selected ruleSet
     */
    public RuleSet getSelectedRuleSet() {
        return selectedRuleSet;
    }

    /**
     * @return import by reference
     */
    public boolean isImportByReference() {
    	return importByReference;
    }
    
    private void updateControls() {
    	boolean hasItem = inputCombo.getSelectionIndex() > 0;
    	getButton(IDialogConstants.OK_ID).setEnabled(hasItem);
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        int selectionIndex = inputCombo.getSelectionIndex();
        if (selectionIndex == -1) {
            importedRuleSetName = inputCombo.getText();
            if (StringUtil.isNotEmpty(importedRuleSetName)) {
                try {
                    RuleSetFactory factory = new RuleSetFactory();
                    selectedRuleSet = factory.createRuleSets(importedRuleSetName).getAllRuleSets()[0];
                } catch (RuleSetNotFoundException e) {
                    PMDPlugin.getDefault().showError(getMessage(StringKeys.ERROR_RULESET_NOT_FOUND), e);
                }
            }
        } else {
            selectedRuleSet = ruleSets[selectionIndex];
        }

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
