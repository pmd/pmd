package net.sourceforge.pmd.eclipse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.core.IRuleSetManager;
import net.sourceforge.pmd.core.PMDCorePlugin;
import net.sourceforge.pmd.eclipse.model.ModelException;
import net.sourceforge.pmd.eclipse.model.ModelFactory;
import net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.32  2006/05/02 20:10:49  phherlin
 * Limit the number of reported violations per file and per rule
 *
 * Revision 1.31  2006/05/02 18:47:10  phherlin
 * Remove dead code
 *
 * Revision 1.30  2006/04/24 19:34:18  phherlin
 * Add performance mesures on commands and on pmd execution
 *
 * Revision 1.29  2006/04/10 20:55:06  phherlin
 * Update to PMD 3.6
 *
 * Revision 1.28  2005/10/24 22:47:41  phherlin
 * Integrating Sebastian Raffel's work
 *
 * Revision 1.27  2005/07/02 14:34:08  phherlin
 * Use the new ruleset manager to get registered default rules
 *
 * Revision 1.26  2005/06/30 23:23:47  phherlin
 * Refactoring the addition of new rules to the preferences
 *
 * Revision 1.25  2005/06/29 21:38:50  phherlin
 * Applying patches from Brian Remedios
 *
 * Revision 1.24  2005/06/29 20:10:33  phherlin
 * Moving dev environment to Eclipse v3.1
 * Revision 1.23 2005/05/31 20:33:02 phherlin
 * Continuing refactoring
 * 
 * Revision 1.22 2004/06/29 22:00:30 phherlin Adapting the plugin to the new
 * OSGi standards Revision 1.21 2004/05/26 15:55:23 phherlin Upgrading to PMD
 * 1.8: adding finalizers ruleset to the default rulesets list
 * 
 * Revision 1.20 2003/12/18 23:58:37 phherlin Fixing malformed UTF-8 characters
 * in generated xml files
 * 
 * Revision 1.19 2003/12/09 00:15:00 phherlin Merging from v2 development
 * 
 * Revision 1.18 2003/12/01 22:27:49 phherlin The default rulesets list is all
 * pmd rulesets
 * 
 * Revision 1.17 2003/11/30 22:57:43 phherlin Merging from eclipse-v2
 * development branch
 * 
 * Revision 1.15.2.4 2003/11/07 14:33:57 phherlin Implementing the "project
 * ruleset" feature
 * 
 * Revision 1.15.2.3 2003/11/04 13:26:38 phherlin Implement the working set
 * feature (working set filtering)
 * 
 * Revision 1.15.2.2 2003/10/29 14:26:05 phherlin Refactoring JDK 1.3
 * compatibility feature. Now use the compiler compliance option.
 * 
 * Revision 1.15.2.1 2003/10/29 13:22:34 phherlin Fix JDK1.3 runtime problem
 * (Thanks to Eduard Naum)
 * 
 * Revision 1.15 2003/10/16 22:26:37 phherlin Fix bug #810858. Complete
 * refactoring of rule set generation. Using a DOM tree and the Xerces 2
 * serializer.
 * 
 * Revision 1.14 2003/09/29 22:38:09 phherlin Adding and implementing "JDK13
 * compatibility" property.
 * 
 * Revision 1.13 2003/08/14 16:10:41 phherlin Implementing Review feature
 * (RFE#787086)
 * 
 * Revision 1.12 2003/08/11 21:57:28 phherlin Refactoring ruleset preference
 * store : moving to state location
 * 
 * Revision 1.11 2003/07/30 19:29:02 phherlin Updating to PMD v1.2
 * 
 * Revision 1.10 2003/07/07 19:23:59 phherlin Adding PMD violations view
 * 
 * Revision 1.9 2003/07/01 20:22:16 phherlin Make rules selectable from projects
 * 
 * Revision 1.8 2003/06/30 20:16:06 phherlin Redesigning plugin configuration
 *  
 */
public class PMDPlugin extends AbstractUIPlugin implements PMDPluginConstants {

    // Static attributes
    private static PMDPlugin plugin;
    private static Log log;

    // Private attributes
    private Properties messageTable;
    private RuleSet ruleSet;
    private String[] priorityLabels;
    private String reviewAdditionalComment;
    private int maxViolationsPerFilePerRule;

    /**
     * Private constructor ensures it remains a singleton.
     */
    public PMDPlugin() {
        super();
        plugin = this;
    }

    /**
     * Returns the shared instance.
     */
    public static PMDPlugin getDefault() {
        return plugin;
    }

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        try {
            URL messageTableUrl = this.find(new Path("$nl$/messages.properties"));
            if (messageTableUrl != null) {
                messageTable = new Properties();
                messageTable.load(messageTableUrl.openStream());
            }

            if (log == null) {
                DOMConfigurator.configure(find(new Path("log4j.xml")));
                log = LogFactory.getLog(getClass().toString()); // always derive the name
            }

            log.info("PMD plugin loaded");
        } catch (IOException e) {
            logError("Can't load message table", e);
        }
    }

    /**
     * Returns the string from the message table
     * 
     * @param key
     *            the message key
     * @param defaultMessage
     *            the returned message if key is not found
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
     * 
     * @param key
     *            the message key
     * @return the requested message
     */
    public String getMessage(String key) {
        return getMessage(key, null);
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
     * Get rule set from state location
     */
    private RuleSet getRuleSetFromStateLocation() {
        RuleSet preferedRuleSet = null;
        RuleSetFactory factory = new RuleSetFactory();

        // First find the ruleset file in the state location
        IPath ruleSetLocation = getStateLocation().append(PREFERENCE_RULESET_FILE);
        log.debug("ruleset state location : " + ruleSetLocation.toOSString());
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

        // Finally, build a default ruleset
        if (preferedRuleSet == null) {
            preferedRuleSet = new RuleSet();
            preferedRuleSet.setName("pmd-eclipse");
            preferedRuleSet.setDescription("PMD Plugin preferences rule set");
            
            IRuleSetManager ruleSetManager = PMDCorePlugin.getDefault().getRuleSetManager();
            Iterator i = ruleSetManager.getDefaultRuleSets().iterator();
            while (i.hasNext()) {
                RuleSet ruleSet = (RuleSet) i.next();
                preferedRuleSet.addRuleSet(ruleSet);
            }
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
            IPath ruleSetLocation = getStateLocation().append(PREFERENCE_RULESET_FILE);
            OutputStream out = new FileOutputStream(ruleSetLocation.toOSString());
            RuleSetWriter writer = WriterAbstractFactory.getFactory().getRuleSetWriter();
            writer.write(out, ruleSet);
            out.flush();
            out.close();
        } catch (IOException e) {
            showError(getMessage(PMDConstants.MSGKEY_ERROR_WRITING_PREFERENCE), e);
        } catch (PMDEclipseException e) {
            showError(getMessage(PMDConstants.MSGKEY_ERROR_WRITING_PREFERENCE), e);
        }
    }

    /**
     * Return the priority labels
     */
    public String[] getPriorityLabels() {
        if (priorityLabels == null) {
            priorityLabels = new String[]{
            	getMessage(PMDConstants.MSGKEY_PRIORITY_ERROR_HIGH),
				getMessage(PMDConstants.MSGKEY_PRIORITY_ERROR),
				getMessage(PMDConstants.MSGKEY_PRIORITY_WARNING_HIGH),
				getMessage(PMDConstants.MSGKEY_PRIORITY_WARNING),
				getMessage(PMDConstants.MSGKEY_PRIORITY_INFORMATION)
			};
        }

        return priorityLabels;
    }

    /**
     * @return the priority values
     */
    public Integer[] getPriorityValues() {
    	return new Integer[] {
    			new Integer(1),
    			new Integer(2),
    			new Integer(3),
    			new Integer(4),
    			new Integer(5)
    	};
    }
    
    /**
     * 
     * @return
     */
    public boolean useDFA() {
    	int value = 
    		getPreferenceStore().getInt(PMDPlugin.USE_DFA_PREFERENCE);
    	if (value == 1)
    		return true;
    	else
    		return false;
    }
    
    /**
     * Get an image corresponding to the severity
     */
    public Image getImage(String key, String iconPath) {
        ImageRegistry registry = getImageRegistry();
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
        return imageDescriptorFromPlugin(PLUGIN_ID, iconPath);
    }

    /**
     * Helper method to log error
     * 
     * @see IStatus
     */
    public void logError(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, getBundle().getSymbolicName(), 0, message + t.getMessage(), t));
        if (log != null) {
            log.error(message, t);
        }
    }

    /**
     * Helper method to log error
     * 
     * @see IStatus
     */
    public void logError(IStatus status) {
        getLog().log(status);
        if (log != null) {
            log.error(status.getMessage(), status.getException());
        }
    }

    /**
     * Helper method to display error
     */
    public void showError(final String message, final Throwable t) {
        logError(message, t);
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), getMessage(PMDConstants.MSGKEY_ERROR_TITLE), message
                        + String.valueOf(t));
            }
        });
    }

    /**
     * Helper method to log information message
     */
    public void logInformation(String message) {
        getLog().log(new Status(IStatus.INFO, getBundle().getSymbolicName(), 0, message, null));
        if (log != null) {
            log.info(message);
        }
    }

    /**
     * Find if rules has been added
     */
    private Set getNewRules(RuleSet newRuleSet) {
        Set addedRules = new HashSet();
        Collection newRules = newRuleSet.getRules();
        Iterator i = newRules.iterator();
        while (i.hasNext()) {
            Rule rule = (Rule) i.next();
            if (this.ruleSet.getRuleByName(rule.getName()) == null) {
                addedRules.add(rule);
            }
        }

        return addedRules;
    }

    /**
     * Add new rules to already configured projects
     */
    private void addNewRulesToConfiguredProjects(Set addedRules, IProgressMonitor monitor) {
        log.debug("Add new rules to configured projects");
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
                try {
                    ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(projects[i]);
                    RuleSet projectRuleSet = model.getProjectRuleSet();
                    if (projectRuleSet != null) {
                        projectRuleSet.addRuleSet(addedRuleSet);
                        model.sync();
                    }
                } catch (ModelException e) {
                    logError("Unable to add new rules for project: " + projects[i], e);
                }
            }

            if (monitor != null) {
                monitor.worked(1);
            }
        }
    }

    /**
     * Get the additional text for review comment
     * 
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
     * 
     * @param string
     */
    public void setReviewAdditionalComment(String string) {
        reviewAdditionalComment = string;
        getPreferenceStore().setValue(REVIEW_ADDITIONAL_COMMENT_PREFERENCE, reviewAdditionalComment);
    }

    /**
     * Get the maximum number of violations per file per rule
     * 
     * @return
     */
    public int getMaxViolationsPerFilePerRule() {
        if (this.maxViolationsPerFilePerRule == 0) {
            getPreferenceStore().setDefault(MAX_VIOLATIONS_PER_FILE_PER_RULE_PREFERENCE, MAX_VIOLATIONS_PER_FILE_PER_RULE_DEFAULT);
            this.maxViolationsPerFilePerRule = getPreferenceStore().getInt(MAX_VIOLATIONS_PER_FILE_PER_RULE_PREFERENCE);
        }

        return this.maxViolationsPerFilePerRule;
    }

    /**
     * Set the maximum number of violations per file per rule
     * 
     * @param int
     */
    public void setMaxViolationsPerFilePerRule(int maxViolationsPerFilePerRule) {
        this.maxViolationsPerFilePerRule = maxViolationsPerFilePerRule;
        getPreferenceStore().setValue(MAX_VIOLATIONS_PER_FILE_PER_RULE_PREFERENCE, this.maxViolationsPerFilePerRule);
    }

}