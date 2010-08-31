/*
 * Created on 7 mai 2006
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

package net.sourceforge.pmd.eclipse.runtime.preferences;

import java.util.Set;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.eclipse.plugin.PriorityDescriptor;
import net.sourceforge.pmd.eclipse.ui.Shape;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.apache.log4j.Level;
import org.eclipse.swt.graphics.RGB;

/**
 * This interface models the PMD Plugin preferences
 * 
 * @author Herlin
 *
 */

public interface IPreferences {

    // General Preferences
    
    boolean PROJECT_BUILD_PATH_ENABLED_DEFAULT = true;
    boolean PMD_PERSPECTIVE_ENABLED_DEFAULT = true;
    int MAX_VIOLATIONS_PFPR_DEFAULT = 5;
    String REVIEW_ADDITIONAL_COMMENT_DEFAULT = "by {0} on {1}";
    boolean REVIEW_PMD_STYLE_ENABLED_DEFAULT = true;
    int MIN_TILE_SIZE_DEFAULT = 25;
    String LOG_FILENAME_DEFAULT = "pmd-eclipse.log";
    Level LOG_LEVEL = Level.WARN;
    String ACTIVE_RULES = "";

    PriorityDescriptor PD_1_DEFAULT = new PriorityDescriptor(RulePriority.HIGH, 		StringKeys.VIEW_FILTER_PRIORITY_1, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY_1, null, Shape.diamond, 	new RGB( 255,0,0), 	13);	// red
    PriorityDescriptor PD_2_DEFAULT = new PriorityDescriptor(RulePriority.MEDIUM_HIGH, 	StringKeys.VIEW_FILTER_PRIORITY_2, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY_2, null, Shape.square, 	new RGB( 0,255,255), 13);	// yellow
    PriorityDescriptor PD_3_DEFAULT = new PriorityDescriptor(RulePriority.MEDIUM, 		StringKeys.VIEW_FILTER_PRIORITY_3, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY_3, null, Shape.circle, 	new RGB( 0,255,0), 	13);	// green
    PriorityDescriptor PD_4_DEFAULT = new PriorityDescriptor(RulePriority.MEDIUM_LOW,	StringKeys.VIEW_FILTER_PRIORITY_4, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY_4, null, Shape.domeRight,new RGB( 255,0,255), 13);	// purple
    PriorityDescriptor PD_5_DEFAULT = new PriorityDescriptor(RulePriority.LOW, 	  		StringKeys.VIEW_FILTER_PRIORITY_5, StringKeys.VIEW_TOOLTIP_FILTER_PRIORITY_5, null, Shape.plus, 	new RGB( 0,0,255), 	13);  	// blue
    
    boolean isActive(String rulename);
    
    void isActive(String ruleName, boolean flag);
    
    Set<String> getActiveRuleNames();
    
    void setActiveRuleNames(Set<String> ruleNames);
    
    /**
     * Should the Project Build Path be used?
     */
    boolean isProjectBuildPathEnabled();
    
    /**
     * Set whether using the Project Build Path?
     */
    void setProjectBuildPathEnabled(boolean projectBuildPathEnabled);
    
    /**
     * Should the plugin switch to the PMD perspective when a manual
     * code review is launched ?
     */
    boolean isPmdPerspectiveEnabled();
    
    /**
     * Set whether the plugin switch to the PMD perspective when a manual
     * code review is launched
     */
    void setPmdPerspectiveEnabled(boolean pmdPerspectiveEnabled);
    
    /**
     * Get the maximum number of violations per file per rule reported by the plugin.
     * This parameter is used to improve performances
     */
    int getMaxViolationsPerFilePerRule();
    
    /**
     * Set the maximum number of violations per file per rule reported by the plugin
     * @param maxViolationPerFilePerRule
     */
    void setMaxViolationsPerFilePerRule(int maxViolationPerFilePerRule);
    
    /**
     * Get the review additional comment. This comment is a text appended to the
     * review comment that is inserted into the code when a violation is reviewed.
     * This string follows the MessageFormat syntax and could contain 2 variable fields.
     * The 1st field is replaced by the current used id and the second by the current date.
     */
    String getReviewAdditionalComment();
    
    /**
     * Set the review additional comment.
     * @param reviewAdditionalComment
     */
    void setReviewAdditionalComment(String reviewAdditionalComment);
    
    /**
     * Does the review comment should be the PMD style (// NOPMD comment) or the
     * plugin style (// @PMD:REVIEW...) which was implemented before.
     */
    boolean isReviewPmdStyleEnabled();
    
    /**
     * Set whether the PMD review comment should be used instead of the plugin comment.
     */
    void setReviewPmdStyleEnabled(boolean reviewPmdStyleEnabled);

    void setPriorityDescriptor(RulePriority priority, PriorityDescriptor pd);
    
    PriorityDescriptor getPriorityDescriptor(RulePriority priority);
    
    // CPD Preferences
    
    /**
     * Get the CPD minimum tile size, ie. the number of lines that could be duplicated.
     * ie. lower it is, more duplicated will be found.
     */
    int getMinTileSize();
    
    /**
     * Set the CPD minimul tile size
     */
    void setMinTileSize(int minTileSize);
    
    /**
     * Get the log filename
     */
    String getLogFileName();

    /**
     * Set the log filename
     */
    void setLogFileName(String logFileName);
    
    /**
     * Return the log level
     */
    Level getLogLevel();
    
    /**
     * Set the log level
     */
    void setLogLevel(Level level);
    
    // Globally configured rules
    
    // later...
    
    
    /**
     * Synchronize the preferences with the preferences store
     */
    void sync();
    
}
