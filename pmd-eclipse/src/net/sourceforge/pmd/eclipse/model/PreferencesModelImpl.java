/*
 * Created on 5 f�vr. 2005
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.eclipse.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.core.IRuleSetManager;
import net.sourceforge.pmd.core.PMDCorePlugin;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This is the implementation class for the preferences model.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2005/10/24 22:41:57  phherlin
 * Refactor preferences management
 *
 * Revision 1.2  2005/05/31 20:33:02  phherlin
 * Continuing refactoring
 *
 * Revision 1.1  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 *
 */
public class PreferencesModelImpl extends AbstractModel implements PreferencesModel {
    private static final Log log = LogFactory.getLog(PreferencesModelImpl.class);
    
    private static final String REVIEW_ADDITIONAL_COMMENT_DEFAULT = "by {0} on {1}";
    private static final String REVIEW_ADDITIONAL_COMMENT_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".review_additional_comment";
    private static final String MIN_TILE_SIZE_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".CPDPreference.mintilesize";
    private static final int MIN_TILE_SIZE_DEFAULT = 25;
    private static final String NO_PMD_STRING_DEFAULT = "NOPMD";
    private static final String NO_PMD_STRING_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".no_pmd_string";
    private static final String CONFIGURATIONS_DIRECTORY = "/confs";

    private String noPmdString;
    private String reviewAdditionalComment;
    private final Map configurations = new HashMap();
    private int cpdTileSize;
    private final Configuration defaultConfiguration = new ConfigurationImpl("Plugin Default");

    /**
     * Default constructor
     *
     */
    public PreferencesModelImpl() {
        super();
        loadPreferences();
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#getReviewAdditionalComment()
     */
    public String getReviewAdditionalComment() {
        if (this.reviewAdditionalComment == null) {
            this.reviewAdditionalComment = REVIEW_ADDITIONAL_COMMENT_DEFAULT;
        }

        return this.reviewAdditionalComment;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#setReviewAdditionalComment(java.lang.String)
     */
    public void setReviewAdditionalComment(final String comment) {
        this.reviewAdditionalComment = comment;

    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#getCpdTileSize()
     */
    public int getCpdTileSize() throws ModelException {
        if (this.cpdTileSize == 0) {
            this.cpdTileSize = MIN_TILE_SIZE_DEFAULT;
        }
        
        return this.cpdTileSize;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#setCpdTileSize(int)
     */
    public void setCpdTileSize(final int tileSize) throws ModelException {
        this.cpdTileSize = tileSize;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#getNoPmdString()
     */
    public String getNoPmdString() throws ModelException {
        if (this.noPmdString == null) {
            this.noPmdString = NO_PMD_STRING_DEFAULT;
        }
        
        return this.noPmdString;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#setNoPmdString(java.lang.String)
     */
    public void setNoPmdString(final String noPmdString) throws ModelException {
        this.noPmdString = noPmdString;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#addConfiguration(net.sourceforge.pmd.eclipse.model.Configuration)
     */
    public void addConfiguration(final Configuration configuration) throws ModelException {
        if (configuration == null) {
            throw new ModelException("configuration cannot be null"); // TODO NLS
        }
        
        this.configurations.put(configuration.getName(), configuration);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#getConfigurations()
     */
    public Configuration[] getConfigurations() throws ModelException {
        final Collection values = this.configurations.values();
        return (Configuration[]) values.toArray(new Configuration[values.size()]);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#getDefaultConfiguration()
     */
    public Configuration getDefaultConfiguration() throws ModelException {
        return this.defaultConfiguration;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#removeConfiguration(net.sourceforge.pmd.eclipse.model.Configuration)
     */
    public void removeConfiguration(final Configuration configuration) throws ModelException {
        if (configuration == null) {
            throw new ModelException("configuration cannot be null"); // TODO NLS
        }
        
        this.configurations.remove(configuration.getName());
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#removeConfigurationByName(java.lang.String)
     */
    public void removeConfigurationByName(final String configurationName) throws ModelException {
        if ((configurationName == null) || (configurationName.equals(""))) {
            throw new ModelException("Invalid configuration name"); // TODO NLS
        }
        
        this.configurations.remove(configurationName);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#searchConfigurationByName(java.lang.String)
     */
    public Configuration searchConfigurationByName(final String configurationName) throws ModelException {
        if ((configurationName == null) || (configurationName.equals(""))) {
            throw new ModelException("Invalid configuration name"); // TODO NLS
        }
        
        return (Configuration) this.configurations.get(configurationName);
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PMDPluginModel#sync()
     */
    public void sync() throws ModelException {
        final IPreferenceStore preferenceStore = PMDPlugin.getDefault().getPreferenceStore();
        preferenceStore.setValue(REVIEW_ADDITIONAL_COMMENT_PREFERENCE, this.reviewAdditionalComment);
        preferenceStore.setValue(MIN_TILE_SIZE_PREFERENCE, this.cpdTileSize);
        preferenceStore.setValue(NO_PMD_STRING_PREFERENCE, this.noPmdString);
    }
    
    /**
     * Load preferences from preference store
     *
     */
    private void loadPreferences() {
        final IPreferenceStore preferenceStore = PMDPlugin.getDefault().getPreferenceStore();
        this.reviewAdditionalComment = preferenceStore.getString(REVIEW_ADDITIONAL_COMMENT_PREFERENCE);
        this.cpdTileSize = preferenceStore.getInt(MIN_TILE_SIZE_PREFERENCE);
        this.noPmdString = preferenceStore.getString(NO_PMD_STRING_PREFERENCE);
        
        loadConfigurations();

        try {
            final RuleSet oldRuleSet = loadDefaultConfigurationForCompatibility();
            if (oldRuleSet == null) {
                final IRuleSetManager ruleSetManager = PMDCorePlugin.getDefault().getRuleSetManager();
                final Set defaultRuleSets = ruleSetManager.getDefaultRuleSets();
                this.defaultConfiguration.setRuleSets((RuleSet[]) defaultRuleSets.toArray(new RuleSet[defaultRuleSets.size()]));
            } else {
                this.defaultConfiguration.setRuleSets(new RuleSet[] { oldRuleSet });
            }
            this.defaultConfiguration.setReadOnly(true);
        } catch (ModelException e) {
            PMDPlugin.getDefault().logError("Error intializing the preferences model. The default configuration may be empty", e);
        }
    }
    
    /**
     * For compatibility with older versions, load the default configuration with the
     * ruleset stored in the State Location.
     * @return the ruleset or null if not found
     */
    private RuleSet loadDefaultConfigurationForCompatibility() {
        final RuleSetFactory factory = new RuleSetFactory();
        RuleSet ruleSet = null;

        final IPath ruleSetLocation = PMDPlugin.getDefault().getStateLocation().append(PMDPluginConstants.PREFERENCE_RULESET_FILE);
        final File ruleSetFile = new File(ruleSetLocation.toOSString());
        if (ruleSetFile.exists()) {
            try {
                final FileInputStream in = new FileInputStream(ruleSetLocation.toOSString());
                ruleSet = factory.createRuleSet(in);
                in.close();
            } catch (FileNotFoundException e) {
                ; // NOPMD Ignore
            } catch (IOException e) {
                ; // NOPMD Ignore
            }
        }
        
        return ruleSet;
    }
    
    /**
     * Load configurations from state location
     *
     */
    private void loadConfigurations() {
        final IPath configurationsLocation = PMDPlugin.getDefault().getStateLocation().append(CONFIGURATIONS_DIRECTORY);
        final File confsDir = new File(configurationsLocation.toOSString());
        if (confsDir.exists()) {
            final File[] confs = confsDir.listFiles();
            for (int i = 0; i < confs.length; i++) {
                loadConfiguration(confs[i]);
            }
        }
    }
    
    /**
     * Load a configuration
     * @param confLocation the configuration directory
     */
    private void loadConfiguration(final File confLocation) {
        if (confLocation.isDirectory()) {
            try {
                final Configuration conf = new ConfigurationImpl(confLocation.getName());
                final RuleSetFactory factory = new RuleSetFactory();
                final File[] ruleSetFiles = confLocation.listFiles();
                for (int i = 0; i < ruleSetFiles.length; i++) {
                    try {
                        if (ruleSetFiles[i].isFile() && ruleSetFiles[i].canRead()) {
                            final InputStream is = new FileInputStream(ruleSetFiles[i]);
                            final RuleSet ruleSet = factory.createRuleSet(is);
                            conf.addRuleSet(ruleSet);
                            is.close();
                        }
                    } catch (FileNotFoundException e) {
                        log.warn("FileNotFound exception when loading configuration from " + ruleSetFiles[i].getName() + ": " + e.getMessage());
                    } catch (IOException e) {
                        log.warn("IO exception when loading configuration from " + ruleSetFiles[i].getName() + ": " + e.getMessage());
                    }
                }
                
                this.configurations.put(conf.getName(), conf);
            } catch (ModelException e) {
            }
        }
    }
}
