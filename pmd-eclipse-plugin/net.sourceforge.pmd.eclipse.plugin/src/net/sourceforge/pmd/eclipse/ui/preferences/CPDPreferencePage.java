package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.cpd.GUI;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.preferences.br.AbstractPMDPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * Preference page for CPD properties
 *
 * @author ?
 * @author Philippe Herlin, Brian Remedios
 *
 */
public class CPDPreferencePage extends AbstractPMDPreferencePage {
	
    private Spinner	 	minTileSizeSpinner;
    private Label 		minTileLabel;

    protected String descriptionId() {
    	return StringKeys.PREF_CPD_TITLE;
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

        buildCPDLauncherButton(composite);
        
        return composite;
    }

	public void createControl(Composite parent) {
		super.createControl(parent);
		
		setModified(false);		
	}
	
	/**
	 * Build the CPD launcher button
	 * @param parent Composite
	 * @return Button
	 */
	private Button buildCPDLauncherButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH | SWT.LEFT);
		button.setText("Launch CPD...");

		button.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent event) {
				new Thread(new Runnable() {
					public void run() {
						GUI.main(new String[] { "-noexitonclose" });
					}
				}).start();
			}
		});

		return button;
	}
    
    
    /**
     * Build the group of general preferences
     * @param parent the parent composite
     * @return the group widget
     */
    private Group buildGeneralGroup(final Composite parent) {

        // build the group
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(getMessage(StringKeys.PREF_CPD_GROUP_GENERAL));
        group.setLayout(new GridLayout(2, false));

        // build the children
        minTileLabel = new Label(group, SWT.NULL);
        minTileLabel.setText(getMessage(StringKeys.PREF_CPD_TILESIZE));

        minTileSizeSpinner = new Spinner(group, SWT.BORDER);
        minTileSizeSpinner.setMinimum(preferences.getMinTileSize());

        // layout children
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        minTileSizeSpinner.setLayoutData(data);

        return group;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        minTileSizeSpinner.setMinimum(IPreferences.MIN_TILE_SIZE_DEFAULT);
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        preferences.setMinTileSize(Integer.valueOf(minTileSizeSpinner.getText()).intValue());
       
        return super.performOk();
    }

}
