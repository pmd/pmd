package net.sourceforge.pmd.eclipse;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author ?
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log :$
 */
public class PMDPlugin extends AbstractUIPlugin {
    
    // Public constants
    public static final String RULESETS_PREFERENCE = "net.sourceforge.pmd.eclipse.rulesets";
    public static final String DEFAULT_RULESETS =
        "rulesets/basic.xml;rulesets/design.xml;rulesets/imports.xml;rulesets/unusedcode.xml";
    public static String MIN_TILE_SIZE_PREFERENCE = "net.sourceforge.pmd.eclipse.CPDPreference.mintilesize";
    public static int DEFAULT_MIN_TILE_SIZE = 25;
    public static String PMD_MARKER = "net.sourceforge.pmd.eclipse.pmdMarker";
    public static String PMD_TASKMARKER = "net.sourceforge.pmd.eclipse.pmdTaskMarker";
    
    // Private constants
    private static final String PREFERENCE_DELIMITER = ";";
    
    // The shared instance.
    private static PMDPlugin plugin;

    // Externalized messages
    private Properties messageTable;

    /**
     * The constructor.
     */
    public PMDPlugin(IPluginDescriptor descriptor) {
        super(descriptor);
        plugin = this;
        try {
            URL messageTableUrl = find(new Path("$nl$/messages.properties"));
            if (messageTableUrl != null) {
                messageTable = new Properties();
                messageTable.load(messageTableUrl.openStream());
            }
        } catch (IOException e) {
            logError("Cant' load message table", e);
        }
    }

    /**
     * Returns the shared instance.
     */
    public static PMDPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the string from the message table
     * @param key the message key
     * @param defaultMessage the returned message if key is not found
     * @return the requested message
     */
    public String getMessage(String key, String defaultMessage) {
        String result = defaultMessage;
        
        if (messageTable != null) {
            result = messageTable.getProperty(key);
        }
        
        return result;
    }

    /**
     * Returns the string from the message table
     * @param key the message key
     * @return the requested message
     */
    public String getMessage(String key) {
        return getMessage(key, null);
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPreferences(IPreferenceStore)
     */
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        store.setDefault(RULESETS_PREFERENCE, DEFAULT_RULESETS);
        store.setDefault(MIN_TILE_SIZE_PREFERENCE, DEFAULT_MIN_TILE_SIZE);
        super.initializeDefaultPreferences(store);
    }

    /**
     * Convert the supplied PREFERENCE_DELIMITER delimited
     * String to a String array.
     * @return String[]
     */
    private String[] convert(String preferenceValue) {
        StringTokenizer tokenizer = new StringTokenizer(preferenceValue, PREFERENCE_DELIMITER);
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
    public String[] getDefaultRuleSetsPreference() {
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
    
    /**
     * Helper method to log error
     * @see IStatus
     */
    public void logError(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, getDescriptor().getUniqueIdentifier(), 0, message, t));
    }
}
