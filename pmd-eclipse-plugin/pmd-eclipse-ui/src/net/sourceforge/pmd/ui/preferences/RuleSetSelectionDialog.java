package net.sourceforge.pmd.ui.preferences;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.core.PMDCorePlugin;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:23:38  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.4  2006/04/10 20:57:16  phherlin
 * Update to PMD 3.6
 *
 * Revision 1.3  2005/06/07 22:40:06  phherlin
 * Implementing extra ruleset declaration
 *
 * Revision 1.2  2003/08/13 20:09:06  phherlin
 * Refactoring private->protected to remove warning about non accessible member access in enclosing types
 *
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RuleSetSelectionDialog extends Dialog {
    protected Combo inputCombo;
    private Button referenceButton;
    private Button copyButton;
    private String importedRuleSetName;
    private final RuleSet[] ruleSets;
    private final String[] ruleSetNames;
    private RuleSet selectedRuleSet;
    private boolean importByReference;

    /**
     * Constructor for RuleSetSelectionDialog.
     * @param parentdlgArea
     */
    public RuleSetSelectionDialog(Shell parent) {
        super(parent);
        Set registeredRuleSets = PMDCorePlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
        SortedSet sortedRuleSets = new TreeSet(new Comparator() {
            public boolean equals(Object arg0) {
                return false;
            }

            public int compare(Object arg0, Object arg1) {
                RuleSet ruleSet1 = (RuleSet) arg0;
                RuleSet ruleSet2 = (RuleSet) arg1;
                return ruleSet1.getName().compareToIgnoreCase(ruleSet2.getName());
            }
        });
        sortedRuleSets.addAll(registeredRuleSets);
        
        ruleSets = new RuleSet[sortedRuleSets.size()];
        ruleSetNames = new String[sortedRuleSets.size()];
        Iterator i = sortedRuleSets.iterator();
        int index = 0;
        while (i.hasNext()) {
            ruleSets[index] = (RuleSet) i.next();
            ruleSetNames[index] = ruleSets[index].getName();
            index++;
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite dlgArea = new Composite(parent, SWT.NULL);

        // Layout controls
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        dlgArea.setLayout(gridLayout);

        // Create controls (order is important)
        Label enterRuleSetLabel = buildLabel(dlgArea, getMessage(StringKeys.MSGKEY_PREF_RULESETSELECTION_LABEL_ENTER_RULESET));
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
        getShell().setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_TITLE));


        return dlgArea;
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
        combo.setItems(this.ruleSetNames);
        combo.setText("");
        combo.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_RULESETSELECTION_TOOLTIP_RULESET));
        return combo;
    }

    /**
     * Build the browse push button
     */
    private Button buildBrowseButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESETSELECTION_BUTTON_BROWSE));
        button.setEnabled(true);
        button.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                String fileName = dialog.open();
                if (fileName != null) {
                    inputCombo.setText(fileName);
                }
            }

            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });
        return button;
    }

    /**
     * Build the reference button
     */
    private Button buildReferenceButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESETSELECTION_BUTTON_REFERENCE));
        button.setSelection(true);
        importByReference = true;
        button.addSelectionListener(new SelectionAdapter() {
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
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULESETSELECTION_BUTTON_COPY));
        button.setSelection(false);
        button.addSelectionListener(new SelectionAdapter() {
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
        return this.importedRuleSetName;
    }
    
    /**
     * @return the selected ruleSet
     */
    public RuleSet getSelectedRuleSet() {
        return this.selectedRuleSet;
    }
    
    /**
     * @return import by reference
     */
    public boolean isImportByReference() {
    	return importByReference;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        int selectionIndex = this.inputCombo.getSelectionIndex();
        if (selectionIndex == -1) {
            importedRuleSetName = inputCombo.getText();
            if (!importedRuleSetName.equals("")) {
                try {
                    final RuleSetFactory factory = new RuleSetFactory();
                    this.selectedRuleSet = factory.createRuleSets(this.importedRuleSetName).getAllRuleSets()[0];
                } catch (RuleSetNotFoundException e) {
                    PMDUiPlugin.getDefault().showError(getMessage(StringKeys.MSGKEY_ERROR_RULESET_NOT_FOUND), e);
                }
            }
        } else {
            this.selectedRuleSet = this.ruleSets[selectionIndex];
        }
        
        super.okPressed();
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDUiPlugin.getDefault().getStringTable().getString(key);
    }

}
