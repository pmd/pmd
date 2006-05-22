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

import org.apache.log4j.Level;

import net.sourceforge.pmd.runtime.preferences.IPreferences;
import net.sourceforge.pmd.runtime.preferences.IPreferencesManager;

/**
 * Implements the preferences information structure
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

class PreferencesImpl implements IPreferences {
    private IPreferencesManager preferencesManager;
    private boolean dfaEnabled;
    private boolean pmdPerspectiveEnabled;
    private int maxViolationsPerFilePerRule;
    private String reviewAdditionalComment;
    private boolean reviewPmdStyleEnabled;
    private int minTileSize;
    private String logFileName;
    private Level logLevel;
    
    /**
     * Is constructed from a preferences manager
     * @param preferencesManager
     */
    public PreferencesImpl(IPreferencesManager preferencesManager) {
        super();
        this.preferencesManager = preferencesManager;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#isDfaEnabled()
     */
    public boolean isDfaEnabled() {
        return this.dfaEnabled;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setDfaEnabled(boolean)
     */
    public void setDfaEnabled(boolean dfaEnabled) {
        this.dfaEnabled = dfaEnabled;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#isPmdPerspectiveEnabled()
     */
    public boolean isPmdPerspectiveEnabled() {
        return this.pmdPerspectiveEnabled;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setPmdPerspectiveEnabled(boolean)
     */
    public void setPmdPerspectiveEnabled(boolean pmdPerspectiveEnabled) {
        this.pmdPerspectiveEnabled = pmdPerspectiveEnabled;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#getMaxViolationsPerFilePerRule()
     */
    public int getMaxViolationsPerFilePerRule() {
        return this.maxViolationsPerFilePerRule;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setMaxViolationsPerFilePerRule(int)
     */
    public void setMaxViolationsPerFilePerRule(int maxViolationPerFilePerRule) {
        this.maxViolationsPerFilePerRule = maxViolationPerFilePerRule;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#getReviewAdditionalComment()
     */
    public String getReviewAdditionalComment() {
        return this.reviewAdditionalComment;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setReviewAdditionalComment(java.lang.String)
     */
    public void setReviewAdditionalComment(String reviewAdditionalComment) {
        this.reviewAdditionalComment = reviewAdditionalComment;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#isReviewPmdStyleEnabled()
     */
    public boolean isReviewPmdStyleEnabled() {
        return this.reviewPmdStyleEnabled;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setReviewPmdStyleEnabled(boolean)
     */
    public void setReviewPmdStyleEnabled(boolean reviewPmdStyleEnabled) {
        this.reviewPmdStyleEnabled = reviewPmdStyleEnabled;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#getMinTileSize()
     */
    public int getMinTileSize() {
        return this.minTileSize;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setMinTileSize(int)
     */
    public void setMinTileSize(int minTileSize) {
        this.minTileSize = minTileSize;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#getLogFileName()
     */
    public String getLogFileName() {
        return this.logFileName;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#getLogLevel()
     */
    public Level getLogLevel() {
        return this.logLevel;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setLogFileName(java.lang.String)
     */
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#setLogLevel(org.apache.log4j.Level)
     */
    public void setLogLevel(Level level) {
        this.logLevel = level;
    }

    /**
     * @see net.sourceforge.pmd.runtime.preferences.IPreferences#sync()
     */
    public void sync() {
        this.preferencesManager.storePreferences(this);
    }

}
