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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.core.IRuleSetManager;
import net.sourceforge.pmd.core.PMDCorePlugin;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;
import net.sourceforge.pmd.eclipse.dao.ConfigurationTO;
import net.sourceforge.pmd.eclipse.dao.ConfigurationsTO;
import net.sourceforge.pmd.eclipse.dao.DAOException;
import net.sourceforge.pmd.eclipse.dao.DAOFactory;
import net.sourceforge.pmd.eclipse.dao.PreferencesDAO;
import net.sourceforge.pmd.eclipse.dao.PreferencesTO;
import net.sourceforge.pmd.eclipse.dao.RuleSetTO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;

/**
 * This is the implementation class for the preferences model.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2005/12/30 16:26:30  phherlin
 * Implement a new preferences model
 *
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
    
    private final PreferencesDAO preferencesDao = DAOFactory.getFactory().getPreferencesDAO();
    private boolean switchPmdPerspective;
    private boolean dfaEnabled;
    private ReviewPreferences reviewPreferences = new ReviewPreferencesImpl();
    private CPDPreferences cpdPreferences = new CPDPreferencesImpl();
    private final Map configurations = new HashMap();
    private final Configuration defaultConfiguration = new ConfigurationImpl("Plugin Default");

    /**
     * Default constructor
     *
     */
    public PreferencesModelImpl() {
        super();
        try {
            loadPreferences();
        } catch (RuntimeException e) {
            PMDPlugin.getDefault().logError("Exception occured when loading preferences", e);
        }
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#dfaEnabled()
     */
    public boolean dfaEnabled() throws ModelException {
        return this.dfaEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#getCpdPreferences()
     */
    public CPDPreferences getCpdPreferences() throws ModelException {
        return this.cpdPreferences;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#getReviewPreferences()
     */
    public ReviewPreferences getReviewPreferences() throws ModelException {
        return this.reviewPreferences;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#isSwitchPmdPerspective()
     */
    public boolean isSwitchPmdPerspective() throws ModelException {
        return this.switchPmdPerspective;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#setDfaEnabled(boolean)
     */
    public void setDfaEnabled(boolean dfaEnabled) throws ModelException {
        this.dfaEnabled = dfaEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PreferencesModel#setSwitchPmdPerspective(boolean)
     */
    public void setSwitchPmdPerspective(boolean switchPmdPerspective) throws ModelException {
        this.switchPmdPerspective = switchPmdPerspective;
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
        try {
            PreferencesTO preferencesTo = new PreferencesTO();
            preferencesTo.setCpdMinTileSize(this.cpdPreferences.getTileSize());
            preferencesTo.setDfaEnabled(this.dfaEnabled);
            preferencesTo.setReviewAdditionalComment(this.reviewPreferences.getAdditionalComment());
            preferencesTo.setReviewNoPmdString(this.reviewPreferences.getNoPmdString());
            preferencesTo.setSwitchPmdPerspective(this.switchPmdPerspective);
            
            storeConfigurations(preferencesTo);
            
            this.preferencesDao.writePreferences(preferencesTo);
        } catch (DAOException e) {
            throw new ModelException(e);
        }
    }
    
    /**
     * Load preferences from preference store
     *
     */
    private void loadPreferences() {
        try {
            PreferencesTO preferencesTo = this.preferencesDao.readPreferences();
            
            this.cpdPreferences.setTileSize(preferencesTo.getCpdMinTileSize());
            this.dfaEnabled = preferencesTo.isDfaEnabled();
            this.reviewPreferences.setAdditionalComment(preferencesTo.getReviewAdditionalComment());
            this.reviewPreferences.setNoPmdString(preferencesTo.getReviewNoPmdString());
            this.switchPmdPerspective = preferencesTo.isSwitchPmdPerspective();
            
            loadConfigurations(preferencesTo);
            loadDefaultConfiguration();
                        
        } catch (DAOException e) {
            PMDPlugin.getDefault().logError("Error intializing the preferences model. The default configuration may be empty", e);
        } catch (ModelException e) {
            PMDPlugin.getDefault().logError("Error intializing the preferences model. The default configuration may be empty", e);
        }
        
    }
    
    private void loadDefaultConfiguration() throws ModelException {
        final RuleSet oldRuleSet = loadDefaultConfigurationForCompatibility();
        if (oldRuleSet == null) {
            final IRuleSetManager ruleSetManager = PMDCorePlugin.getDefault().getRuleSetManager();
            final Set defaultRuleSets = ruleSetManager.getDefaultRuleSets();
            RuleSetProxy proxy = null;
            for (Iterator i = defaultRuleSets.iterator(); i.hasNext();) {
                RuleSet ruleSet = (RuleSet) i.next();
                proxy.setOverride(true);
                proxy.setRuleSet(ruleSet);
                this.defaultConfiguration.addRuleSet(proxy);
            }
        } else {
            RuleSetProxy ruleSet = new RuleSetProxyImpl();
            ruleSet.setOverride(true);
            ruleSet.setRuleSet(oldRuleSet);
            this.defaultConfiguration.addRuleSet(ruleSet);
        }
        
        this.defaultConfiguration.setReadOnly(true);
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
     * Load configurations from stored preferences
     *
     */
    private void loadConfigurations(PreferencesTO preferencesTo) throws ModelException {
        this.configurations.clear();
        ConfigurationTO[] configurations = preferencesTo.getConfigurations().getConfigurations();
        if (configurations != null) {
            for (int i = 0; i < configurations.length; i++) {
                Configuration configuration = new ConfigurationImpl();
                configuration.setName(configurations[i].getName());
                configuration.setReadOnly(configurations[i].isReadOnly());
                
                RuleSetTO[] rulesets = configurations[i].getRuleSets();
                for (int j = 0; j < rulesets.length; j++) {
                    RuleSetProxy ruleSet = new RuleSetProxyImpl();
                    ruleSet.setOverride(rulesets[j].isOverride());
                    ruleSet.setRuleSetUrl(rulesets[j].getRuleSetUrl());
                    ruleSet.setRuleSet(rulesets[j].getRuleSet());
                    configuration.addRuleSet(ruleSet);
                }
                
                this.configurations.put(configuration.getName(), configuration);
            }
        }
    }
    
    /**
     * Store configurations inside a transfer object for effective storage
     * @param preferences
     * @throws ModelException
     */
    private void storeConfigurations(PreferencesTO preferencesTo) throws ModelException {
        ConfigurationsTO configurationsTo = new ConfigurationsTO();
        for (Iterator i = this.configurations.values().iterator(); i.hasNext();) {
            Configuration configuration = (Configuration) i.next();
            ConfigurationTO configurationTo = new ConfigurationTO();
            configurationTo.setName(configuration.getName());
            
            RuleSetProxy[] proxies = configuration.getRuleSets();
            RuleSetTO[] ruleSetTo = new RuleSetTO[proxies.length];
            for (int j = 0; j < proxies.length; j++) {
                ruleSetTo[j] = new RuleSetTO();
                ruleSetTo[j].setOverride(proxies[j].isOverride());
                ruleSetTo[j].setRuleSet(proxies[j].getRuleSet());
                ruleSetTo[j].setRuleSetUrl(proxies[j].getRuleSetUrl());
            }
            configurationTo.setRuleSets(ruleSetTo);
        }
    }
}
