package net.sourceforge.pmd.eclipse.preferences;

import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
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
 * Revision 1.7  2003/03/18 23:28:36  phherlin
 * *** keyword substitution change ***
 *
 */
public class GeneralPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#init
	 */
	public void init(IWorkbench arg0)  {
		setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
		setDescription(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PREF_GENERAL_TITLE));
	}

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite parent)  {
        noDefaultAndApplyButton();
        Composite composite = new Composite(parent, SWT.NONE);
        
		return composite;
	}
}
