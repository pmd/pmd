package net.sourceforge.pmd.eclipse.preferences;

import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * 
 * @author ?
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.6  2003/03/17 23:38:30  phherlin
 * minor cleaning
 *
 */

public class PMDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private List rulesetsList;
    private Combo newEntryCombo;
    private Label title;

    public void init(IWorkbench workbench) {
        setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
        setDescription(getMessage(PMDConstants.MSGKEY_PREF_RULESET_TITLE));
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        rulesetsList.setItems(PMDPlugin.getDefault().getDefaultRuleSetsPreference());
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        PMDPlugin.getDefault().setRuleSetsPreference(rulesetsList.getItems());
        return super.performOk();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {

        Composite entryTable = new Composite(parent, SWT.NULL);

        //Create a data that takes up the extra space in the dialog .
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        entryTable.setLayoutData(data);

        GridLayout layout = new GridLayout();
        entryTable.setLayout(layout);

        //Add in a dummy label for spacing
        new Label(entryTable, SWT.NONE);

        title = new Label(entryTable, SWT.NONE);
        title.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_LIST));
        data = new GridData(GridData.FILL_HORIZONTAL);
        title.setLayoutData(data);

        rulesetsList = new List(entryTable, SWT.BORDER);
        rulesetsList.setItems(PMDPlugin.getDefault().getRuleSetsPreference());
        //Create a data that takes up the extra space in the dialog and spans both columns.
        data = new GridData(GridData.FILL_BOTH);
        rulesetsList.setLayoutData(data);

        Composite buttonComposite = new Composite(entryTable, SWT.NULL);

        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 2;
        buttonComposite.setLayout(buttonLayout);

        //Create a data that takes up the extra space in the dialog and spans both columns.
        data = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
        buttonComposite.setLayoutData(data);

        Button addButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

        addButton.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                rulesetsList.add(newEntryCombo.getText(), rulesetsList.getItemCount());
            }
        });

        //create the combox box to add new rulesets
        newEntryCombo = new Combo(buttonComposite, SWT.BORDER);
        //Create a data that takes up the extra space in the dialog .
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        newEntryCombo.setLayoutData(data);
        //populate the combo list with the items ruleset properties file
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream("rulesets/rulesets.properties"));
            String rulesetFilenames = props.getProperty("rulesets.filenames");
            for (StringTokenizer st = new StringTokenizer(rulesetFilenames, ","); st.hasMoreTokens();) {
                newEntryCombo.add(st.nextToken());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button removeButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

        removeButton.setText(getMessage(PMDConstants.MSGKEY_PREF_RULESET_REMOVE));
        removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                rulesetsList.remove(rulesetsList.getSelectionIndex());
            }
        });

        data = new GridData();
        data.horizontalSpan = 2;
        removeButton.setLayoutData(data);

        return entryTable;

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