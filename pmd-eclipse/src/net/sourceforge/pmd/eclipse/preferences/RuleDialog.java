package net.sourceforge.pmd.eclipse.preferences;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.rules.XPathRule;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a dialog for adding or editing a rule.
 * When editing, the rule is automatically updated. The caller has no need to
 * test if the dialog was OK or not.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/07/07 19:25:36  phherlin
 * Adding PMD violations view
 *
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RuleDialog extends Dialog {
    private static final int MODE_ADD = 1;
    private static final int MODE_EDIT = 2;
    private static final int MODE_VIEW = 3;

    private int mode = MODE_ADD;
    private Rule editedRule;
    private Rule rule;
    private Text nameText;
    private Button xpathRuleButton;
    private Text implementationClassText;
    private Text messageText;
    private Text descriptionText;
    private Text exampleText;
    private Font courierFont;

    /**
     * Constructor for RuleDialog.
     * @param parentdlgArea
     */
    public RuleDialog(Shell parent) {
        super(parent);
        mode = MODE_ADD;
    }

    /**
     * Constructor for RuleDialog.
     * @param parentdlgArea
     */
    public RuleDialog(Shell parent, Rule editedRule) {
        super(parent);
        mode = MODE_EDIT;
        this.editedRule = editedRule;
    }

    /**
     * Constructor for RuleDialog.
     * @param parentdlgArea
     */
    public RuleDialog(Shell parent, Rule editedRule, boolean flEdit) {
        super(parent);
        mode = flEdit ? MODE_EDIT : MODE_VIEW;
        this.editedRule = editedRule;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
     */
    protected Control createDialogArea(Composite parent) {
        courierFont = new Font(getShell().getDisplay(), "Courier New", 10, SWT.NORMAL);

        Composite dlgArea = new Composite(parent, SWT.NULL);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        dlgArea.setLayout(gridLayout);

        Label nameLabel = buildLabel(dlgArea, PMDConstants.MSGKEY_PREF_RULEEDIT_LABEL_NAME);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        nameLabel.setLayoutData(data);

        nameText = buildNameText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        nameText.setLayoutData(data);

        xpathRuleButton = buildXPathRuleButton(dlgArea);

        Label implementationClassLabel = buildLabel(dlgArea, PMDConstants.MSGKEY_PREF_RULEEDIT_LABEL_IMPLEMENTATION_CLASS);
        data = new GridData();
        data.horizontalSpan = 2;
        implementationClassLabel.setLayoutData(data);

        implementationClassText = buildImplementationClassText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        implementationClassText.setLayoutData(data);

        Label messageLabel = buildLabel(dlgArea, PMDConstants.MSGKEY_PREF_RULEEDIT_LABEL_MESSAGE);
        data = new GridData();
        data.horizontalSpan = 2;
        messageLabel.setLayoutData(data);

        messageText = buildMessageText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        messageText.setLayoutData(data);

        Label descriptionLabel = buildLabel(dlgArea, PMDConstants.MSGKEY_PREF_RULEEDIT_LABEL_DESCRIPTION);
        data = new GridData();
        data.horizontalSpan = 2;
        descriptionLabel.setLayoutData(data);

        descriptionText = buildDescriptionText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.widthHint = 300;
        data.heightHint = 100;
        descriptionText.setLayoutData(data);

        Label exampleLabel = buildLabel(dlgArea, PMDConstants.MSGKEY_PREF_RULEEDIT_LABEL_EXAMPLE);
        data = new GridData();
        data.horizontalSpan = 2;
        exampleLabel.setLayoutData(data);

        exampleText = buildExampleText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.widthHint = 300;
        data.heightHint = 100;
        exampleText.setLayoutData(data);

        // Set the window title
        getShell().setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_DIALOG_TITLE));

        return dlgArea;
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
     * Build the rule name text
     */
    private Text buildNameText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        if (mode == MODE_ADD) {
            text.setFocus();
        }

        if (mode == MODE_EDIT) {
            text.setText(editedRule.getName());
            text.setEnabled(false);
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
            text.setText(editedRule.getName());
        }

        return text;
    }

    /**
     * Build the XPath rule button
     */
    private Button buildXPathRuleButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(PMDConstants.MSGKEY_PREF_RULEEDIT_BUTTON_XPATH_RULE));

        if (mode == MODE_VIEW) {
            button.setVisible(false);
        } else {
            if (mode == MODE_EDIT) {
                button.setSelection(editedRule instanceof XPathRule);
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
                button.setSelection(false);
            }

            button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    if (button.getSelection()) {
                        implementationClassText.setText(XPathRule.class.getName());
                        implementationClassText.setEnabled(false);
                    } else {
                        implementationClassText.setText("");
                        implementationClassText.setEnabled(true);
                    }
                }
            });
        }

        return button;
    }

    /**
     * Build the implementation class text
     */
    private Text buildImplementationClassText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        if (mode == MODE_EDIT) {
            text.setText(editedRule.getClass().getName());
            text.setEnabled(false);
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
            text.setText(editedRule.getClass().getName());
        }

        return text;
    }

    /**
     * Build the message text
     */
    private Text buildMessageText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        if (mode == MODE_EDIT) {
            text.setFocus();
            text.setText(editedRule.getMessage().trim());
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
            text.setText(editedRule.getMessage().trim());
        }

        return text;
    }

    /**
     * Build the description text
     */
    private Text buildDescriptionText(Composite parent) {
        Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        if (mode == MODE_EDIT) {
            text.setText(editedRule.getDescription().trim());
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
            text.setText(editedRule.getDescription().trim());
        }

        return text;
    }

    /**
     * Build the example text
     */
    private Text buildExampleText(Composite parent) {
        Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        text.setFont(courierFont);
        if (mode == MODE_EDIT) {
            text.setText(editedRule.getExample().trim());
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
            text.setText(editedRule.getExample().trim());
        }

        return text;
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
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        if (validateForm() && (mode != MODE_VIEW)) {
            super.okPressed();
        }
    }

    /**
     * Perform the form validation
     */
    private boolean validateForm() {
        return validateName() && validateMessage() && validateImplementationClass();
    }

    /**
     * Perform the name validation
     */
    private boolean validateName() {
        boolean flValid = true;

        String name = nameText.getText();
        if (name.trim().length() == 0) {
            MessageDialog.openWarning(
                getShell(),
                getMessage(PMDConstants.MSGKEY_WARNING_TITLE),
                getMessage(PMDConstants.MSGKEY_WARNING_NAME_MANDATORY));
            nameText.setFocus();
            flValid = false;
        }

        return flValid;
    }

    /**
     * Perform the messageValidation
     */
    private boolean validateMessage() {
        boolean flValid = true;

        String message = messageText.getText();
        if (message.trim().length() == 0) {
            MessageDialog.openWarning(
                getShell(),
                getMessage(PMDConstants.MSGKEY_WARNING_TITLE),
                getMessage(PMDConstants.MSGKEY_WARNING_MESSAGE_MANDATORY));
            messageText.setFocus();
            flValid = false;
        }

        return flValid;
    }

    /**
     * Perform the implementation class validation
     */
    private boolean validateImplementationClass() {
        boolean flValid = true;
        boolean flClassError = false;

        // Instantiate the rule (add mode)
        if (mode == MODE_ADD) {
            try {
                Class ruleClass = Class.forName(implementationClassText.getText());
                Object instance = ruleClass.newInstance();
                if (instance instanceof Rule) {
                    rule = (Rule) ruleClass.newInstance();
                    rule.setName(nameText.getText().trim());
                    rule.setExample(exampleText.getText());
                    rule.setInclude(false);
                    rule.setMessage(messageText.getText().trim());
                    rule.setDescription(descriptionText.getText());
                    rule.setPriority(3);
                    if (rule instanceof XPathRule) {
                        rule.addProperty("xpath", "");
                    }
                } else {
                    flClassError = true;
                }
            } catch (ClassNotFoundException e) {
                flClassError = true;
            } catch (InstantiationException e) {
                flClassError = true;
            } catch (IllegalAccessException e) {
                flClassError = true;
            }
        }

        // else only modify appropriate fields (edit mode)
        else {
            editedRule.setExample(exampleText.getText());
            editedRule.setMessage(messageText.getText().trim());
            editedRule.setDescription(descriptionText.getText());
        }

        // Display class error if needed
        if (flClassError) {
            MessageDialog.openWarning(
                getShell(),
                getMessage(PMDConstants.MSGKEY_WARNING_TITLE),
                getMessage(PMDConstants.MSGKEY_WARNING_CLASS_INVALID));
            implementationClassText.setFocus();
            flValid = false;
        }

        return flValid;
    }

    /**
     * Returns the rule.
     * @return Rule
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     */
    protected void cancelPressed() {
        courierFont.dispose();
        super.cancelPressed();
    }

}
