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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptor;

import org.apache.log4j.Level;

/**
 * Implements the preferences information structure
 * 
 * @author Herlin
 *
 */

class PreferencesImpl implements IPreferences {
	
	private Map<String, Boolean> booleansById = new HashMap<String, Boolean>();
	
    private IPreferencesManager preferencesManager;
    private boolean 			projectBuildPathEnabled;
    private boolean 			pmdPerspectiveEnabled;
    private boolean				checkAfterSaveEnabled;
    private boolean				useCustomPriorityNames;
    private int 				maxViolationsPerFilePerRule;
    private String 				reviewAdditionalComment;
    private boolean 			reviewPmdStyleEnabled;
    private int 				minTileSize;
    private String 				logFileName;
    private Level 				logLevel;
    private Set<String> 		activeRuleNames = new HashSet<String>();
    private Set<String> 		activeRendererNames = new HashSet<String>();
    
    private Map<RulePriority, PriorityDescriptor> uiDescriptorsByPriority = new HashMap<RulePriority, PriorityDescriptor>(5);
    
    /**
     * Is constructed from a preferences manager
     * @param preferencesManager
     */
    public PreferencesImpl(IPreferencesManager preferencesManager) {
        super();
        this.preferencesManager = preferencesManager;
    }

    public boolean boolFor(String prefId) {
    	Boolean value = booleansById.get(prefId);
    	if (value == null) throw new IllegalArgumentException("Unknown pref id: " + prefId);
    	return value;
    }
    
    public void boolFor(String prefId, boolean newValue) {
    	booleansById.put(prefId, newValue);
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#isProjectBuildPathEnabled()
     */
    public boolean isProjectBuildPathEnabled() {
    	return projectBuildPathEnabled;
	}

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setProjectBuildPathEnabled(boolean)
     */
	public void setProjectBuildPathEnabled(boolean projectBuildPathEnabled) {
		this.projectBuildPathEnabled = projectBuildPathEnabled;
	}

	/**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#isPmdPerspectiveEnabled()
     */
    public boolean isPmdPerspectiveEnabled() {
        return pmdPerspectiveEnabled;
    }

	/**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#isCheckCodeAfterSaveEnabled()
     */
    public boolean isCheckAfterSaveEnabled() {
        return checkAfterSaveEnabled;
    }
    
	/**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#isCheckCodeAfterSaveEnabled()
     */
    public void isCheckAfterSaveEnabled(boolean flag) {
        checkAfterSaveEnabled = flag;
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setPmdPerspectiveEnabled(boolean)
     */
    public void setPmdPerspectiveEnabled(boolean pmdPerspectiveEnabled) {
        this.pmdPerspectiveEnabled = pmdPerspectiveEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#getMaxViolationsPerFilePerRule()
     */
    public int getMaxViolationsPerFilePerRule() {
        return maxViolationsPerFilePerRule;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setMaxViolationsPerFilePerRule(int)
     */
    public void setMaxViolationsPerFilePerRule(int maxViolationPerFilePerRule) {
        this.maxViolationsPerFilePerRule = maxViolationPerFilePerRule;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#getReviewAdditionalComment()
     */
    public String getReviewAdditionalComment() {
        return reviewAdditionalComment;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setReviewAdditionalComment(java.lang.String)
     */
    public void setReviewAdditionalComment(String reviewAdditionalComment) {
        this.reviewAdditionalComment = reviewAdditionalComment;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#isReviewPmdStyleEnabled()
     */
    public boolean isReviewPmdStyleEnabled() {
        return reviewPmdStyleEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setReviewPmdStyleEnabled(boolean)
     */
    public void setReviewPmdStyleEnabled(boolean reviewPmdStyleEnabled) {
        this.reviewPmdStyleEnabled = reviewPmdStyleEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#getMinTileSize()
     */
    public int getMinTileSize() {
        return minTileSize;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setMinTileSize(int)
     */
    public void setMinTileSize(int minTileSize) {
        this.minTileSize = minTileSize;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#getLogFileName()
     */
    public String getLogFileName() {
        return logFileName;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#getLogLevel()
     */
    public Level getLogLevel() {
        return logLevel;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setLogFileName(java.lang.String)
     */
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#setLogLevel(org.apache.log4j.Level)
     */
    public void setLogLevel(Level level) {
        logLevel = level;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences#sync()
     */
    public void sync() {
        preferencesManager.storePreferences(this);
    }

	public boolean isActive(String ruleName) {
		return activeRuleNames.contains(ruleName);
	}

	public boolean isActiveRenderer(String rendererName) {
		return activeRendererNames.contains(rendererName);
	}
	
	public void isActive(String ruleName, boolean flag) {
		if (flag) {
			activeRuleNames.add(ruleName);
		} else {
			activeRuleNames.remove(ruleName);
		}
	}

	public Set<String> getActiveRuleNames() {
		return activeRuleNames;
	}

	public void setActiveRuleNames(Set<String> ruleNames) {
		activeRuleNames = ruleNames;
	}

	public void setPriorityDescriptor(RulePriority priority, PriorityDescriptor pd) {
		uiDescriptorsByPriority.put(priority, pd);		
	}

	public PriorityDescriptor getPriorityDescriptor(RulePriority priority) {
		return uiDescriptorsByPriority.get(priority);
	}

	public boolean useCustomPriorityNames() {
		return useCustomPriorityNames;
	}

	public void useCustomPriorityNames(boolean flag) {
		useCustomPriorityNames = flag;
	}

	public Set<String> activeReportRenderers() {
		return activeRendererNames;
	}

	public void activeReportRenderers(Set<String> names) {
		activeRendererNames = names;
	}

}
