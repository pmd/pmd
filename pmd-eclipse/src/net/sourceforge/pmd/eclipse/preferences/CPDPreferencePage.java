package net.sourceforge.pmd.eclipse.preferences;

import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Insert the type's description here.
 * @see PreferencePage
 */
public class CPDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	Text minTileText;
	Label minTileLabel;

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#init
	 */
	public void init(IWorkbench workbench)  {
		setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
		setDescription("CPD Configuration Options");

	}

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite parent)  {
		Composite entryTable = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		entryTable.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		entryTable.setLayoutData(data);
		
		minTileLabel = new Label(entryTable, SWT.NULL);
		minTileLabel.setText("Minimum Tile Size");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		minTileLabel.setLayoutData(data);

		minTileText = new Text(entryTable, SWT.NULL);
		minTileText.setText(getPreferenceStore().getString(PMDPlugin.MIN_TILE_SIZE_PREFERENCE));
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		minTileText.setLayoutData(data);
		
		return entryTable;
	}
	
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		getPreferenceStore().setValue(PMDPlugin.MIN_TILE_SIZE_PREFERENCE, PMDPlugin.DEFAULT_MIN_TILE_SIZE);
	}
	
	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		try {
			getPreferenceStore().setValue(PMDPlugin.MIN_TILE_SIZE_PREFERENCE, Integer.parseInt(minTileText.getText()));
		} 
		catch (Exception e){}
		return super.performOk();
	}
	
}
