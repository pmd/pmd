package net.sourceforge.pmd.eclipse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author ?
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.8  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 */
public class PMDPlugin extends AbstractUIPlugin {

    // Public constants
    public static final String[] RULESET_DEFAULTLIST =
        { "rulesets/basic.xml", "rulesets/design.xml", "rulesets/imports.xml", "rulesets/unusedcode.xml" };
    public static final String[] RULESET_ALLPMD =
        {
            "rulesets/basic.xml",
            "rulesets/braces.xml",
            "rulesets/codesize.xml",
            "rulesets/controversial.xml",
            "rulesets/coupling.xml",
            "rulesets/design.xml",
            "rulesets/imports.xml",
            "rulesets/javabeans.xml",
            "rulesets/junit.xml",
            "rulesets/naming.xml",
            "rulesets/strings.xml",
            "rulesets/unusedcode.xml" };

    public static final String RULESET_PREFERENCE = "net.sourceforge.pmd.eclipse.ruleset";
    public static final String RULESET_DEFAULT = "";

    public static String MIN_TILE_SIZE_PREFERENCE = "net.sourceforge.pmd.eclipse.CPDPreference.mintilesize";
    public static int MIN_TILE_SIZE_DEFAULT = 25;

    public static String PMD_MARKER = "net.sourceforge.pmd.eclipse.pmdMarker";
    public static String PMD_TASKMARKER = "net.sourceforge.pmd.eclipse.pmdTaskMarker";

    // Static attributes
    private static PMDPlugin plugin;
    private static Log log;

    // Private attributes
    private Properties messageTable;
    private RuleSet ruleSet;
    private String[] priorityLabels;

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

            if (log == null) {
                DOMConfigurator.configure(find(new Path("log4j.xml")));
                log = LogFactory.getLog("net.sourceforge.pmd.eclipse.PMDPlugin");
            }

            log.info("PMD plugin loaded");
        } catch (IOException e) {
            logError("Can't load message table", e);
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
        store.setDefault(RULESET_PREFERENCE, RULESET_DEFAULT);
        store.setDefault(MIN_TILE_SIZE_PREFERENCE, MIN_TILE_SIZE_DEFAULT);
        super.initializeDefaultPreferences(store);
    }

    /**
     * Get the configured rule set
     */
    public RuleSet getRuleSet() {
        if (ruleSet == null) {
            ruleSet = getRuleSetFromPreference();
        }

        return ruleSet;
    }

    /**
     * Set the rule set and store it in the preferences
     */
    public void setRuleSet(RuleSet newRuleSet) {
        ruleSet = newRuleSet;
        storeRuleSetInPreference();
    }

    /**
     * Get rule set from preference
     */
    private RuleSet getRuleSetFromPreference() {
        RuleSet preferedRuleSet = null;
        RuleSetFactory factory = new RuleSetFactory();
        String ruleSetPreference = getPreferenceStore().getString(RULESET_PREFERENCE);

        // creating a default rule set from a ruleset list
        if (ruleSetPreference.equals(RULESET_DEFAULT)) {
            preferedRuleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(RULESET_DEFAULTLIST[0]));
            for (int i = 1; i < RULESET_DEFAULTLIST.length; i++) {
                RuleSet tmpRuleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(RULESET_DEFAULTLIST[i]));
                preferedRuleSet.addRuleSet(tmpRuleSet);
            }

            preferedRuleSet.setName("pmd-eclipse");
            preferedRuleSet.setDescription("PMD Plugin generated rule set");
        }

        // creating a rule set object from the preference store
        else {
            try {
                InputStream ruleSetStream = new ByteArrayInputStream(ruleSetPreference.getBytes());
                preferedRuleSet = factory.createRuleSet(ruleSetStream);
                ruleSetStream.close();
            } catch (IOException e) {
                showError(getMessage(PMDConstants.MSGKEY_ERROR_READING_PREFERENCE), e);
            }
        }

        return preferedRuleSet;

    }

    /**
     * Store the rule set in preference store
     */
    private void storeRuleSetInPreference() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            RuleSetWriter writer = new RuleSetWriter(out);
            writer.write(ruleSet);
            out.flush();
            getPreferenceStore().setValue(PMDPlugin.RULESET_PREFERENCE, out.toString());
            out.close();
        } catch (IOException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_WRITING_PREFERENCE), e);
        }
    }

    /**
     * Return the priority labels
     */
    public String[] getPriorityLabels() {
        if (priorityLabels == null) {
            priorityLabels =
                new String[] {
                    getMessage(PMDConstants.MSGKEY_PRIORITY_ERROR_HIGH),
                    getMessage(PMDConstants.MSGKEY_PRIORITY_ERROR),
                    getMessage(PMDConstants.MSGKEY_PRIORITY_WARNING_HIGH),
                    getMessage(PMDConstants.MSGKEY_PRIORITY_WARNING),
                    getMessage(PMDConstants.MSGKEY_PRIORITY_INFORMATION)};
        }

        return priorityLabels;
    }

    /**
     * Helper method to log error
     * @see IStatus
     */
    public void logError(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, getDescriptor().getUniqueIdentifier(), 0, message + t.getMessage(), t));
        if (log != null) {
            log.error(message, t);
        }
    }

    /**
     * Helper method to display error
     */
    public void showError(final String message, final Throwable t) {
        logError(message, t);
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(
                    Display.getCurrent().getActiveShell(),
                    getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                    message + String.valueOf(t));
            }
        });
    }
}
