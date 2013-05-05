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

package net.sourceforge.pmd.eclipse.runtime.preferences;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.ui.priority.PriorityDescriptor;

/**
 * This interface specifies the behaviour of the preferences manager.
 * 
 * @author Herlin
 *
 */

public interface IPreferencesManager {
    
    /**
     * Load preferences from the preferences store or return the one that
     * was previously loaded.
     * 
     * @return the preferences structure
     */
    IPreferences loadPreferences();
    
    /**
     * Reload preferences from the preferences store.
     * 
     * @return the preferences structure
     */
    IPreferences reloadPreferences();
    
    /**
     * Store a preferences structure into the preferences store
     * @param preferences structure of the preferences information
     */
    void storePreferences(IPreferences preferences);
    
    
    // The following operations are for a transition period only

    /**
     * Get the configured rule set
     */
    RuleSet getRuleSet();
    
    /**
     * Set the rule set and store it in the preferences
     */
    void setRuleSet(RuleSet newRuleSet);
    
    /**
     * 
     * @param priority
     * @return
     */
    PriorityDescriptor defaultDescriptorFor(RulePriority priority);
}
