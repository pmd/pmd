package net.sourceforge.pmd.eclipse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
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
 * Revision 1.14  2003/09/29 22:38:09  phherlin
 * Adding and implementing "JDK13 compatibility" property.
 *
 * Revision 1.13  2003/08/14 16:10:41  phherlin
 * Implementing Review feature (RFE#787086)
 *
 * Revision 1.12  2003/08/11 21:57:28  phherlin
 * Refactoring ruleset preference store : moving to state location
 *
 * Revision 1.11  2003/07/30 19:29:02  phherlin
 * Updating to PMD v1.2
 *
 * Revision 1.10  2003/07/07 19:23:59  phherlin
 * Adding PMD violations view
 *
 * Revision 1.9  2003/07/01 20:22:16  phherlin
 * Make rules selectable from projects
 *
 * Revision 1.8  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 */
public class PMDPlugin extends AbstractUIPlugin {

    // Public constants
    public static final String PLUGIN_ID = "net.sourceforge.pmd.eclipse";
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
            "rulesets/strictexception.xml",
            "rulesets/strings.xml",
            "rulesets/unusedcode.xml" };

    public static final String RULESET_PREFERENCE = PLUGIN_ID + ".ruleset";
    public static final String RULESET_DEFAULT = "";
    public static final String RULESET_FILE = "/ruleset.xml";

    public static final String MIN_TILE_SIZE_PREFERENCE = PLUGIN_ID + ".CPDPreference.mintilesize";
    public static final int MIN_TILE_SIZE_DEFAULT = 25;

    public static final String PMD_MARKER = PLUGIN_ID + ".pmdMarker";
    public static final String PMD_TASKMARKER = PLUGIN_ID + ".pmdTaskMarker";

    public static final QualifiedName SESSION_PROPERTY_ACTIVE_RULESET =
        new QualifiedName(PLUGIN_ID + ".sessprops", "active_rulset");
    public static final QualifiedName PERSISTENT_PROPERTY_ACTIVE_RULESET =
        new QualifiedName(PLUGIN_ID + ".persprops", "active_rulset");

    public static final QualifiedName SESSION_PROPERTY_JDK13 =
        new QualifiedName(PLUGIN_ID + ".sessprops", "jdk13");
    public static final QualifiedName PERSISTENT_PROPERTY_JDK13 =
        new QualifiedName(PLUGIN_ID + ".persprops", "jdk13");

    public static final String LIST_DELIMITER = ";";

    public static final String ICON_ERROR = "icons/error.gif";
    public static final String ICON_WARN = "icons/warn.gif";
    public static final String ICON_INFO = "icons/info.gif";
    public static final String ICON_PROJECT = "icons/prj.gif";
    public static final String ICON_FILE = "icons/file.gif";
    public static final String ICON_PRIO1 = "icons/prio_1.gif";
    public static final String ICON_PRIO2 = "icons/prio_2.gif";
    public static final String ICON_PRIO3 = "icons/prio_3.gif";
    public static final String ICON_PRIO4 = "icons/prio_4.gif";
    public static final String ICON_PRIO5 = "icons/prio_5.gif";
    public static final String ICON_REMVIO = "icons/remvio.gif";

    public static final String KEY_MARKERATT_RULENAME = "rulename";

    public static final String SETTINGS_VIEW_FILE_SELECTION = "view.file_selection";
    public static final String SETTINGS_VIEW_PROJECT_SELECTION = "view.project_selection";
    public static final String SETTINGS_VIEW_ERRORHIGH_FILTER = "view.errorhigh_filter";
    public static final String SETTINGS_VIEW_ERROR_FILTER = "view.high_filter";
    public static final String SETTINGS_VIEW_WARNINGHIGH_FILTER = "view.warninghigh_filter";
    public static final String SETTINGS_VIEW_WARNING_FILTER = "view.warning_filter";
    public static final String SETTINGS_VIEW_INFORMATION_FILTER = "view.information_filter";
    
    public static final String REVIEW_MARKER = "// @PMD:REVIEWED:";
    public static final String REVIEW_ADDITIONAL_COMMENT_DEFAULT = "by {0} on {1}";
    public static final String REVIEW_ADDITIONAL_COMMENT_PREFERENCE = PLUGIN_ID + ".review_additional_comment";

    // Static attributes
    private static PMDPlugin plugin;
    private static Log log;

    // Private attributes
    private Properties messageTable;
    private RuleSet ruleSet;
    private String[] priorityLabels;
    private String reviewAdditionalComment;

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
            ruleSet = getRuleSetFromStateLocation();
        }

        return ruleSet;
    }

    /**
     * Set the rule set and store it in the preferences
     */
    public void setRuleSet(RuleSet newRuleSet, IProgressMonitor monitor) {
        Set newRules = getNewRules(newRuleSet);
        if (!newRules.isEmpty()) {
            addNewRulesToConfiguredProjects(newRules, monitor);
        }

        ruleSet = newRuleSet;
        storeRuleSetInStateLocation(ruleSet);
    }

    /**
     * Get a sub ruleset from a rule list
     */
    public RuleSet getRuleSetFromRuleList(String ruleList) {
        RuleSet subRuleSet = new RuleSet();
        RuleSet ruleSet = getRuleSet();

        StringTokenizer st = new StringTokenizer(ruleList, LIST_DELIMITER);
        while (st.hasMoreTokens()) {
            try {
                Rule rule = ruleSet.getRuleByName(st.nextToken());
                if (rule != null) {
                    subRuleSet.addRule(rule);
                }
            } catch (RuntimeException e) {
                logError("Ignored runtime exception from PMD : ", e);
            }
        }

        return subRuleSet;
    }

    /**
     * Get the rulset configured for the resouce.
     * Currently, it is the one configured for the resource's project
     */
    public RuleSet getRuleSetForResource(IResource resource, boolean flCreateProperty) {
        boolean flNeedSave = false;
        RuleSet projectRuleSet = null;
        RuleSet configuredRuleSet = getRuleSet();
        IProject project = resource.getProject();
        try {
            projectRuleSet = (RuleSet) project.getSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET);
            if (projectRuleSet == null) {
                String activeRulesList = project.getPersistentProperty(PERSISTENT_PROPERTY_ACTIVE_RULESET);
                if (activeRulesList != null) {
                    projectRuleSet = getRuleSetFromRuleList(activeRulesList);
                    flNeedSave = true;
                } else {
                    if (flCreateProperty) {
                        projectRuleSet = configuredRuleSet;
                        flNeedSave = true;
                    } else {
                        flNeedSave = false;
                    }
                }
            }

            // If meanwhile, rules have been deleted from preferences
            // delete them also from the project ruleset
            if ((projectRuleSet != null) && (projectRuleSet != configuredRuleSet)) {
                Iterator i = projectRuleSet.getRules().iterator();
                while (i.hasNext()) {
                    Object rule = i.next();
                    if (!configuredRuleSet.getRules().contains(rule)) {
                        i.remove();
                        flNeedSave = true;
                    }
                }
            }

            // If needed store modified ruleset
            if (flNeedSave) {
                storeRuleSetForResource(resource, projectRuleSet);
            }
        } catch (CoreException e) {
            logError("Error when searching for project ruleset. Using the full ruleset.", e);
        }

        return projectRuleSet;
    }

    /**
     * Store the rules selection in resource property
     */
    public void storeRuleSetForResource(IResource resource, RuleSet ruleSet) {
        try {
            StringBuffer ruleSelectionList = new StringBuffer();
            Iterator i = ruleSet.getRules().iterator();
            while (i.hasNext()) {
                Rule rule = (Rule) i.next();
                ruleSelectionList.append(rule.getName()).append(LIST_DELIMITER);
            }

            resource.setPersistentProperty(PERSISTENT_PROPERTY_ACTIVE_RULESET, ruleSelectionList.toString());
            resource.setSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET, ruleSet);

        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_STORING_PROPERTY), e);
        }
    }

    /**
     * Get rule set from state location
     */
    private RuleSet getRuleSetFromStateLocation() {
        RuleSet preferedRuleSet = null;
        RuleSetFactory factory = new RuleSetFactory();
        
        // First find the ruleset file in the state location
        IPath ruleSetLocation = getStateLocation().append(RULESET_FILE);
        File ruleSetFile = new File(ruleSetLocation.toOSString());
        if (ruleSetFile.exists()) {
            try {
                FileInputStream in = new FileInputStream(ruleSetLocation.toOSString());
                preferedRuleSet = factory.createRuleSet(in);
                in.close();
            } catch (FileNotFoundException e) {
                showError(getMessage(PMDConstants.MSGKEY_ERROR_READING_PREFERENCE), e);
            } catch (IOException e) {
                showError(getMessage(PMDConstants.MSGKEY_ERROR_READING_PREFERENCE), e);
            }
        }
        
        // For compatibility test the preference store
        if (preferedRuleSet == null) {
            preferedRuleSet = getRuleSetFromPreference();
            if (preferedRuleSet != null) {
                storeRuleSetInStateLocation(preferedRuleSet);
                getPreferenceStore().setValue(RULESET_PREFERENCE, RULESET_DEFAULT);
                
            }
        }
        
        // Finally, build a default ruleser
        if (preferedRuleSet == null) {
            preferedRuleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(RULESET_DEFAULTLIST[0]));
            for (int i = 1; i < RULESET_DEFAULTLIST.length; i++) {
                RuleSet tmpRuleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(RULESET_DEFAULTLIST[i]));
                preferedRuleSet.addRuleSet(tmpRuleSet);
            }

            preferedRuleSet.setName("pmd-eclipse");
            preferedRuleSet.setDescription("PMD Plugin preferences rule set");
        }

        return preferedRuleSet;

    }

    /**
     * Get rule set from preference (old version ; used for compatibility)
     */
    private RuleSet getRuleSetFromPreference() {
        RuleSet preferedRuleSet = null;
        RuleSetFactory factory = new RuleSetFactory();
        String ruleSetPreference = getPreferenceStore().getString(RULESET_PREFERENCE);

        if (!ruleSetPreference.equals(RULESET_DEFAULT)) {
            try {
                InputStream ruleSetStream = new ByteArrayInputStream(ruleSetPreference.getBytes());
                preferedRuleSet = factory.createRuleSet(ruleSetStream);
                ruleSetStream.close();
            } catch (IOException e) {
                logError(getMessage(PMDConstants.MSGKEY_ERROR_READING_PREFERENCE), e);
            }
        }

        return preferedRuleSet;

    }

    /**
     * Store the rule set in preference store
     */
    private void storeRuleSetInStateLocation(RuleSet ruleSet) {
        try {
            IPath ruleSetLocation = getStateLocation().append(RULESET_FILE);
            FileOutputStream out = new FileOutputStream(ruleSetLocation.toOSString());
            RuleSetWriter writer = new RuleSetWriter(out);
            writer.write(ruleSet);
            out.flush();
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
     * Get an image corresponding to the severity
     */
    public Image getImage(String key, String iconPath) {
        ImageRegistry registry = PMDPlugin.getDefault().getImageRegistry();
        Image image = registry.get(key);
        if (image == null) {
            ImageDescriptor descriptor = getImageDescriptor(iconPath);
            if (descriptor != null) {
                registry.put(key, descriptor);
                image = registry.get(key);
            }
        }

        return image;
    }

    /**
     * Get a new image descriptor
     */
    public ImageDescriptor getImageDescriptor(String iconPath) {
        ImageDescriptor descriptor = null;
        try {
            URL urlBasic = getDescriptor().getInstallURL();
            URL urlIcon = new URL(urlBasic, iconPath);
            descriptor = ImageDescriptor.createFromURL(urlIcon);
        } catch (MalformedURLException e) {
            logError("Exception when search for icons", e);
        }

        return descriptor;
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

    /**
     * Find if rules has been added
     */
    private Set getNewRules(RuleSet newRuleSet) {
        Set addedRules = new HashSet();
        Set newRules = newRuleSet.getRules();
        Iterator i = newRules.iterator();
        while (i.hasNext()) {
            Rule rule = (Rule) i.next();
            try {
                ruleSet.getRuleByName(rule.getName());
            } catch (RuntimeException e) {
                addedRules.add(rule);
            }
        }

        return addedRules;
    }

    /**
     * Add new rules to already configured projects
     */
    private void addNewRulesToConfiguredProjects(Set addedRules, IProgressMonitor monitor) {
        RuleSet addedRuleSet = new RuleSet();
        Iterator ruleIterator = addedRules.iterator();
        while (ruleIterator.hasNext()) {
            Rule rule = (Rule) ruleIterator.next();
            addedRuleSet.addRule(rule);
        }

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        if (monitor != null) {
            monitor.beginTask(getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), projects.length);
        }

        for (int i = 0; i < projects.length; i++) {
            if (monitor != null) {
                monitor.subTask(getMessage(PMDConstants.MSGKEY_MONITOR_UPDATING_PROJECTS) + projects[i].getName());
            }

            if (projects[i].isAccessible()) {
                RuleSet projectRuleSet = getRuleSetForResource(projects[i], false);
                if (projectRuleSet != null) {
                    projectRuleSet.addRuleSet(addedRuleSet);
                    storeRuleSetForResource(projects[i], projectRuleSet);
                }
            }

            if (monitor != null) {
                monitor.worked(1);
            }
        }
    }
    
    /**
     * Get the additional text for review comment
     * @return
     */
    public String getReviewAdditionalComment() {
        if (reviewAdditionalComment == null) {
            getPreferenceStore().setDefault(REVIEW_ADDITIONAL_COMMENT_PREFERENCE, REVIEW_ADDITIONAL_COMMENT_DEFAULT);
            reviewAdditionalComment = getPreferenceStore().getString(REVIEW_ADDITIONAL_COMMENT_PREFERENCE);
        }
        
        return reviewAdditionalComment;
    }

    /**
     * Set the additional text for review comment
     * @param string
     */
    public void setReviewAdditionalComment(String string) {
        reviewAdditionalComment = string;
        getPreferenceStore().setValue(REVIEW_ADDITIONAL_COMMENT_PREFERENCE, reviewAdditionalComment);
    }

    /**
     * Get the jdk13 compatibility property
     * Currently, it is the one configured for the resource's project
     */
    public boolean isJdk13Enable(IResource resource) {
        boolean flNeedSave = false;
        Boolean jdk13Enable;
        IProject project = resource.getProject();
        try {
            jdk13Enable = (Boolean) project.getSessionProperty(SESSION_PROPERTY_JDK13);
            if (jdk13Enable == null) {
                String property = project.getPersistentProperty(PERSISTENT_PROPERTY_JDK13);
                if (property != null) {
                    jdk13Enable = Boolean.valueOf(property);
                    flNeedSave = true;
                } else {
                    jdk13Enable = Boolean.FALSE;
                    flNeedSave = true;
                }
            }

            // If needed store modified ruleset
            if (flNeedSave) {
                setJdk13Enable(resource, jdk13Enable.booleanValue());
            }
        } catch (CoreException e) {
            jdk13Enable = Boolean.FALSE;
            logError("Error when searching for the jdk13 property ; assume false", e);
        }

        return jdk13Enable.booleanValue();
    }

    /**
     * Store the jdk13 compatibility choice in resource property
     */
    public void setJdk13Enable(IResource resource, boolean jdk13Enable) {
        try {
            resource.setPersistentProperty(PERSISTENT_PROPERTY_JDK13, Boolean.toString(jdk13Enable));
            resource.setSessionProperty(SESSION_PROPERTY_JDK13, Boolean.valueOf(jdk13Enable));

        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_STORING_PROPERTY), e);
        }
    }

}
