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

/**
 * This interface specifies the model of PMD preferences.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2005/10/24 22:41:57  phherlin
 * Refactor preferences management
 *
 * Revision 1.1  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 *
 */
public interface PreferencesModel extends PMDPluginModel {
    /**
     * @return the comment text that will be added to review tags
     * @throws ModelException if an error occurs
     */
    String getReviewAdditionalComment() throws ModelException;
    
    /**
     * Set the comment text that will be added to review tags.
     * This is a dynamic message. Substitutions strings are 0 for the user name
     * and 1 for the current date and time.
     * @param comment the review additional comment
     * @throws ModelException if an error occurs
     */
    void setReviewAdditionalComment(String comment) throws ModelException;
    
    /**
     * @return the string used for NOPMD comments
     * @throws ModelException if an error occurs
     */
    String getNoPmdString() throws ModelException;
    
    /**
     * Set the string for NOPMD comments
     * @param noPmdString the NOPMD string
     * @throws ModelException if an error occurs
     */
    void setNoPmdString(String noPmdString) throws ModelException;
    
    /**
     * @return all plugun rulesets configurations
     * @throws ModelException if an error occurs
     */
    Configuration[] getConfigurations() throws ModelException;
    
    /**
     * Get a configuration by its name
     * @param configurationName a configuration name;
     * @return a configuration or null if not found
     * @throws ModelException if an error occurs
     */
    Configuration searchConfigurationByName(String configurationName) throws ModelException;
    
    /**
     * Add a configuration to the plugin. If the configuration already exists,
     * it is replaced.
     * @param configuration a ruleset configuration
     * @throws ModelException if an error occurs
     */
    void addConfiguration(Configuration configuration) throws ModelException;
    
    /**
     * Remove a configuration from the plugin. This is not an
     * error to remove a configuration that does not exist.
     * @param configuration the configuration to remove
     * @throws ModelException if an error occurs
     */
    void removeConfiguration(Configuration configuration) throws ModelException;
    
    /**
     * Remove a configuration by its name. This is not an
     * error to remove a configuration that does not exist.
     * @param configurationName the configuration to remove.
     * @throws ModelException if an error occurs
     */
    void removeConfigurationByName(String configurationName) throws ModelException;
    
    /**
     * @return a default configuration
     */
    Configuration getDefaultConfiguration() throws ModelException;
    
    /**
     * @return the CPD minimal tile size
     * @throws ModelException if an error occurs
     */
    int getCpdTileSize() throws ModelException;

    /**
     * Set the CPD minimal tile size
     * @param tileSize a tile size
     * @throws ModelException if an error occurs
     */
    void setCpdTileSize(int tileSize) throws ModelException;
}
