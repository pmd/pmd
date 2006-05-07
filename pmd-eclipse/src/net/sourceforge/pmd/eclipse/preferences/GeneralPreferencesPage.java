package net.sourceforge.pmd.eclipse.preferences;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;

/**
 * Dummy page for PMD preference category
 * 
 * @see CPDPreferencePage
 * @see PMDPreferencePage
 * 
 * @author ?
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.11  2006/05/07 12:03:08  phherlin
 * Add the possibility to use the PMD violation review style
 *
 * Revision 1.10  2006/05/02 20:11:13  phherlin
 * Limit the number of reported violations per file and per rule
 *
 * Revision 1.9  2005/10/24 22:43:22  phherlin
 * Integrating Sebastian Raffel's work
 * Revision 1.8 2003/08/14 16:10:41
 * phherlin Implementing Review feature (RFE#787086)
 * 
 * Revision 1.7 2003/03/18 23:28:36 phherlin *** keyword substitution change ***
 * 
 */
public class GeneralPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
    private Text additionalCommentText;
    private Label sampleLabel;
    private Button showPerspectiveBox;
    private Button useDFABox;
    private Text maxViolationsPerFilePerRule;
    private Button reviewPmdStyleBox;

    /**
     * Initialize the page
     * 
     * @see PreferencePage#init
     */
    public void init(IWorkbench arg0) {
        setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
        setDescription(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_TITLE));
    }

    /**
     * Create and initialize the controls of the page
     * 
     * @see PreferencePage#createContents
     */
    protected Control createContents(Composite parent) {

        // Create parent composite
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 20;
        composite.setLayout(layout);

        // Create chidls
        Group generalGroup = buildGeneralGroup(composite);
        Group reviewGroup = buildReviewGroup(composite);
        
        // Layout children
        generalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        reviewGroup.setLayoutData(data);

        return composite;
    }

    /**
     * Build the group of general preferences
     * @param parent the parent composte
     * @return the group widget
     */
    private Group buildGeneralGroup(final Composite parent) {
        
        // build the group
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_GROUP_GENERAL));
        group.setLayout(new GridLayout(1, false));

        // build the children
        this.showPerspectiveBox = buildShowPerspectiveBoxButton(group);
        this.useDFABox = buildUseDfaBoxButton(group);
        Label separator = new Label(group, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
        this.maxViolationsPerFilePerRule = buildMaxViolationsPerFilePerRuleText(group);
        
        // layout children
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        this.showPerspectiveBox.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        this.useDFABox.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        separator.setLayoutData(data);
        
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        this.maxViolationsPerFilePerRule.setLayoutData(data);
        
        return group;
    }
    
    /**
     * Build the group of review preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildReviewGroup(final Composite parent) {
        
        // build the group
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_GROUP_REVIEW));
        group.setLayout(new GridLayout(1, false));

        // build children
        this.reviewPmdStyleBox = buildReviewPmdStyleBoxButton(group);
        Label separator = new Label(group, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
        buildLabel(group, PMDConstants.MSGKEY_PREF_GENERAL_LABEL_ADDCOMMENT);
        this.additionalCommentText = buildAdditionalCommentText(group);
        buildLabel(group, PMDConstants.MSGKEY_PREF_GENERAL_LABEL_SAMPLE);
        this.sampleLabel = buildSampleLabel(group);
        updateSampleLabel();
        
        // layout children
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        this.reviewPmdStyleBox.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        separator.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        this.additionalCommentText.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        sampleLabel.setLayoutData(data);
        
        return group;
    }
    
    /**
     * Build a label
     */
    private Label buildLabel(Composite parent, String msgKey) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(msgKey == null ? "" : PMDPlugin.getDefault().getMessage(msgKey));
        return label;
    }

    /**
     * Build the sample
     */
    private Label buildSampleLabel(Composite parent) {
        Label label = new Label(parent, SWT.WRAP);
        return label;
    }

    /**
     * Build the text for additional comment input
     * 
     * @param parent
     * @return
     */
    private Text buildAdditionalCommentText(Composite parent) {
        Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        text.setText(PMDPlugin.getDefault().getReviewAdditionalComment());
        text.setToolTipText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_TOOLTIP_ADDCOMMENT));

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateSampleLabel();
            }
        });

        return text;
    }

    /**
     * Build the check box for showing the PMD perspective
     * @param viewGroup the parent composite
     *
     */
    private Button buildShowPerspectiveBoxButton(final Composite viewGroup) {
        Button button = new Button(viewGroup, SWT.CHECK);
        button.setText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_LABEL_SHOW_PERSPECTIVE));

        int showValue = PMDPlugin.getDefault().getPreferenceStore().getInt(PMDPlugin.SHOW_PERSPECTIVE_ON_CHECK_PREFERENCE);
        if (showValue == IPreferenceStore.INT_DEFAULT_DEFAULT) {
            button.setSelection(PMDPlugin.SHOW_PERSPECTIVE_ON_CHECK_DEFAULT == 1 ? true : false);
        } else {
            button.setSelection(showValue == 1 ? true : false);
        }
        
        return button;
    }
    
    /**
     * Build the check box for enabling DFA
     * @param viewGroup the parent composite
     *
     */
    private Button buildUseDfaBoxButton(final Composite viewGroup) {
        Button button = new Button(viewGroup, SWT.CHECK);
        button.setText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_LABEL_USE_DFA));

        int useDfaValue = PMDPlugin.getDefault().getPreferenceStore().getInt(PMDPlugin.USE_DFA_PREFERENCE);
        if (useDfaValue == IPreferenceStore.INT_DEFAULT_DEFAULT) {
            button.setSelection(PMDPlugin.USE_DFA_DEFAULT == 1 ? true : false);
        } else {
            button.setSelection(useDfaValue == 1 ? true : false);
        }
        
        return button;
    }

    /**
     * Build the text for maximum violations per file per rule
     * 
     * @param parent
     * @return
     */
    private Text buildMaxViolationsPerFilePerRuleText(Composite parent) {
        buildLabel(parent, PMDConstants.MSGKEY_PREF_GENERAL_LABEL_MAX_VIOLATIONS_PFPR);
        final Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        text.setText(String.valueOf(PMDPlugin.getDefault().getMaxViolationsPerFilePerRule()));
        text.setToolTipText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_TOOLTIP_MAX_VIOLATIONS_PFPR));

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateTextIsNumeric(text);
            }
        });

        return text;
    }
    
    /**
     * Build the check box for enabling PMD review style
     * @param viewGroup the parent composite
     *
     */
    private Button buildReviewPmdStyleBoxButton(final Composite parent) {
        Button button = new Button(parent, SWT.CHECK);
        button.setText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_REVIEW_PMD_STYLE));
        button.setSelection(PMDPlugin.getDefault().isReviewPmdStyle());
        
        return button;
    }

    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        if (this.additionalCommentText != null) {
            this.additionalCommentText.setText(PMDPlugin.REVIEW_ADDITIONAL_COMMENT_DEFAULT);
        }

        if (this.showPerspectiveBox != null) {
            this.showPerspectiveBox.setSelection(PMDPlugin.SHOW_PERSPECTIVE_ON_CHECK_DEFAULT == 1 ? true : false);
        }

        if (this.useDFABox != null) {
            this.useDFABox.setSelection(PMDPlugin.USE_DFA_DEFAULT == 1 ? true : false);
        }
        
        if (this.maxViolationsPerFilePerRule != null) {
            this.maxViolationsPerFilePerRule.setText(String.valueOf(PMDPluginConstants.MAX_VIOLATIONS_PER_FILE_PER_RULE_DEFAULT));
        }
    }

    /**
     * Update the sample label when the additional comment text is modified
     */
    protected void updateSampleLabel() {
        String pattern = additionalCommentText.getText();
        try {
            String commentText = MessageFormat.format(pattern, new Object[] { System.getProperty("user.name", ""), new Date() });

            sampleLabel.setText(commentText);
            setMessage(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_HEADER), NONE);
            setValid(true);

        } catch (IllegalArgumentException e) {
            setMessage(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_MESSAGE_INCORRECT_FORMAT), ERROR);
            setValid(false);
        }
    }
    
    /**
     * Check if the entry filed is a numeric value
     *
     */
    protected void validateTextIsNumeric(Text text) {
        try {
            Integer value = Integer.valueOf(text.getText());
            setMessage(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_HEADER), NONE);
            setValid(true);
        } catch (NumberFormatException e) {
            setMessage(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_MESSAGE_INVALID_NUMERIC_VALUE), ERROR);
            setValid(false);
        }
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        if (this.additionalCommentText != null) {
            PMDPlugin.getDefault().setReviewAdditionalComment(additionalCommentText.getText());
        }

        if (this.showPerspectiveBox != null) {
            PMDPlugin.getDefault().getPreferenceStore().setValue(PMDPluginConstants.SHOW_PERSPECTIVE_ON_CHECK_PREFERENCE,
                    this.showPerspectiveBox.getSelection() ? 1 : -1);
        }

        if (this.useDFABox != null) {
            PMDPlugin.getDefault().getPreferenceStore().setValue(PMDPluginConstants.USE_DFA_PREFERENCE,
                    this.useDFABox.getSelection() ? 1 : -1);
        }
        
        if (this.maxViolationsPerFilePerRule != null) {
            PMDPlugin.getDefault().setMaxViolationsPerFilePerRule(Integer.valueOf(this.maxViolationsPerFilePerRule.getText()).intValue());
        }

        if (this.reviewPmdStyleBox != null) {
            PMDPlugin.getDefault().setReviewPmdStyle(this.reviewPmdStyleBox.getSelection());
        }

        return true;
    }

}
