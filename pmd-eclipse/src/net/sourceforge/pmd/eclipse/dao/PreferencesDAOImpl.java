/*
 * Created on 27 déc. 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.eclipse.dao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;

import org.eclipse.jface.preference.IPreferenceStore;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * Default implementation for preferences persistance
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/12/30 16:25:39  phherlin
 * Implement a new preferences model
 *
 *
 */

class PreferencesDAOImpl implements PreferencesDAO {
    private static final String PREFERENCES_MAPPING = "/net/sourceforge/pmd/eclipse/dao/mapping.xml";
    private static final String REVIEW_ADDITIONAL_COMMENT_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".review_additional_comment";
    private static final String NO_PMD_STRING_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".no_pmd_string";
    private static final String MIN_TILE_SIZE_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".CPDPreference.mintilesize";
    private static final String USE_DFA_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".use_dfa";
    private static final String SHOW_PERSPECTIVE_ON_CHECK_PREFERENCE = PMDPluginConstants.PLUGIN_ID + ".show_perspective_on_check";
    private static final String CONFIGURATIONS_FILE = "configurations.xml";

    private final IPreferenceStore preferenceStore = PMDPlugin.getDefault().getPreferenceStore();

    /**
     * @see net.sourceforge.pmd.eclipse.dao.PreferencesDAO#readPreferences()
     */
    public PreferencesTO readPreferences() throws DAOException {
        PreferencesTO preferences = new PreferencesTO();
        
        loadBasicPreferences(preferences);
        loadConfigurations(preferences);
        
        return preferences;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.dao.PreferencesDAO#writePreferences(net.sourceforge.pmd.eclipse.dao.PreferencesTO)
     */
    public void writePreferences(PreferencesTO preferences) throws DAOException {
        storeBasicPreferences(preferences);
        storeConfigurations(preferences);
    }
    
    /**
     * Read simple preferences from standard Eclipse preferences store
     * @param preferences
     */
    private void loadBasicPreferences(PreferencesTO preferences) {
        preferences.setReviewAdditionalComment(preferenceStore.getString(REVIEW_ADDITIONAL_COMMENT_PREFERENCE));
        preferences.setReviewNoPmdString(preferenceStore.getString(NO_PMD_STRING_PREFERENCE));
        preferences.setCpdMinTileSize(preferenceStore.getInt(MIN_TILE_SIZE_PREFERENCE));
        preferences.setDfaEnabled(preferenceStore.getInt(USE_DFA_PREFERENCE) == 1);
        preferences.setSwitchPmdPerspective(preferenceStore.getInt(SHOW_PERSPECTIVE_ON_CHECK_PREFERENCE) == 1);
    }
 
    /**
     * Store basic preferences into the standard Eclipse preferences store
     * @param preferences
     */
    private void storeBasicPreferences(PreferencesTO preferences) {
        this.preferenceStore.setValue(REVIEW_ADDITIONAL_COMMENT_PREFERENCE, preferences.getReviewAdditionalComment());
        this.preferenceStore.setValue(NO_PMD_STRING_PREFERENCE, preferences.getReviewNoPmdString());
        this.preferenceStore.setValue(MIN_TILE_SIZE_PREFERENCE, preferences.getCpdMinTileSize());
        this.preferenceStore.setValue(SHOW_PERSPECTIVE_ON_CHECK_PREFERENCE, preferences.isSwitchPmdPerspective() ? 1 : -1);
        this.preferenceStore.setValue(USE_DFA_PREFERENCE, preferences.isDfaEnabled() ? 1 : -1);
    }
    
    /**
     * Store configurations
     * @param preferences
     */
    private void storeConfigurations(PreferencesTO preferences) throws DAOException {
        try {
            final Mapping mapping = new Mapping();
            final URL mappingSpecUrl = this.getClass().getResource(PREFERENCES_MAPPING);
            mapping.loadMapping(mappingSpecUrl);

            final FileWriter writer = new FileWriter(PMDPlugin.getDefault().getStateLocation().append(CONFIGURATIONS_FILE).toOSString());
            final Marshaller marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
            marshaller.marshal(preferences.getConfigurations());
            writer.flush();
            writer.close();
            
            

        } catch (MarshalException e) {
            throw new DAOException(e);
        } catch (ValidationException e) {
            throw new DAOException(e);
        } catch (IOException e) {
            throw new DAOException(e);
        } catch (MappingException e) {
            throw new DAOException(e);
        }
    }
    
    /**
     * Load configurations definitions
     * @param preferences
     * @throws DAOException
     */
    private void loadConfigurations(PreferencesTO preferences) throws DAOException {
        ConfigurationsTO configurations = null;
        
        try {
            final Mapping mapping = new Mapping();
            final URL mappingSpecUrl = this.getClass().getResource(PREFERENCES_MAPPING);
            mapping.loadMapping(mappingSpecUrl);

            File configurationFile = PMDPlugin.getDefault().getStateLocation().append(CONFIGURATIONS_FILE).toFile();
            if (configurationFile.canRead()) {
                final Reader reader = new FileReader(configurationFile);
                final Unmarshaller unmarshaller = new Unmarshaller(mapping);
                configurations = (ConfigurationsTO) unmarshaller.unmarshal(reader);
                reader.close();
            }
            
            preferences.setConfigurations(configurations == null ? new ConfigurationsTO() : configurations);
            
        } catch (MarshalException e) {
            throw new DAOException(e);
        } catch (ValidationException e) {
            throw new DAOException(e);
        } catch (IOException e) {
            throw new DAOException(e);
        } catch (MappingException e) {
            throw new DAOException(e);
        }
    }
}
