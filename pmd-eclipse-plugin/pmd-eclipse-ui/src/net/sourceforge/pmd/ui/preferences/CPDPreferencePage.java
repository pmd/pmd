package net.sourceforge.pmd.ui.preferences;

import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.preferences.IPreferences;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

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
 * Revision 1.1  2006/05/22 21:23:39  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.8  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 */
public class CPDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private Text minTileText;
    private Label minTileLabel;
    private IPreferences preferences;

    /**
     * Insert the method's description here.
     * @see PreferencePage#init
     */
    public void init(IWorkbench workbench) {
        setDescription(getMessage(StringKeys.MSGKEY_PREF_CPD_TITLE));
        this.preferences = PMDRuntimePlugin.getDefault().loadPreferences();
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
        minTileLabel.setText(getMessage(StringKeys.MSGKEY_PREF_CPD_TILESIZE));

        minTileText = new Text(entryTable, SWT.BORDER);
        minTileText.setText(String.valueOf(this.preferences.getMinTileSize()));
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        minTileText.setLayoutData(data);

        return entryTable;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        this.minTileText.setText(String.valueOf(IPreferences.MIN_TILE_SIZE_DEFAULT));
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        this.preferences.setMinTileSize(new Integer(this.minTileText.getText()).intValue());
        this.preferences.sync();
        
        return super.performOk();
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
