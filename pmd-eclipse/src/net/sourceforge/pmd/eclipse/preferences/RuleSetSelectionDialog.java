package net.sourceforge.pmd.eclipse.preferences;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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
    private String importedRuleSetName;

    /**
     * Constructor for RuleSetSelectionDialog.
     * @param parentdlgArea
     */
    public RuleSetSelectionDialog(Shell parent) {
        super(parent);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite dlgArea = new Composite(parent, SWT.NULL);

        // Create controls (order is important)
        buildLabel(dlgArea, getMessage(PMDConstants.MSGKEY_PREF_RULESETSELECTION_LABEL_ENTER_RULESET));
        buildLabel(dlgArea, "");
        inputCombo = buildInputCombo(dlgArea);
        buildBrowseButton(dlgArea);

        // Layout controls
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        dlgArea.setLayout(gridLayout);

        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.widthHint = 200;
        inputCombo.setLayoutData(data);
        
        // Set the window title
        getShell().setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_DIALOG_TITLE));


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
        combo.setItems(PMDPlugin.RULESET_ALLPMD);
        combo.setText("");
        combo.setToolTipText(getMessage(PMDConstants.MSGKEY_PREF_RULESETSELECTION_TOOLTIP_RULESET));
        return combo;
    }

    /**
     * Build the browse push button
     */
    private Button buildBrowseButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESETSELECTION_BUTTON_BROWSE));
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
     * Returns the importedRuleSetName.
     * @return String
     */
    public String getImportedRuleSetName() {
        return importedRuleSetName;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        importedRuleSetName = inputCombo.getText();
        if (importedRuleSetName.equals("")) {
            importedRuleSetName = null;
        }
        
        super.okPressed();
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
