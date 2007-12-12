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

package net.sourceforge.pmd.runtime.preferences.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.core.IRuleSetManager;
import net.sourceforge.pmd.core.PMDCorePlugin;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.preferences.IPreferences;
import net.sourceforge.pmd.runtime.preferences.IPreferencesFactory;
import net.sourceforge.pmd.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.PropertiesException;
import net.sourceforge.pmd.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.runtime.writer.WriterException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This class implements the preferences management services
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 *
 */

class PreferencesManagerImpl implements IPreferencesManager {
    private static final Logger log = Logger.getLogger(PreferencesManagerImpl.class);
    
    private static final String DFA_ENABLED = PMDRuntimePlugin.PLUGIN_ID + ".dfa_enabled";
    private static final String PMD_PERSPECTIVE_ENABLED = PMDRuntimePlugin.PLUGIN_ID + ".pmd_perspective_enabled";
    private static final String MAX_VIOLATIONS_PFPR = PMDRuntimePlugin.PLUGIN_ID + ".max_violations_pfpr";
    private static final String REVIEW_ADDITIONAL_COMMENT = PMDRuntimePlugin.PLUGIN_ID + ".review_additional_comment";
    private static final String REVIEW_PMD_STYLE_ENABLED = PMDRuntimePlugin.PLUGIN_ID + ".review_pmd_style_enabled";
    private static final String MIN_TILE_SIZE = PMDRuntimePlugin.PLUGIN_ID + ".min_tile_size";
    private static final String LOG_FILENAME = PMDRuntimePlugin.PLUGIN_ID + ".log_filename";
    private static final String LOG_LEVEL = PMDRuntimePlugin.PLUGIN_ID + ".log_level";

    private static final String PREFERENCE_RULESET_FILE = "/ruleset.xml";
    
    private IPreferences preferences;
    private IPreferenceStore preferencesStore = PMDRuntimePlugin.getDefault().getPreferenceStore();
    private RuleSet ruleSet;

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferencesManager#loadPreferences()
     */
    public IPreferences loadPreferences() {
        if (this.preferences == null) {
            IPreferencesFactory factory = new PreferencesFactoryImpl();
            this.preferences = factory.newPreferences(this);
            
            loadDfaEnabled();
            loadPmdPerspectiveEnabled();
            loadMaxViolationsPerFilePerRule();
            loadReviewAdditionalComment();
            loadReviewPmdStyleEnabled();
            loadMinTileSize();
            loadLogFileName();
            loadLogLevel();
        }
        
        return this.preferences;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferencesManager#storePreferences(net.sourceforge.pmd.runtime.preferences.IPreferences)
     */
    public void storePreferences(IPreferences preferences) {
        this.preferences = preferences;

        storeDfaEnabled();
        storePmdPerspectiveEnabled();
        storeMaxViolationsPerFilePerRule();
        storeReviewAdditionalComment();
        storeReviewPmdStyleEnabled();
        storeMinTileSize();
        storeLogFileName();
        storeLogLevel();
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferencesManager#getRuleSet()
     */
    public RuleSet getRuleSet() {
        if (this.ruleSet == null) {
            this.ruleSet = getRuleSetFromStateLocation();
        }

        return this.ruleSet;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferencesManager#setRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setRuleSet(RuleSet newRuleSet) {
        Set newRules = getNewRules(newRuleSet);
        if (!newRules.isEmpty()) {
            addNewRulesToConfiguredProjects(newRules);
        }

        ruleSet = newRuleSet;
        storeRuleSetInStateLocation(ruleSet);
    }

    /**
     * Read the dfaEnabled flag
     *
     */
    private void loadDfaEnabled() {
        this.preferencesStore.setDefault(DFA_ENABLED, IPreferences.DFA_ENABLED_DEFAULT);
        this.preferences.setDfaEnabled(this.preferencesStore.getBoolean(DFA_ENABLED));
    }

    /**
     * Read the pmdPerspectiveEnabled flag
     *
     */
    private void loadPmdPerspectiveEnabled() {
        this.preferencesStore.setDefault(PMD_PERSPECTIVE_ENABLED, IPreferences.PMD_PERSPECTIVE_ENABLED_DEFAULT);
        this.preferences.setPmdPerspectiveEnabled(this.preferencesStore.getBoolean(PMD_PERSPECTIVE_ENABLED));
    }

    /**
     * Read the maxViolationsPerFilePerRule preference
     *
     */
    private void loadMaxViolationsPerFilePerRule() {
        this.preferencesStore.setDefault(MAX_VIOLATIONS_PFPR, IPreferences.MAX_VIOLATIONS_PFPR_DEFAULT);
        this.preferences.setMaxViolationsPerFilePerRule(this.preferencesStore.getInt(MAX_VIOLATIONS_PFPR));
    }

    /**
     * Read the review additional comment
     *
     */
    private void loadReviewAdditionalComment() {
        this.preferencesStore.setDefault(REVIEW_ADDITIONAL_COMMENT, IPreferences.REVIEW_ADDITIONAL_COMMENT_DEFAULT);
        this.preferences.setReviewAdditionalComment(this.preferencesStore.getString(REVIEW_ADDITIONAL_COMMENT));
    }

    /**
     * Read the reviewPmdStyle flag
     *
     */
    private void loadReviewPmdStyleEnabled() {
        this.preferencesStore.setDefault(REVIEW_PMD_STYLE_ENABLED, IPreferences.REVIEW_PMD_STYLE_ENABLED_DEFAULT);
        this.preferences.setReviewPmdStyleEnabled(this.preferencesStore.getBoolean(REVIEW_PMD_STYLE_ENABLED));
    }

    /**
     * Read the min tile size preference
     *
     */
    private void loadMinTileSize() {
        this.preferencesStore.setDefault(MIN_TILE_SIZE, IPreferences.MIN_TILE_SIZE_DEFAULT);
        this.preferences.setMinTileSize(this.preferencesStore.getInt(MIN_TILE_SIZE));
    }

    /**
     * Read the log filename
     *
     */
    private void loadLogFileName() {
        this.preferencesStore.setDefault(LOG_FILENAME, IPreferences.LOG_FILENAME_DEFAULT);
        this.preferences.setLogFileName(this.preferencesStore.getString(LOG_FILENAME));
    }
    
    /**
     * Read the log level
     *
     */
    private void loadLogLevel() {
        this.preferencesStore.setDefault(LOG_LEVEL, IPreferences.LOG_LEVEL.toString());
        this.preferences.setLogLevel(Level.toLevel(this.preferencesStore.getString(LOG_LEVEL)));
    }

    /**
     * Write the dfaEnabled flag
     *
     */
    private void storeDfaEnabled() {
        this.preferencesStore.setValue(DFA_ENABLED, this.preferences.isDfaEnabled());
    }

    /**
     * Write the pmdPerspectiveEnabled flag
     *
     */
    private void storePmdPerspectiveEnabled() {
        this.preferencesStore.setValue(PMD_PERSPECTIVE_ENABLED, this.preferences.isPmdPerspectiveEnabled());
    }

    /**
     * Write the maxViolationsPerFilePerRule preference
     *
     */
    private void storeMaxViolationsPerFilePerRule() {
        this.preferencesStore.setValue(MAX_VIOLATIONS_PFPR, this.preferences.getMaxViolationsPerFilePerRule());
    }

    /**
     * Write the review additional comment
     *
     */
    private void storeReviewAdditionalComment() {
        this.preferencesStore.setValue(REVIEW_ADDITIONAL_COMMENT, this.preferences.getReviewAdditionalComment());
    }

    /**
     * Write the reviewPmdStyle flag
     *
     */
    private void storeReviewPmdStyleEnabled() {
        this.preferencesStore.setValue(REVIEW_PMD_STYLE_ENABLED, this.preferences.isReviewPmdStyleEnabled());
    }

    /**
     * Write the min tile size preference
     *
     */
    private void storeMinTileSize() {
        this.preferencesStore.setValue(MIN_TILE_SIZE, this.preferences.getMinTileSize());
    }
    
    /**
     * Write the log filename
     *
     */
    private void storeLogFileName() {
        this.preferencesStore.setValue(LOG_FILENAME, this.preferences.getLogFileName());
    }
    
    /**
     * Write the log level
     *
     */
    private void storeLogLevel() {
        this.preferencesStore.setValue(LOG_LEVEL, this.preferences.getLogLevel().toString());
    }

    /**
     * Get rule set from state location
     */
    private RuleSet getRuleSetFromStateLocation() {
        RuleSet preferedRuleSet = null;
        RuleSetFactory factory = new RuleSetFactory();

        // First find the ruleset file in the state location
        IPath ruleSetLocation = PMDRuntimePlugin.getDefault().getStateLocation().append(PREFERENCE_RULESET_FILE);
        File ruleSetFile = new File(ruleSetLocation.toOSString());
        if (ruleSetFile.exists()) {
            try {
                FileInputStream in = new FileInputStream(ruleSetLocation.toOSString());
                preferedRuleSet = factory.createRuleSet(in);
                in.close();
            } catch (FileNotFoundException e) {
                PMDRuntimePlugin.getDefault().logError("File Not Found Exception when loading state ruleset file", e);
            } catch (IOException e) {
                PMDRuntimePlugin.getDefault().logError("IO Exception when loading state ruleset file", e);
            } catch (RuntimeException e) {
            	PMDRuntimePlugin.getDefault().logError("Runtime Exception when loading state ruleset file", e);
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
    private void addNewRulesToConfiguredProjects(Set addedRules) {
        log.debug("Add new rules to configured projects");
        RuleSet addedRuleSet = new RuleSet();
        Iterator ruleIterator = addedRules.iterator();
        while (ruleIterator.hasNext()) {
            Rule rule = (Rule) ruleIterator.next();
            addedRuleSet.addRule(rule);
        }

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        for (int i = 0; i < projects.length; i++) {

            if (projects[i].isAccessible()) {
                try {
                    IProjectProperties properties = PMDRuntimePlugin.getDefault().loadProjectProperties(projects[i]);
                    RuleSet projectRuleSet = properties.getProjectRuleSet();
                    if (projectRuleSet != null) {
                        projectRuleSet.addRuleSet(addedRuleSet);
                        properties.sync();
                    }
                } catch (PropertiesException e) {
                    PMDRuntimePlugin.getDefault().logError("Unable to add new rules for project: " + projects[i], e);
                }
            }
        }
    }

    /**
     * Store the rule set in preference store
     */
    private void storeRuleSetInStateLocation(RuleSet ruleSet) {
        try {
            IPath ruleSetLocation = PMDRuntimePlugin.getDefault().getStateLocation().append(PREFERENCE_RULESET_FILE);
            OutputStream out = new FileOutputStream(ruleSetLocation.toOSString());
            IRuleSetWriter writer = PMDRuntimePlugin.getDefault().getRuleSetWriter();
            writer.write(out, ruleSet);
            out.flush();
            out.close();
        } catch (IOException e) {
            PMDRuntimePlugin.getDefault().logError("IO Exception when storing ruleset in state location", e);
        } catch (WriterException e) {
            PMDRuntimePlugin.getDefault().logError("General PMD Eclipse Exception when storing ruleset in state location", e);
        }
    }
}
