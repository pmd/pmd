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
import java.util.List;

import net.sourceforge.pmd.util.StringUtil;

/**
 * This class is a value object that is the root of the structure of a rulesets
 * object. It holds the different configurations the user may define and use in
 * each project.
 *
 * @author Herlin
 *
 */

public class RuleSets {
    private RuleSet defaultRuleSet;
    private List<RuleSet> ruleSetsList = new ArrayList<RuleSet>();

    /**
     * Getter for the defaultRuleSet attribute. The default rule set is the one
     * loaded by the "getRuleSet(void)" operation from the preferences manager.
     * Also, the default rule set is the one selected on each new Java project.
     *
     * @return Returns the defaultRuleSet.
     */
    public RuleSet getDefaultRuleSet() {
        return defaultRuleSet;
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
        if (!ruleSetsList.contains(defaultRuleSet)) {
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
    public List<RuleSet> getRuleSets() {
        return ruleSetsList;
    }

    /**
     * Setter of the rule sets list attribute.
     *
     * @param ruleSetsSet The ruleSetsSet to set.
     */
    public void setRuleSets(List<RuleSet> ruleSets) {
        if (ruleSets == null || ruleSets.isEmpty()) {
            throw new IllegalArgumentException("The rule set list should not be null or empty");

        }
        this.ruleSetsList = ruleSets;
    }

    /**
     * Return the name of the default ruleset
     * @return the name of the default ruleset
     */
    public String getDefaultRuleSetName() {
        return defaultRuleSet.getName();
    }

    /**
     * Sets the default ruleset by its name. If the ruleset does not exist,
     * the default ruleset is not set.
     * @param ruleSetName a name of an already defined ruleset.
     */
    public void setDefaultRuleSetName(String ruleSetName) {
        if (StringUtil.isEmpty(ruleSetName)) {
            throw new IllegalArgumentException("The default ruleset name must not be null or empty");
        }

        for (RuleSet ruleSet2 : ruleSetsList) {
            RuleSet ruleSet = ruleSet2;
            if (ruleSet.getName().equals(ruleSetName)) {
                setDefaultRuleSet(ruleSet);
                break;
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("RuleSets defaultRuleSet=");
        buffer.append(defaultRuleSet.getName());
        buffer.append(" ruleSetsList=");

        for (RuleSet ruleSet : ruleSetsList) {
            buffer.append(ruleSet);
        }

        return buffer.toString();
    }
}
