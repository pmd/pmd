package net.sourceforge.pmd.eclipse.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PMDPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	public static final String P_RULESETS = 
		"net.sourceforge.pmd.eclipse.rulesets";

	public PMDPreferencePage() {
		super(GRID);
		setPreferenceStore(PMDPlugin.getDefault().getPreferenceStore());
		setDescription("PMD Configuration Options");
		initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(P_RULESETS, "rulesets/basic.xml");		
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
		addField( new StringFieldEditor( P_RULESETS,
			     "&Ruleset", getFieldEditorParent() ));
	}
	
	public void init(IWorkbench workbench) {
	}
}