/*
 * Created on 8 mai 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.runtime.preferences.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.core.IRuleSetManager;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesFactory;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.WriterException;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptor;
import net.sourceforge.pmd.eclipse.util.IOUtil;
import net.sourceforge.pmd.util.StringUtil;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.graphics.RGB;

/**
 * This class implements the preferences management services
 *
 * @author Herlin
 * @author Brian Remedios
 */

class PreferencesManagerImpl implements IPreferencesManager {
    
    private IPreferences        preferences;
    private IPreferenceStore    storePreferencesStore = PMDPlugin.getDefault().getPreferenceStore();
    private IPreferenceStore    loadPreferencesStore;

    private RuleSet 			ruleSet;
    
    
    private static final Logger log = Logger.getLogger(PreferencesManagerImpl.class);

    private static final String PROJECT_BUILD_PATH_ENABLED  	= PMDPlugin.PLUGIN_ID + ".project_build_path_enabled";
    private static final String PMD_PERSPECTIVE_ENABLED     	= PMDPlugin.PLUGIN_ID + ".pmd_perspective_enabled";
    private static final String PMD_CHECK_AFTER_SAVE_ENABLED	= PMDPlugin.PLUGIN_ID + ".pmd_check_after_save_enabled";
    private static final String MAX_VIOLATIONS_PFPR         	= PMDPlugin.PLUGIN_ID + ".max_violations_pfpr";
    private static final String REVIEW_ADDITIONAL_COMMENT 		= PMDPlugin.PLUGIN_ID + ".review_additional_comment";
    private static final String REVIEW_PMD_STYLE_ENABLED    	= PMDPlugin.PLUGIN_ID + ".review_pmd_style_enabled";
    private static final String PMD_USE_CUSTOM_PRIORITY_NAMES   = PMDPlugin.PLUGIN_ID + ".use_custom_priority_names";
    private static final String MIN_TILE_SIZE               	= PMDPlugin.PLUGIN_ID + ".min_tile_size";
    private static final String LOG_FILENAME                	= PMDPlugin.PLUGIN_ID + ".log_filename";
    private static final String LOG_LEVEL                   	= PMDPlugin.PLUGIN_ID + ".log_level";
    private static final String GLOBAL_RULE_MANAGEMENT          = PMDPlugin.PLUGIN_ID + ".globalRuleManagement";
    private static final String ACTIVE_RULES                	= PMDPlugin.PLUGIN_ID + ".active_rules";
    private static final String ACTIVE_RENDERERS               	= PMDPlugin.PLUGIN_ID + ".active_renderers";
    private static final String ACTIVE_EXCLUSIONS              	= PMDPlugin.PLUGIN_ID + ".active_exclusions";
    private static final String ACTIVE_INCLUSIONS              	= PMDPlugin.PLUGIN_ID + ".active_inclusions";
    
    private static final String OLD_PREFERENCE_PREFIX       = "net.sourceforge.pmd.runtime";
    private static final String OLD_PREFERENCE_LOCATION     = "/.metadata/.plugins/org.eclipse.core.runtime/.settings/net.sourceforge.pmd.runtime.prefs";
    public static final String NEW_PREFERENCE_LOCATION      = "/.metadata/.plugins/org.eclipse.core.runtime/.settings/net.sourceforge.pmd.eclipse.plugin.prefs";

    private static final String PREFERENCE_RULESET_FILE     = "/ruleset.xml";

    private static final Map<RulePriority, PriorityDescriptor> DefaultDescriptorsByPriority = new HashMap<RulePriority, PriorityDescriptor>(5);
    private static final Map<RulePriority, String> StoreKeysByPriority = new HashMap<RulePriority, String>(5);
    
    static {
    	DefaultDescriptorsByPriority.put(RulePriority.HIGH, 		new PriorityDescriptor(RulePriority.HIGH, 		StringKeys.VIEW_FILTER_PRIORITY_1, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY, null, Shape.triangleRight, 	new RGB( 255,0,0), 	13));	// red
    	DefaultDescriptorsByPriority.put(RulePriority.MEDIUM_HIGH, 	new PriorityDescriptor(RulePriority.MEDIUM_HIGH,StringKeys.VIEW_FILTER_PRIORITY_2, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY, null, Shape.triangleRight, 	new RGB( 0,255,255), 13));	// yellow
    	DefaultDescriptorsByPriority.put(RulePriority.MEDIUM, 		new PriorityDescriptor(RulePriority.MEDIUM, 	StringKeys.VIEW_FILTER_PRIORITY_3, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY, null, Shape.triangleRight, 	new RGB( 0,255,0), 	13));	// green
    	DefaultDescriptorsByPriority.put(RulePriority.MEDIUM_LOW, 	new PriorityDescriptor(RulePriority.MEDIUM_LOW,	StringKeys.VIEW_FILTER_PRIORITY_4, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY, null, Shape.triangleRight,	new RGB( 255,0,255), 13));	// purple
    	DefaultDescriptorsByPriority.put(RulePriority.LOW, 			new PriorityDescriptor(RulePriority.LOW, 	  	StringKeys.VIEW_FILTER_PRIORITY_5, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY, null, Shape.triangleRight, 	new RGB( 0,0,255), 	13));  	// blue
    
    	StoreKeysByPriority.put(RulePriority.HIGH, 			PMDPlugin.PLUGIN_ID + ".priority_descriptor_1");
    	StoreKeysByPriority.put(RulePriority.MEDIUM_HIGH, 	PMDPlugin.PLUGIN_ID + ".priority_descriptor_2");
    	StoreKeysByPriority.put(RulePriority.MEDIUM, 		PMDPlugin.PLUGIN_ID + ".priority_descriptor_3");
    	StoreKeysByPriority.put(RulePriority.MEDIUM_LOW, 	PMDPlugin.PLUGIN_ID + ".priority_descriptor_4");
    	StoreKeysByPriority.put(RulePriority.LOW, 			PMDPlugin.PLUGIN_ID + ".priority_descriptor_5");
   }

    public PriorityDescriptor defaultDescriptorFor(RulePriority priority) {
    	return DefaultDescriptorsByPriority.get(priority);
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager#loadPreferences()
     */
    public IPreferences loadPreferences() {
        if (preferences == null) {
            reloadPreferences();
        }

        return preferences;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager#loadPreferences()
     */
    public IPreferences reloadPreferences() {

        initLoadPreferencesStore();
        IPreferencesFactory factory = new PreferencesFactoryImpl();
        preferences = factory.newPreferences(this);

        loadProjectBuildPathEnabled();
        loadPmdPerspectiveEnabled();
        loadCheckAfterSaveEnabled();
        loadUseCustomPriorityNames();
        loadMaxViolationsPerFilePerRule();
        loadReviewAdditionalComment();
        loadReviewPmdStyleEnabled();
        loadMinTileSize();
        loadLogFileName();
        loadLogLevel();
        loadGlobalRuleManagement();
        loadActiveRules();
        loadActiveReportRenderers();
        loadActiveExclusions();
        loadActiveInclusions();
        loadRulePriorityDescriptors();

        return preferences;
    }
    
    /**
     * Initialize 'loadPreferencesStore' to deal with backward compatibility issues.
     * The old preferences use the net.sourceforge.pmd.runtime package instead of the
     * new net.sourceforge.pmd.eclipse.plugin package.
     */
    private void initLoadPreferencesStore() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IPath path = root.getLocation();

        File newPrefs = new File(path.append(NEW_PREFERENCE_LOCATION).toString());
        File oldPrefs = new File(path.append(OLD_PREFERENCE_LOCATION).toString());

        loadPreferencesStore = storePreferencesStore;

        if (!newPrefs.exists() && oldPrefs.exists()) {
            // only retrieve old style preferences if new file doesn't exist
            try {
                Properties props = new Properties();
                FileInputStream in = new FileInputStream(oldPrefs);
                props.load(in);
                in.close();
                loadPreferencesStore = new PreferenceStore();
                for (Map.Entry<Object, Object> entry: props.entrySet()) {
                    String key = (String)entry.getKey();
                    if (key.startsWith(OLD_PREFERENCE_PREFIX)) {
                        key = key.replaceFirst(OLD_PREFERENCE_PREFIX, PMDPlugin.PLUGIN_ID);
                    }
                    loadPreferencesStore.putValue(key, (String)entry.getValue());
                }
            } catch (IOException ioe) {
                PMDPlugin.getDefault().logError("IOException in loading old format preferences", ioe);

                // ignore old preference file
                loadPreferencesStore = storePreferencesStore;
            }
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager#storePreferences(net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences)
     */
    public void storePreferences(IPreferences thePreferences) {
        preferences = thePreferences;

        storeProjectBuildPathEnabled();
        storePmdPerspectiveEnabled();
        storeCheckAfterSaveEnabled();
        storeUseCustomPriorityNames();
        storeMaxViolationsPerFilePerRule();
        storeReviewAdditionalComment();
        storeReviewPmdStyleEnabled();
        storeMinTileSize();
        storeLogFileName();
        storeLogLevel();
        storeGlobalRuleManagement();
        storeActiveRules();
        storeActiveReportRenderers();
        storeActiveExclusions();
        storeActiveInclusions();
        storePriorityDescriptors();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager#getRuleSet()
     */
    public RuleSet getRuleSet() {
        
        if (ruleSet == null) {
            ruleSet = getRuleSetFromStateLocation();
        }
        return ruleSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager#setRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setRuleSet(RuleSet newRuleSet) {
        updateConfiguredProjects(newRuleSet);
        ruleSet = newRuleSet;
        storeRuleSetInStateLocation(ruleSet);
    }

    private void loadProjectBuildPathEnabled() {
        loadPreferencesStore.setDefault(PROJECT_BUILD_PATH_ENABLED, IPreferences.PROJECT_BUILD_PATH_ENABLED_DEFAULT);
        preferences.setProjectBuildPathEnabled(loadPreferencesStore.getBoolean(PROJECT_BUILD_PATH_ENABLED));
    }
    
    private void loadPmdPerspectiveEnabled() {
        loadPreferencesStore.setDefault(PMD_PERSPECTIVE_ENABLED, IPreferences.PMD_PERSPECTIVE_ENABLED_DEFAULT);
        preferences.setPmdPerspectiveEnabled(loadPreferencesStore.getBoolean(PMD_PERSPECTIVE_ENABLED));
    }

    private void loadCheckAfterSaveEnabled() {
        loadPreferencesStore.setDefault(PMD_CHECK_AFTER_SAVE_ENABLED, IPreferences.PMD_CHECK_AFTER_SAVE_DEFAULT);
        preferences.isCheckAfterSaveEnabled(loadPreferencesStore.getBoolean(PMD_CHECK_AFTER_SAVE_ENABLED));
    }
    
    private void loadUseCustomPriorityNames() {
        loadPreferencesStore.setDefault(PMD_USE_CUSTOM_PRIORITY_NAMES, IPreferences.PMD_USE_CUSTOM_PRIORITY_NAMES_DEFAULT);
        preferences.useCustomPriorityNames(loadPreferencesStore.getBoolean(PMD_USE_CUSTOM_PRIORITY_NAMES));
    }

    private void loadMaxViolationsPerFilePerRule() {
        loadPreferencesStore.setDefault(MAX_VIOLATIONS_PFPR, IPreferences.MAX_VIOLATIONS_PFPR_DEFAULT);
        preferences.setMaxViolationsPerFilePerRule(loadPreferencesStore.getInt(MAX_VIOLATIONS_PFPR));
    }

    private void loadReviewAdditionalComment() {
        loadPreferencesStore.setDefault(REVIEW_ADDITIONAL_COMMENT, IPreferences.REVIEW_ADDITIONAL_COMMENT_DEFAULT);
        preferences.setReviewAdditionalComment(loadPreferencesStore.getString(REVIEW_ADDITIONAL_COMMENT));
    }

    private void loadReviewPmdStyleEnabled() {
        loadPreferencesStore.setDefault(REVIEW_PMD_STYLE_ENABLED, IPreferences.REVIEW_PMD_STYLE_ENABLED_DEFAULT);
        preferences.setReviewPmdStyleEnabled(loadPreferencesStore.getBoolean(REVIEW_PMD_STYLE_ENABLED));
    }

    private void loadMinTileSize() {
        loadPreferencesStore.setDefault(MIN_TILE_SIZE, IPreferences.MIN_TILE_SIZE_DEFAULT);
        preferences.setMinTileSize(this.loadPreferencesStore.getInt(MIN_TILE_SIZE));
    }

    private void loadLogFileName() {
        loadPreferencesStore.setDefault(LOG_FILENAME, IPreferences.LOG_FILENAME_DEFAULT);
        preferences.setLogFileName(loadPreferencesStore.getString(LOG_FILENAME));
    }

    private void loadLogLevel() {
        loadPreferencesStore.setDefault(LOG_LEVEL, IPreferences.LOG_LEVEL.toString());
        preferences.setLogLevel(Level.toLevel(loadPreferencesStore.getString(LOG_LEVEL)));
    }

    private void loadGlobalRuleManagement() {
        loadPreferencesStore.setDefault(GLOBAL_RULE_MANAGEMENT, false);
        preferences.setGlobalRuleManagement(loadPreferencesStore.getBoolean(GLOBAL_RULE_MANAGEMENT));
    }

    private void loadActiveRules() {
        loadPreferencesStore.setDefault(ACTIVE_RULES, preferences.getDefaultActiveRules());
        preferences.setActiveRuleNames(asStringSet(loadPreferencesStore.getString(ACTIVE_RULES), ","));
    }

    private void loadActiveReportRenderers() {
        loadPreferencesStore.setDefault(ACTIVE_RENDERERS, IPreferences.ACTIVE_RENDERERS);
        preferences.activeReportRenderers(asStringSet(loadPreferencesStore.getString(ACTIVE_RENDERERS), ","));
    }
    
    private void loadActiveExclusions() {
        loadPreferencesStore.setDefault(ACTIVE_EXCLUSIONS, IPreferences.ACTIVE_EXCLUSIONS);
        preferences.activeExclusionPatterns(asStringSet(loadPreferencesStore.getString(ACTIVE_EXCLUSIONS), ","));
    }
    
    private void loadActiveInclusions() {
        loadPreferencesStore.setDefault(ACTIVE_INCLUSIONS, IPreferences.ACTIVE_INCLUSIONS);
        preferences.activeInclusionPatterns(asStringSet(loadPreferencesStore.getString(ACTIVE_INCLUSIONS), ","));
    }
    
    private void loadRulePriorityDescriptors() {
    	
    	for (Map.Entry<RulePriority, String> entry : StoreKeysByPriority.entrySet()) {
    		PriorityDescriptor desc = defaultDescriptorFor(entry.getKey());
    		loadPreferencesStore.setDefault(entry.getValue(), desc.storeString());
    		String storeKey = StoreKeysByPriority.get(entry.getKey());
    		preferences.setPriorityDescriptor(entry.getKey(), PriorityDescriptor.from( loadPreferencesStore.getString(storeKey) ) );
    	}
    }
    
    
    private static Set<String> asStringSet(String delimitedString, String delimiter) {
    	
    	String[] values = delimitedString.split(delimiter);
    	Set<String> valueSet = new HashSet<String>(values.length);
    	for (int i=0; i<values.length; i++) {
    		String name = values[i].trim();
    		if (StringUtil.isEmpty(name)) continue;
    		valueSet.add(name);
    	}
    	return valueSet;
    }
    
    private static String asDelimitedString(Set<String>values, String delimiter) {
    	
    	if (values == null || values.isEmpty()) return "";
    	
    	StringBuilder sb = new StringBuilder();
    	
    	for (String value : values) {
    		sb.append(delimiter).append(value);
    	}
    	
    	return sb.toString();
    }
    
    private void storeGlobalRuleManagement() {
        storePreferencesStore.setValue(GLOBAL_RULE_MANAGEMENT, preferences.getGlobalRuleManagement());
    }

    private void storeActiveRules() {
    	storePreferencesStore.setValue(ACTIVE_RULES, asDelimitedString(preferences.getActiveRuleNames(), ","));
    }
    
    private void storeActiveReportRenderers() {
    	storePreferencesStore.setValue(ACTIVE_RENDERERS, asDelimitedString(preferences.activeReportRenderers(), ","));
    }

    private void storeActiveExclusions() {
    	storePreferencesStore.setValue(ACTIVE_EXCLUSIONS, asDelimitedString(preferences.activeExclusionPatterns(), ","));
    }
    
    private void storeActiveInclusions() {
    	storePreferencesStore.setValue(ACTIVE_INCLUSIONS, asDelimitedString(preferences.activeInclusionPatterns(), ","));
    }
    
    private void storeProjectBuildPathEnabled() {
        storePreferencesStore.setValue(PROJECT_BUILD_PATH_ENABLED, preferences.isProjectBuildPathEnabled());
    }

    private void storeCheckAfterSaveEnabled() {
        storePreferencesStore.setValue(PMD_CHECK_AFTER_SAVE_ENABLED, preferences.isCheckAfterSaveEnabled());
    }
    
    private void storeUseCustomPriorityNames() {
        storePreferencesStore.setValue(PMD_USE_CUSTOM_PRIORITY_NAMES, preferences.useCustomPriorityNames());
    }
    
    private void storePmdPerspectiveEnabled() {
        storePreferencesStore.setValue(PMD_PERSPECTIVE_ENABLED, preferences.isPmdPerspectiveEnabled());
    }

    private void storeMaxViolationsPerFilePerRule() {
        storePreferencesStore.setValue(MAX_VIOLATIONS_PFPR, preferences.getMaxViolationsPerFilePerRule());
    }

    private void storeReviewAdditionalComment() {
        storePreferencesStore.setValue(REVIEW_ADDITIONAL_COMMENT, preferences.getReviewAdditionalComment());
    }

    private void storeReviewPmdStyleEnabled() {
        storePreferencesStore.setValue(REVIEW_PMD_STYLE_ENABLED, preferences.isReviewPmdStyleEnabled());
    }

    private void storeMinTileSize() {
        storePreferencesStore.setValue(MIN_TILE_SIZE, preferences.getMinTileSize());
    }

    private void storeLogFileName() {
        storePreferencesStore.setValue(LOG_FILENAME, preferences.getLogFileName());
    }

    private void storeLogLevel() {
        storePreferencesStore.setValue(LOG_LEVEL, preferences.getLogLevel().toString());
    }

    private void storePriorityDescriptors() {
    	
    	for (Map.Entry<RulePriority, String> entry : StoreKeysByPriority.entrySet()) {
    		PriorityDescriptor desc = preferences.getPriorityDescriptor(entry.getKey());
    		storePreferencesStore.setValue(entry.getValue(), desc.storeString());
    	}
    }
    
    /**
     * Get rule set from state location
     */
    private RuleSet getRuleSetFromStateLocation() {
        RuleSet preferedRuleSet = null;
        RuleSetFactory factory = new RuleSetFactory();

        // First find the ruleset file in the state location
        IPath ruleSetLocation = PMDPlugin.getDefault().getStateLocation().append(PREFERENCE_RULESET_FILE);
        File ruleSetFile = new File(ruleSetLocation.toOSString());
        if (ruleSetFile.exists()) {
            try {
                preferedRuleSet = factory.createRuleSet(ruleSetLocation.toOSString());
            } catch (RuntimeException e) {
            	PMDPlugin.getDefault().logError("Runtime Exception when loading state ruleset file", e);
            } catch (RuleSetNotFoundException e) {
            	PMDPlugin.getDefault().logError("RuleSet Not Found Exception when loading state ruleset file", e);
	    }
        }

        // Finally, build a default ruleset
        if (preferedRuleSet == null) {
            preferedRuleSet = new RuleSet();
            preferedRuleSet.setName("pmd-eclipse");
            preferedRuleSet.setDescription("PMD Plugin preferences rule set");

            IRuleSetManager ruleSetManager = PMDPlugin.getDefault().getRuleSetManager();
            for (RuleSet ruleSet: ruleSetManager.getDefaultRuleSets()) {
                preferedRuleSet.addRuleSetByReference(ruleSet, false);
            }
        }

        return preferedRuleSet;

    }

    /**
     * Find if rules has been added
     */
    private Set<Rule> getNewRules(RuleSet newRuleSet) {
        Set<Rule> addedRules = new HashSet<Rule>();
        for (Rule rule: newRuleSet.getRules()) {
            if (this.ruleSet.getRuleByName(rule.getName()) == null) {
                addedRules.add(rule);
            }
        }

        return addedRules;
    }

    /**
     * Add new rules to already configured projects, and update the exclude/include patterns
     */
    private void updateConfiguredProjects(RuleSet updatedRuleSet) {
    	log.debug("Updating configured projects");
        RuleSet addedRuleSet = new RuleSet();
        for (Rule rule: getNewRules(updatedRuleSet)) {
            addedRuleSet.addRule(rule);
        }

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        for (IProject project : projects) {

            if (project.isAccessible()) {
                try {
                    IProjectProperties properties = PMDPlugin.getDefault().loadProjectProperties(project);
                    RuleSet projectRuleSet = properties.getProjectRuleSet();
                    if (projectRuleSet != null) {
                        projectRuleSet.addRuleSet(addedRuleSet);
                        projectRuleSet.setExcludePatterns(new ArrayList<String>(updatedRuleSet.getExcludePatterns()));
                        projectRuleSet.setIncludePatterns(new ArrayList<String>(updatedRuleSet.getIncludePatterns()));
                        properties.setProjectRuleSet(projectRuleSet);
                        properties.sync();
                    }
                } catch (PropertiesException e) {
                    PMDPlugin.getDefault().logError("Unable to add new rules for project: " + project, e);
                }
            }
        }
    }

    /**
     * Store the rule set in preference store
     */
    private void storeRuleSetInStateLocation(RuleSet ruleSet) {
    	OutputStream out = null;
    	PMDPlugin plugin = PMDPlugin.getDefault();
    	
        try {
            IPath ruleSetLocation = plugin.getStateLocation().append(PREFERENCE_RULESET_FILE);
            out = new FileOutputStream(ruleSetLocation.toOSString());
            IRuleSetWriter writer = plugin.getRuleSetWriter();
            writer.write(out, ruleSet);
            out.flush();
            
        } catch (IOException e) {
        	plugin.logError("IO Exception when storing ruleset in state location", e);
        } catch (WriterException e) {
        	plugin.logError("General PMD Eclipse Exception when storing ruleset in state location", e);
        } finally {
        	IOUtil.closeQuietly(out);
        }
    }
}
