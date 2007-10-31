package net.sourceforge.pmd.ui.preferences;

import java.text.MessageFormat;
import java.util.Date;

import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.preferences.IPreferences;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.apache.log4j.Level;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

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
 * Revision 1.2  2007/01/06 11:58:08  holobender
 * disabled the experimental option since it can now be enabled over the DataflowAnomalyAnalysis rule
 *
 * Revision 1.1  2006/05/22 21:23:40  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
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
    private static final String[] LOG_LEVELS = { "OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "ALL" };

    private Text additionalCommentText;
    private Label sampleLabel;
    private Button showPerspectiveBox;
    private Button useDFABox;
    private Text maxViolationsPerFilePerRule;
    private Button reviewPmdStyleBox;
    private IPreferences preferences;
    private Text logFileNameText;
    private Scale logLevelScale;
    private Label logLevelValueLabel;
    private Button browseButton;

    /**
     * Initialize the page
     * 
     * @see PreferencePage#init
     */
    public void init(IWorkbench arg0) {
        setDescription(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TITLE));
        this.preferences = PMDRuntimePlugin.getDefault().loadPreferences();
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
        Group logGroup = buildLoggingGroup(composite);
        
        // Layout children
        generalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        logGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
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
        group.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_GROUP_GENERAL));
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
        group.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_GROUP_REVIEW));
        group.setLayout(new GridLayout(1, false));

        // build children
        this.reviewPmdStyleBox = buildReviewPmdStyleBoxButton(group);
        Label separator = new Label(group, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
        buildLabel(group, StringKeys.MSGKEY_PREF_GENERAL_LABEL_ADDCOMMENT);
        this.additionalCommentText = buildAdditionalCommentText(group);
        buildLabel(group, StringKeys.MSGKEY_PREF_GENERAL_LABEL_SAMPLE);
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
     * Build the log group.
     * Note that code is a cut & paste from the Eclipse Visual Editor 
     *
     */
    private Group buildLoggingGroup(Composite parent) {
        GridData gridData2 = new GridData();
        gridData2.horizontalSpan = 2;
        gridData2.horizontalAlignment = SWT.FILL;
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData11.horizontalSpan = 3;
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData3.horizontalSpan = 3;
        GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        
        Group loggingGroup = new Group(parent, SWT.NONE);
        loggingGroup.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_GROUP_LOGGING));
        loggingGroup.setLayout(gridLayout);
        
        Label logFileNameLabel = new Label(loggingGroup, SWT.NONE);
        logFileNameLabel.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_LOG_FILE_NAME));
        logFileNameLabel.setLayoutData(gridData);
        
        this.logFileNameText = new Text(loggingGroup, SWT.BORDER);
        this.logFileNameText.setText(this.preferences.getLogFileName());
        this.logFileNameText.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TOOLTIP_LOG_FILE_NAME));
        this.logFileNameText.setLayoutData(gridData1);
        
        this.browseButton = new Button(loggingGroup, SWT.NONE);
        this.browseButton.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_BUTTON_BROWSE));
        this.browseButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                browseLogFile();
            }
            public void widgetDefaultSelected(SelectionEvent event) {
                // do nothing                
            }
        });
        
        Label separator = new Label(loggingGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(gridData11);
        
        Label logLevelLabel = new Label(loggingGroup, SWT.NONE);
        logLevelLabel.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_LOG_LEVEL));
        
        this.logLevelValueLabel = new Label(loggingGroup, SWT.NONE);
        this.logLevelValueLabel.setText("");
        this.logLevelValueLabel.setLayoutData(gridData2);
        
        this.logLevelScale = new Scale(loggingGroup, SWT.NONE);
        this.logLevelScale.setMaximum(6);
        this.logLevelScale.setPageIncrement(1);
        this.logLevelScale.setLayoutData(gridData3);
        this.logLevelScale.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                updateLogLevelValueLabel();
            }
            public void widgetDefaultSelected(SelectionEvent event) {
                updateLogLevelValueLabel();                
            }
        });
        
        this.logLevelScale.setSelection(intLogLevel(this.preferences.getLogLevel()));
        updateLogLevelValueLabel();
        
        return loggingGroup;
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
        text.setText(this.preferences.getReviewAdditionalComment());
        text.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TOOLTIP_ADDCOMMENT));

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
        button.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_SHOW_PERSPECTIVE));
        button.setSelection(this.preferences.isPmdPerspectiveEnabled());
        
        return button;
    }
    
    /**
     * Build the check box for enabling DFA
     * @param viewGroup the parent composite
     *
     */
    private Button buildUseDfaBoxButton(final Composite viewGroup) {
        Button button = new Button(viewGroup, SWT.CHECK);
        button.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_LABEL_USE_DFA));
        // this check box has no function since dfa can now be enabled over the DataflowAnomalyAnalysis - Rule.
        //button.setSelection(this.preferences.isDfaEnabled());
        button.setSelection(true);
        button.setEnabled(false);
        return button;
    }

    /**
     * Build the text for maximum violations per file per rule
     * 
     * @param parent
     * @return
     */
    private Text buildMaxViolationsPerFilePerRuleText(Composite parent) {
        buildLabel(parent, StringKeys.MSGKEY_PREF_GENERAL_LABEL_MAX_VIOLATIONS_PFPR);
        final Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        text.setText(String.valueOf(this.preferences.getMaxViolationsPerFilePerRule()));
        text.setToolTipText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_TOOLTIP_MAX_VIOLATIONS_PFPR));

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
        button.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_REVIEW_PMD_STYLE));
        button.setSelection(this.preferences.isReviewPmdStyleEnabled());
        
        return button;
    }

    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        if (this.additionalCommentText != null) {
            this.additionalCommentText.setText(IPreferences.REVIEW_ADDITIONAL_COMMENT_DEFAULT);
        }

        if (this.showPerspectiveBox != null) {
            this.showPerspectiveBox.setSelection(IPreferences.PMD_PERSPECTIVE_ENABLED_DEFAULT);
        }

        if (this.useDFABox != null) {
            this.useDFABox.setSelection(IPreferences.DFA_ENABLED_DEFAULT);
        }
        
        if (this.maxViolationsPerFilePerRule != null) {
            this.maxViolationsPerFilePerRule.setText(String.valueOf(IPreferences.MAX_VIOLATIONS_PFPR_DEFAULT));
        }
        
        if (this.reviewPmdStyleBox !=null) {
            this.reviewPmdStyleBox.setSelection(IPreferences.REVIEW_PMD_STYLE_ENABLED_DEFAULT);
        }
        
        if (this.logFileNameText != null) {
            this.logFileNameText.setText(IPreferences.LOG_FILENAME_DEFAULT);
        }
        
        if (this.logLevelScale != null) {
            this.logLevelScale.setSelection(intLogLevel(IPreferences.LOG_LEVEL));
            updateLogLevelValueLabel();
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
            setMessage(getMessage(StringKeys.MSGKEY_PREF_GENERAL_HEADER), NONE);
            setValid(true);

        } catch (IllegalArgumentException e) {
            setMessage(getMessage(StringKeys.MSGKEY_PREF_GENERAL_MESSAGE_INCORRECT_FORMAT), ERROR);
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
            setMessage(getMessage(StringKeys.MSGKEY_PREF_GENERAL_HEADER), NONE);
            setValid(true);
        } catch (NumberFormatException e) {
            setMessage(getMessage(StringKeys.MSGKEY_PREF_GENERAL_MESSAGE_INVALID_NUMERIC_VALUE), ERROR);
            setValid(false);
        }
    }
    
    /**
     * Update the label of the log level to reflect the log level selected
     *
     */
    protected void updateLogLevelValueLabel() {
        this.logLevelValueLabel.setText(LOG_LEVELS[this.logLevelScale.getSelection()]);
    }
    
    /**
     * Display a file selection dialog in order to let the user select a log file
     *
     */
    protected void browseLogFile() {
        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
        dialog.setText(getMessage(StringKeys.MSGKEY_PREF_GENERAL_DIALOG_BROWSE));
        String fileName = dialog.open();
        if (fileName != null) {
            this.logFileNameText.setText(fileName);
        }
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        if (this.additionalCommentText != null) {
            this.preferences.setReviewAdditionalComment(this.additionalCommentText.getText());
        }

        if (this.showPerspectiveBox != null) {
            this.preferences.setPmdPerspectiveEnabled(this.showPerspectiveBox.getSelection());
        }

        if (this.useDFABox != null) {
            this.preferences.setDfaEnabled(this.useDFABox.getSelection());
        }
        
        if (this.maxViolationsPerFilePerRule != null) {
            this.preferences.setMaxViolationsPerFilePerRule(Integer.valueOf(this.maxViolationsPerFilePerRule.getText()).intValue());
        }

        if (this.reviewPmdStyleBox != null) {
            this.preferences.setReviewPmdStyleEnabled(this.reviewPmdStyleBox.getSelection());
        }
        
        if (this.logFileNameText != null) {
            this.preferences.setLogFileName(this.logFileNameText.getText());
        }
        
        if (this.logLevelScale != null) {
            this.preferences.setLogLevel(Level.toLevel(LOG_LEVELS[this.logLevelScale.getSelection()]));
        }
        
        this.preferences.sync();
        PMDRuntimePlugin.getDefault().applyLogPreferences(this.preferences);

        return true;
    }
    
    /**
     * Return the selection index corresponding to the log level
     */
    private int intLogLevel(Level level) {
        int result = 0;
        
        if (level.equals(Level.OFF)) {
            result = 0;
        } else if (level.equals(Level.FATAL)) {
            result = 1;
        } else if (level.equals(Level.ERROR)) {
            result = 2;
        } else if (level.equals(Level.WARN)) {
            result = 3;
        } else if (level.equals(Level.INFO)) {
            result = 4;
        } else if (level.equals(Level.DEBUG)) {
            result = 5;
        } else if (level.equals(Level.ALL)) {
            result = 6;
        }
        
        return result;
        
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
