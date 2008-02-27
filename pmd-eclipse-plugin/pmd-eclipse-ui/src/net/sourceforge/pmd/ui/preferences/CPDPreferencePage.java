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
import org.eclipse.swt.widgets.Group;
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

        // Create parent composite
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        composite.setLayout(layout);

        // Create children
        Group generalGroup = buildGeneralGroup(composite);
        
        // Layout children
        generalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return composite;
    }

    /**
     * Build the group of general preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildGeneralGroup(final Composite parent) {
        
        // build the group
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(getMessage(StringKeys.MSGKEY_PREF_CPD_GROUP_GENERAL));
        group.setLayout(new GridLayout(2, false));

        // build the children
        minTileLabel = new Label(group, SWT.NULL);
        minTileLabel.setText(getMessage(StringKeys.MSGKEY_PREF_CPD_TILESIZE));

        minTileText = new Text(group, SWT.BORDER);
        minTileText.setText(String.valueOf(this.preferences.getMinTileSize()));

        // layout children
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        minTileText.setLayoutData(data);

        return group;
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
