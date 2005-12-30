/*
 * Created on 28 déc. 2005
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

package test.net.sourceforge.pmd.eclipse.dao;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.dao.ConfigurationsTO;
import net.sourceforge.pmd.eclipse.dao.DAOException;
import net.sourceforge.pmd.eclipse.dao.DAOFactory;
import net.sourceforge.pmd.eclipse.dao.PreferencesDAO;
import net.sourceforge.pmd.eclipse.dao.PreferencesTO;
import junit.framework.TestCase;

/**
 * Test the Preferences Data Access Object
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/12/30 16:29:15  phherlin
 * Implement a new preferences model and review some tests
 *
 *
 */

public class PreferencesDAOTest extends TestCase {
    
    /**
     * Test reading preferences
     *
     */
    public void testReadPreferences() throws DAOException {
        PreferencesDAO dao = DAOFactory.getFactory().getPreferencesDAO();
        PreferencesTO preferences = dao.readPreferences();
        
        // Assert review preferences are not null
        assertNotNull("Review additional comment should not be null", preferences.getReviewAdditionalComment());
        assertNotNull("No PMD String should not be null", preferences.getReviewNoPmdString());
        assertNotNull("There should be a configurations object", preferences.getConfigurations());
        assertFalse("CPD minimum tile size should not be 0", preferences.getCpdMinTileSize() == 0);
    }
    
    /**
     * Test writing preferences
     * @throws DAOException
     */
    public void testWritePreferences() throws DAOException {
        PreferencesDAO dao = DAOFactory.getFactory().getPreferencesDAO();
        PreferencesTO preferences = new PreferencesTO();
        preferences.setCpdMinTileSize(25);
        preferences.setReviewAdditionalComment("a review additional comment");
        preferences.setDfaEnabled(false);
        preferences.setReviewNoPmdString("NOPMD");
        preferences.setSwitchPmdPerspective(true);
        preferences.setConfigurations(new ConfigurationsTO());
        
        dao.writePreferences(preferences);
        
        IPreferenceStore store = PMDPlugin.getDefault().getPreferenceStore();
        assertEquals("DFA has not been set correctly", -1, store.getInt("net.sourceforge.pmd.eclipse.use_dfa"));
        assertEquals("Switch to PMD perspective has not been set correctly", 1, store.getInt("net.sourceforge.pmd.eclipse.show_perspective_on_check"));
        assertEquals("CPD min tile size has not been set correctly", 25, store.getInt("net.sourceforge.pmd.eclipse.CPDPreference.mintilesize"));
        assertEquals("Review additional comment has not been set correctly", "a review additional comment", store.getString("net.sourceforge.pmd.eclipse.review_additional_comment"));
        assertEquals("No PMD String has not been set correctly", "NOPMD", store.getString("net.sourceforge.pmd.eclipse.no_pmd_string"));
        
        IPath stateLocation = PMDPlugin.getDefault().getStateLocation();
        File confFile = stateLocation.append("configurations.xml").toFile();
        assertNotNull("Conf File object is not expected to be null", confFile);
        assertTrue("Conf file should exist", confFile.canRead());
    }

}
