package net.sourceforge.pmd.eclipse.preferences;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;

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
 * Revision 1.8  2003/08/14 16:10:41  phherlin
 * Implementing Review feature (RFE#787086)
 *
 * Revision 1.7  2003/03/18 23:28:36  phherlin
 * *** keyword substitution change ***
 *
 */
public class GeneralPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
    private Text additionalCommentText;
    private Label sampleLabel;

	/**
	 * Initialize the page
	 * @see PreferencePage#init
	 */
	public void init(IWorkbench arg0)  {
		setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
		setDescription(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_TITLE));
	}

	/**
	 * Create and initialize the controls of the page
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite parent)  {
        Composite composite = new Composite(parent, SWT.NONE);        
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);

        Group group = new Group(composite, SWT.SHADOW_IN);
        group.setText(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_GROUP_REVIEW));
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        group.setLayoutData(data);
                
        buildLabel(group, PMDConstants.MSGKEY_PREF_GENERAL_LABEL_ADDCOMMENT);
        additionalCommentText = buildAdditionalCommentText(group);
        Label separator = new Label(group, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
        buildLabel(group, PMDConstants.MSGKEY_PREF_GENERAL_LABEL_SAMPLE);
        sampleLabel = buildSampleLabel(group);
        updateSampleLabel();
        
        layout = new GridLayout(1, false);
        group.setLayout(layout);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        additionalCommentText.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        separator.setLayoutData(data);

        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        sampleLabel.setLayoutData(data);
        
		return composite;
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
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        if (additionalCommentText != null) {
            additionalCommentText.setText(PMDPlugin.REVIEW_ADDITIONAL_COMMENT_DEFAULT);
        }
    }
    
    /**
     * Update the sample label when the additional comment text is modified
     */
    protected void updateSampleLabel() {
        String pattern = additionalCommentText.getText();
        try {
            String commentText = MessageFormat.format(pattern, new Object[] {
                System.getProperty("user.name", ""),
                new Date()
            });
            
            sampleLabel.setText(commentText);
            setValid(true);
            setMessage("", NONE);
            
        } catch (IllegalArgumentException e) {
            setMessage(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_MESSAGE_INCORRECT_FORMAT), ERROR);
            setValid(false);
        }        
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        if (additionalCommentText != null) {
            PMDPlugin.getDefault().setReviewAdditionalComment(additionalCommentText.getText());
        }
        
        return true;
    }

}
