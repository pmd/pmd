package net.sourceforge.pmd.eclipse.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleReference;
import net.sourceforge.pmd.rules.XPathRule;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * Implements a dialog for adding or editing a rule. When editing, the rule is
 * automatically updated. The caller has no need to test if the dialog was OK or
 * not.
 *
 * @author Philippe Herlin
 *
 */
public class RuleDialog extends Dialog {
    private static final int MODE_ADD = 1;
    private static final int MODE_EDIT = 2;
    private static final int MODE_VIEW = 3;

    protected Text implementationClassText;
    private int mode = MODE_ADD;
    private Rule editedRule;
    private Rule rule;
    private Text ruleSetNameText;
    private Button ruleReferenceButton;
    private Text sinceText;
    private Text nameText;
    private Button xpathRuleButton;
    private Text messageText;
    private Combo priorityCombo;
    protected Button usesTypeResolutionButton;
    protected Button usesDfaButton;
    private Text descriptionText;
    private Text externalInfoUrlText;
    protected Button openExternalInfoUrlButton;
    private Text exampleText;
    protected Text xpathText;
    private Font courierFont;

    /**
     * Constructor for RuleDialog.
     *
     * @param parentdlgArea
     */
    public RuleDialog(Shell parent) {
        super(parent);
        mode = MODE_ADD;
    }

    /**
     * Constructor for RuleDialog.
     *
     * @param parentdlgArea
     */
    public RuleDialog(Shell parent, Rule editedRule) {
        super(parent);
        mode = MODE_EDIT;
        this.editedRule = editedRule;
    }

    /**
     * Constructor for RuleDialog.
     *
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
        gridLayout.numColumns = 4;
        dlgArea.setLayout(gridLayout);

        Label ruleSetNameLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_RULESET_NAME);
        GridData data = new GridData();
        data.horizontalSpan = 1;
        ruleSetNameLabel.setLayoutData(data);

        ruleSetNameText = buildRuleSetNameText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        ruleSetNameText.setLayoutData(data);

        ruleReferenceButton = buildRuleReferenceButton(dlgArea);

        Label sinceLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_SINCE);
        data = new GridData();
        data.horizontalSpan = 1;
        sinceLabel.setLayoutData(data);

        sinceText = buildSinceText(dlgArea);
        data = new GridData();
//        data.horizontalAlignment = GridData.;
        data.horizontalSpan = 3;
        data.grabExcessHorizontalSpace = false;
        sinceText.setLayoutData(data);

        Label nameLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_NAME);
        data = new GridData();
        data.horizontalSpan = 4;
        nameLabel.setLayoutData(data);

        nameText = buildNameText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 3;
        data.grabExcessHorizontalSpace = true;
        nameText.setLayoutData(data);

        xpathRuleButton = buildXPathRuleButton(dlgArea);

        Label implementationClassLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_IMPLEMENTATION_CLASS);
        data = new GridData();
        data.horizontalSpan = 4;
        implementationClassLabel.setLayoutData(data);

        implementationClassText = buildImplementationClassText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 4;
        data.grabExcessHorizontalSpace = true;
        implementationClassText.setLayoutData(data);

        Label messageLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_MESSAGE);
        data = new GridData();
        data.horizontalSpan = 4;
        messageLabel.setLayoutData(data);

        messageText = buildMessageText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 4;
        data.grabExcessHorizontalSpace = true;
        messageText.setLayoutData(data);

        Label priorityLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_PRIORITY);
        data = new GridData();
        data.horizontalSpan = 1;
        priorityLabel.setLayoutData(data);

        priorityCombo = buildPriorityCombo(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 1;
        data.grabExcessHorizontalSpace = true;
        priorityCombo.setLayoutData(data);

        usesTypeResolutionButton = buildUsesTypeResolutionButton(dlgArea);

        usesDfaButton = buildUsesDfaButton(dlgArea);

        Label descriptionLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_DESCRIPTION);
        data = new GridData();
        data.horizontalSpan = 4;
        descriptionLabel.setLayoutData(data);

        descriptionText = buildDescriptionText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 4;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.widthHint = 300;
        data.heightHint = 100;
        descriptionText.setLayoutData(data);

        Label externalInfoUrlLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_EXTERNAL_INFO_URL);
        data = new GridData();
        data.horizontalSpan = 3;
        externalInfoUrlLabel.setLayoutData(data);

        openExternalInfoUrlButton = buildOpenExternalInfoUrlButton(dlgArea);

        externalInfoUrlText = buildExternalInfoUrlText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 4;
        data.grabExcessHorizontalSpace = true;
        externalInfoUrlText.setLayoutData(data);

        Label exampleLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_EXAMPLES);
        data = new GridData();
        data.horizontalSpan = 4;
        exampleLabel.setLayoutData(data);

        exampleText = buildExampleText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 4;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.widthHint = 300;
        data.heightHint = 70;
        exampleText.setLayoutData(data);

        Label xpathLabel = buildLabel(dlgArea, StringKeys.MSGKEY_PREF_RULEEDIT_LABEL_XPATH);
        data = new GridData();
        data.horizontalSpan = 4;
        xpathLabel.setLayoutData(data);

        xpathText = buildXPathText(dlgArea);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 4;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.widthHint = 300;
        data.heightHint = 100;
        xpathText.setLayoutData(data);

        // Set the window title
        getShell().setText(getMessage(StringKeys.MSGKEY_PREF_RULESET_DIALOG_TITLE));

        refreshOverridden();

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
     * Build the rule set name text
     */
    private Text buildRuleSetNameText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        if (mode == MODE_ADD) {
        	text.setText("pmd-eclipse");
        	text.setEnabled(false);
        }

        if (mode == MODE_EDIT) {
       		text.setText(editedRule.getRuleSetName());
            text.setEnabled(false);
        }

        if (mode == MODE_VIEW) {
       		text.setText(editedRule.getRuleSetName());
            text.setEnabled(false);
        }

        return text;
    }

    /**
     * Build the rule reference button
     */
    private Button buildRuleReferenceButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULEEDIT_BUTTON_RULE_REFERENCE));
        button.setEnabled(false);
        button.setSelection(editedRule instanceof RuleReference);
        return button;
    }

    /**
     * Build the since text
     */
    private Text buildSinceText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
    	text.setEnabled(false);

    	String since = "n/a";
    	if (editedRule != null && editedRule.getSince() != null)
    	{
    		since = editedRule.getSince();
    	}

    	if (mode == MODE_ADD) {
        	text.setText(since);
        }

        if (mode == MODE_EDIT) {
        	text.setText(since);
        }

        if (mode == MODE_VIEW) {
        	text.setText(since);
        }

        return text;
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
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULEEDIT_BUTTON_XPATH_RULE));

        if (mode == MODE_VIEW) {
            button.setVisible(false);
        } else {
            if (mode == MODE_EDIT) {
                button.setSelection(editedRule.getRuleClass().endsWith("XPathRule"));
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
                button.setSelection(true);
            }

            button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    if (button.getSelection()) {
                        implementationClassText.setText(XPathRule.class.getName());
                        implementationClassText.setEnabled(false);
                        xpathText.setEnabled(true);
                        usesTypeResolutionButton.setEnabled(false);
                        usesTypeResolutionButton.setSelection(true);
                        usesDfaButton.setEnabled(false);
                        usesDfaButton.setSelection(false);
                    } else {
                        implementationClassText.setText("");
                        implementationClassText.setEnabled(true);
                        xpathText.setText("");
                        xpathText.setEnabled(false);
                        usesTypeResolutionButton.setEnabled(true);
                        usesTypeResolutionButton.setSelection(true);
                        usesDfaButton.setEnabled(true);
                        usesDfaButton.setSelection(false);
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
            text.setText(editedRule.getRuleClass());
            text.setEnabled(false);
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
            text.setText(editedRule.getRuleClass());
        }

        if (mode == MODE_ADD) {
        	text.setText(XPathRule.class.getName());
            text.setEnabled(false);
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
     * Build the priority combo
     */
    private Combo buildPriorityCombo(Composite parent) {
        Combo combo = new Combo(parent, SWT.SINGLE | SWT.BORDER);
    	String[] labels = PMDPlugin.getDefault().getPriorityLabels();
    	int index = 3-1;
		if (editedRule != null && editedRule.getPriority() >= 0 && editedRule.getPriority() <= labels.length) {
			index = editedRule.getPriority() - 1;
		}
    	for (int i = 0; i < labels.length; i++) {
    		String label = labels[i];
    		combo.add(label);
    	}
    	combo.select(index);

        if (mode == MODE_VIEW) {
    		combo.setEnabled(false);
        }
        else if (mode == MODE_EDIT) {
    		combo.setEnabled(true);
        }
	    else if (mode == MODE_ADD) {
    		combo.setEnabled(true);
	    }
        return combo;
    }

    /**
     * Build the uses type resolution button
     */
    private Button buildUsesTypeResolutionButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULEEDIT_BUTTON_USES_TYPE_RESOLUTION));

        if (mode == MODE_VIEW) {
            button.setVisible(false);
        } else {
            if (mode == MODE_EDIT) {
                button.setEnabled(false);
                button.setSelection(editedRule.usesTypeResolution());
            } else {
                button.setEnabled(false);
                button.setSelection(true);
            }
        }

        return button;
    }

    /**
     * Build the uses dfa button
     */
    private Button buildUsesDfaButton(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULEEDIT_BUTTON_USES_DFA));

        if (mode == MODE_VIEW) {
            button.setVisible(false);
        } else {
            if (mode == MODE_EDIT) {
                button.setEnabled(false);
                button.setSelection(editedRule.usesDFA());
            } else {
                button.setEnabled(false);
                button.setSelection(false);
            }
        }

        return button;
    }

    /**
     * Build the description text
     */
    private Text buildDescriptionText(Composite parent) {
        Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);

        String description = null;
        if (editedRule != null) {
        	description = editedRule.getDescription();
        }
        if (description == null) {
            description = "";
        }
        text.setText(description.trim());

        if (mode == MODE_VIEW) {
            text.setEditable(false);
        }

        return text;
    }

    /**
     * Build the external info url text
     */
    private Text buildExternalInfoUrlText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

        String externalInfoUrl = null;
        if (editedRule != null) {
        	externalInfoUrl = editedRule.getExternalInfoUrl();
        }
        if (externalInfoUrl == null) {
        	externalInfoUrl = "";
        }
        text.setText(externalInfoUrl.trim());

        if (mode == MODE_VIEW) {
            text.setEditable(false);
        }

        return text;
    }

    /**
     * Build the open external info url button
     */
    private Button buildOpenExternalInfoUrlButton(Composite parent) {
        final Button button = new Button(parent, SWT.PUSH);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_RULEEDIT_BUTTON_OPEN_EXTERNAL_INFO_URL));

        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	String url = externalInfoUrlText.getText().trim();
            	if (url.length() > 0) {
            		try {
						IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
						browser.openURL(new URL(url));
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        });

        return button;
    }

    /**
     * Concatenate all the rule examples in one String.
     *
     * @return the concatenation of all example strings
     */
    private String getExamplesString() {
        StringBuffer buffer = new StringBuffer();
        Iterator i = this.editedRule.getExamples().iterator();
        boolean first = true;
        while (i.hasNext()) {
            if (first) {
                first = false;
            } else {
                buffer.append("\n\n");
            }
            buffer.append(((String)i.next()).trim());
        }
        return buffer.toString();
    }

    /**
     * Build the example text
     */
    private Text buildExampleText(Composite parent) {
        Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        text.setFont(courierFont);
        if (mode == MODE_EDIT) {
            text.setText(getExamplesString());
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
            text.setText(getExamplesString());
        }

        return text;
    }

    /**
     * Build the xpath text
     */
    private Text buildXPathText(Composite parent) {
        Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        text.setFont(courierFont);

        if (mode == MODE_ADD) {
            text.setEditable(true);
            text.setEnabled(true);
        }

        if (mode == MODE_EDIT) {
        	if (this.editedRule.hasProperty("xpath")) {
                text.setText(this.editedRule.getStringProperty("xpath").trim());
                text.setEditable(true);
        	}
        }

        if (mode == MODE_VIEW) {
            text.setEditable(false);
        	if (this.editedRule.hasProperty("xpath")) {
                text.setText(this.editedRule.getStringProperty("xpath").trim());
        	}
        }

        return text;
    }

    /**
     * Based on current settings of a RuleReference being edited, update the visual
     * indicators of whether an override of the underlying Rule is occurring
     * or not.
     */
    protected void refreshOverridden() {
    	if (mode == MODE_EDIT || mode == MODE_VIEW) {
    		if (editedRule instanceof RuleReference) {
    			RuleReference ruleReference = (RuleReference)editedRule;
    			Color lightBlue = new Color(null, 196, 196, 255);
    			nameText.setBackground(ruleReference.getOverriddenName() != null ? lightBlue: null);
    			messageText.setBackground(ruleReference.getOverriddenMessage() != null ? lightBlue: null);
    			priorityCombo.setBackground(ruleReference.getOverriddenPriority() != null ? lightBlue: null);
    			descriptionText.setBackground(ruleReference.getOverriddenDescription() != null ? lightBlue: null);
    			externalInfoUrlText.setBackground(ruleReference.getOverriddenExternalInfoUrl() != null ? lightBlue: null);
    			exampleText.setBackground(ruleReference.getOverriddenExamples() != null ? lightBlue: null);
    			xpathText.setBackground(ruleReference.getOverriddenProperties() != null && ruleReference.getOverriddenProperties().containsKey("xpath") ? lightBlue: null);
    		}
    	}
    }

    /**
     * Helper method to shorten message access
     *
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        if (validateForm() && this.mode != MODE_VIEW) {
            super.okPressed();
        } else if (this.mode == MODE_VIEW) {
            cancelPressed();
        }
    }

    /**
     * Perform the form validation
     */
    private boolean validateForm() {
        return validateName() && validatePriority() && validateMessage() && validateImplementationClass();
    }

    /**
     * Perform the name validation
     */
    private boolean validateName() {
        boolean flValid = true;

        String name = nameText.getText();
        if (name.trim().length() == 0) {
            MessageDialog.openWarning(getShell(), getMessage(StringKeys.MSGKEY_WARNING_TITLE),
                    getMessage(StringKeys.MSGKEY_WARNING_NAME_MANDATORY));
            nameText.setFocus();
            flValid = false;
        }

        return flValid;
    }

    /**
     * Perform the priority validation
     */
    private boolean validatePriority() {
        boolean flValid = true;

        if (priorityCombo.getSelectionIndex() < 0) {
            MessageDialog.openWarning(getShell(), getMessage(StringKeys.MSGKEY_WARNING_TITLE),
                    getMessage(StringKeys.MSGKEY_WARNING_PRIORITY_MANDATORY));
            priorityCombo.setFocus();
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
            MessageDialog.openWarning(getShell(), getMessage(StringKeys.MSGKEY_WARNING_TITLE),
                    getMessage(StringKeys.MSGKEY_WARNING_MESSAGE_MANDATORY));
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
                    rule.setRuleSetName("pmd-eclipse");
                    rule.setMessage(messageText.getText().trim());
                    rule.setDescription(descriptionText.getText());
                    rule.getExamples().add(exampleText.getText());
                    rule.setPriority(priorityCombo.getSelectionIndex()+1);
                    rule.setExternalInfoUrl(externalInfoUrlText.getText());
                    if (usesTypeResolutionButton.getSelection()) {
                    	rule.setUsesTypeResolution();
                    }
                    if (usesDfaButton.getSelection()) {
                    	rule.setUsesDFA();
                    }
                    if (rule instanceof XPathRule) {
                    	String xpath = xpathText.getText().trim();
                    	if (xpath.length() != 0) {
                            rule.addProperty("xpath", xpath);
                    	} else {
                            MessageDialog.openWarning(getShell(), getMessage(StringKeys.MSGKEY_WARNING_TITLE),
                                    getMessage(StringKeys.MSGKEY_WARNING_XPATH_MANDATORY));
                            xpathText.setFocus();
                            flValid = false;
                    	}
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
            editedRule.setMessage(messageText.getText().trim());
            editedRule.setPriority(priorityCombo.getSelectionIndex() + 1);
            editedRule.setDescription(descriptionText.getText());
            editedRule.setExternalInfoUrl(externalInfoUrlText.getText());
            editedRule.addExample(this.exampleText.getText());
            String xpath = xpathText.getText().trim();
            if (xpath.length() > 0) {
            	editedRule.addProperty("xpath", xpath);
            }
        }

        // Display class error if needed
        if (flClassError) {
            MessageDialog.openWarning(getShell(), getMessage(StringKeys.MSGKEY_WARNING_TITLE),
                    getMessage(StringKeys.MSGKEY_WARNING_CLASS_INVALID));
            implementationClassText.setFocus();
            flValid = false;
        }

        return flValid;
    }

    /**
     * Returns the rule.
     *
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
