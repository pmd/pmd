package net.sourceforge.pmd.eclipse.preferences;

import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;
import net.sourceforge.pmd.eclipse.PMDPlugin;

/**
 * Insert the type's description here.
 * @see PreferencePage
 */
public class GeneralPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#init
	 */
	public void init(IWorkbench arg0)  {
		setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
		setDescription("PMD General Preferences");

	}

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite arg0)  {
		return null;
	}
}
