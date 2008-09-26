/*
 * Created on 7 juin 2005
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
package net.sourceforge.pmd.eclipse.core;

import java.util.Set;

import net.sourceforge.pmd.RuleSet;

/**
 * Interface for a rule set manager. A RuleSetManager handle a set of rule sets.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2005/07/02 14:33:05  phherlin
 * Implement the RuleSets extension point
 *
 * Revision 1.1  2005/06/07 22:39:57  phherlin
 * Implementing extra ruleset declaration
 *
 *
 */
public interface IRuleSetManager {
    /**
     * Register a rule set
     * @param ruleSet the ruleset to register
     */
    void registerRuleSet(RuleSet ruleSet);
    
    /**
     * Unregister a rule set
     * @param ruleSet the ruleset to unregister
     */
    void unregisterRuleSet(RuleSet ruleSet);
    
    /**
     * @return a set of registered ruleset; this can be empty but never null
     */
    Set getRegisteredRuleSets();
        
    /**
     * Register a rule set for the default set
     * @param ruleSet the ruleset to register
     */
    void registerDefaultRuleSet(RuleSet ruleSet);
    
    /**
     * Unregister a rule set from the default set
     * @param ruleSet the ruleset to unregister
     */
    void unregisterDefaultRuleSet(RuleSet ruleSet);

    /**
     * @return the plugin default ruleset set
     */
    Set getDefaultRuleSets();
}
