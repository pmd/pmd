package net.sourceforge.pmd.eclipse.preferences;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for CPD properties
 * 
 * @author ?
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.6  2003/03/17 23:38:04  phherlin
 * minor cleaning
 *
 */
public class CPDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private Text minTileText;
    private Label minTileLabel;

    /**
     * Insert the method's description here.
     * @see PreferencePage#init
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
        setDescription(getMessage(PMDConstants.MSGKEY_PREF_CPD_TITLE));
    }

    /**
     * Insert the method's description here.
     * @see PreferencePage#createContents
     */
    protected Control createContents(Composite parent) {
        Composite entryTable = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        entryTable.setLayout(layout);

        minTileLabel = new Label(entryTable, SWT.NULL);
        minTileLabel.setText(getMessage(PMDConstants.MSGKEY_PREF_CPD_TILESIZE));

        minTileText = new Text(entryTable, SWT.BORDER);
        minTileText.setText(getPreferenceStore().getString(PMDPlugin.MIN_TILE_SIZE_PREFERENCE));
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        minTileText.setLayoutData(data);

        return entryTable;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        getPreferenceStore().setValue(PMDPlugin.MIN_TILE_SIZE_PREFERENCE, PMDPlugin.DEFAULT_MIN_TILE_SIZE);
        minTileText.setText(getPreferenceStore().getString(PMDPlugin.MIN_TILE_SIZE_PREFERENCE));
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        getPreferenceStore().setValue(PMDPlugin.MIN_TILE_SIZE_PREFERENCE, Integer.parseInt(minTileText.getText()));
        return super.performOk();
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
