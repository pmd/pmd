/*
 * Created on 20 juin 2006
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

package net.sourceforge.pmd.eclipse.core.rulesets.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a value objet that is the root of the structure of a rulesets
 * object. It holds the different configurations the user may define and use in
 * each project.
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/06 16:42:46  phherlin
 * Continue refactoring of rullesets management
 *
 * Revision 1.1  2006/06/21 23:06:41  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 * Revision 1.1  2006/06/20 22:03:58  phherlin
 * Implement the last object of the rulesets structure
 *
 * 
 */

public class RuleSets {
    private RuleSet defaultRuleSet;
    private List ruleSetsList = new ArrayList();

    /**
     * Getter for the defaultRuleSet attribute. The default rule set is the one
     * loaded by the "getRuleSet(void)" operation from the preferences manager.
     * Also, the default rule set is the one selected on each new Java project.
     * 
     * @return Returns the defaultRuleSet.
     */
    public RuleSet getDefaultRuleSet() {
        return this.defaultRuleSet;
    }

    /**
     * Setter for the defaultRuleSet attribute. The rule set must belong to the
     * rule sets list.
     * 
     * @param defaultRuleSet The defaultRuleSet to set.
     */
    public void setDefaultRuleSet(RuleSet defaultRuleSet) {
        if (defaultRuleSet == null) {
            throw new IllegalArgumentException("default rule set cannot be null");
        }
        if (!this.ruleSetsList.contains(defaultRuleSet)) {
            throw new IllegalArgumentException("The rule set " + defaultRuleSet.getName()
                    + " must belong to the rule set list to be set as default.");
        }

        this.defaultRuleSet = defaultRuleSet;
    }

    /**
     * Getter of the rule sets list attribute.
     * 
     * @return Returns the ruleSet list.
     */
    public List getRuleSets() {
        return this.ruleSetsList;
    }

    /**
     * Setter of the rule sets list attribute.
     * 
     * @param ruleSetsSet The ruleSetsSet to set.
     */
    public void setRuleSets(List ruleSets) {
        if (ruleSets == null) {
            throw new IllegalArgumentException("ruleSets cannot be null");
        }
        if (ruleSets.size() == 0) {
            throw new IllegalArgumentException("The rule sets list should not be empty");

        }
        this.ruleSetsList = ruleSets;
    }
    
    /**
     * Return the name of the default ruleset
     * @return the name of the default ruleset
     */
    public String getDefaultRuleSetName() {
        return this.defaultRuleSet.getName();
    }
    
    /**
     * Sets the default ruleset by its name. If the ruleset does not exist, 
     * the default ruleset is not set.
     * @param ruleSetName a name of an already defined ruleset.
     */
    public void setDefaultRuleSetName(String ruleSetName) {
        if (ruleSetName == null) {
            throw new IllegalArgumentException("The default ruleset name must not ne null");
        }
        
        for (final Iterator i = this.ruleSetsList.iterator(); i.hasNext();) {
            final RuleSet ruleSet = (RuleSet) i.next();
            if (ruleSet.getName().equals(ruleSetName)) {
                setDefaultRuleSet(ruleSet);
                break;
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final StringBuffer buffer = new StringBuffer("RuleSets defaultRuleSet=");
        buffer.append(this.defaultRuleSet.getName());
        buffer.append(" ruleSetsList=");
        
        for (final Iterator i = this.ruleSetsList.iterator(); i.hasNext();) {
            buffer.append(i.next());
        }
        
        return buffer.toString();
    }
}
