package net.sourceforge.pmd.eclipse;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.preference.*;
import org.eclipse.core.resources.*;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class PMDPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static PMDPlugin plugin;

	//Resource bundle.
	private ResourceBundle resourceBundle;

	//preference constants
	public static final String RULESETS_PREFERENCE = "net.sourceforge.pmd.eclipse.rulesets";
	public static final String DEFAULT_RULESETS = "rulesets/basic.xml;rulesets/design.xml;rulesets/imports.xml;rulesets/unusedcode.xml";
	public static String MIN_TILE_SIZE_PREFERENCE = "net.sourceforge.pmd.eclipse.CPDPreference.mintilesize";
	public static int DEFAULT_MIN_TILE_SIZE = 25;
	
	//marker constants
	public static String PMD_MARKER = "net.sourceforge.pmd.eclipse.PMDMarker";
	public static String CPD_MARKER = "net.sourceforge.pmd.eclipse.CPDMarker";
	
	private static final String PREFERENCE_DELIMITER = ";";
	
	/**
	 * The constructor.
	 */
	public PMDPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("net.sourceforge.pmd.eclipse.PMDPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static PMDPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= PMDPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPreferences(IPreferenceStore)
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(RULESETS_PREFERENCE, DEFAULT_RULESETS);
		super.initializeDefaultPreferences(store);
	}
		
	/**
	 * Convert the supplied PREFERENCE_DELIMITER delimited
	 * String to a String array.
	 * @return String[]
	 */
	private String[] convert(String preferenceValue) {
		StringTokenizer tokenizer =
			new StringTokenizer(preferenceValue, PREFERENCE_DELIMITER);
		int tokenCount = tokenizer.countTokens();
		String[] elements = new String[tokenCount];
		for (int i = 0; i < tokenCount; i++) {
			elements[i] = tokenizer.nextToken();
		}
	
		return elements;
	}
	
	/**
	 * Return the rulesets preference default
	 * as an array of Strings.
	 * @return String[]
	 */
	public String[] getDefaultRuleSetsPreference(){
		return convert(getPreferenceStore().getDefaultString(RULESETS_PREFERENCE));
	}
	
	/**
	 * Return the rulesets preference as an array of
	 * Strings.
	 * @return String[]
	 */
	public String[] getRuleSetsPreference() {
		return convert(getPreferenceStore().getString(RULESETS_PREFERENCE));
	}
		
	/**
	 * Set the bad words preference
	 * @param String [] elements - the Strings to be 
	 * 	converted to the preference value
	 */
	public void setRuleSetsPreference(String[] elements) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(PREFERENCE_DELIMITER);
		}
		getPreferenceStore().setValue(RULESETS_PREFERENCE, buffer.toString());
	}
}
