/*
 * Created on 2 juil. 2005
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

package test;

import java.util.Set;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.core.IRuleSetsExtension;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.eclipse.core.runtime.IStatus;

/**
 * Sample of an RuleSets extension.
 * This will automatically registers our fragment rulesets into the core plugin.
 *
 * @author Herlin
 *
 */

public class RuleSetsExtension implements IRuleSetsExtension {
    private RuleSet ruleSet1;
    private RuleSet ruleSet2;

    /**
     * Replace the core plugin fragment with our own rulesets
     * @see net.sourceforge.pmd.eclipse.core.IRuleSetsExtension#registerRuleSets(java.util.Set)
     */
    public void registerRuleSets(Set<RuleSet> registeredRuleSets) {
        try {
            RuleSet ruleSet1 = getRuleSet1();
            RuleSet ruleSet2 = getRuleSet2();

            // registeredRuleSets.clear(); // to remove all rulesets already registered
            registeredRuleSets.add(ruleSet1);
            registeredRuleSets.add(ruleSet2);
        } catch (RuleSetNotFoundException e) {
            PMDPlugin.getDefault().log(IStatus.ERROR, "Unable to load rulesets", e);
        }
    }

    /**
     * Replace the default rule sets. These rule sets are the one loaded if no rule sets has been configured yet
     * (for instance when creating a new workspace)
     * @see net.sourceforge.pmd.eclipse.core.IRuleSetsExtension#registerDefaultRuleSets(java.util.Set)
     */
    public void registerDefaultRuleSets(Set<RuleSet> defaultRuleSets) {
        try {
            RuleSet ruleSet1 = getRuleSet1();
            RuleSet ruleSet2 = getRuleSet2();

            // registeredRuleSets.clear(); // to remove all rulesets already registered
            defaultRuleSets.add(ruleSet1);
            defaultRuleSets.add(ruleSet2);
        } catch (RuleSetNotFoundException e) {
            PMDPlugin.getDefault().log(IStatus.ERROR, "Unable to load rulesets", e);
        }
    }

    /**
     * Load the 1st ruleset
     * @return the 1st ruleset
     * @throws RuleSetNotFoundException
     */
    private RuleSet getRuleSet1() throws RuleSetNotFoundException {
        if (this.ruleSet1 == null) {
            RuleSetFactory factory = new RuleSetFactory();
            this.ruleSet1 = factory.createRuleSets("rulesets/extra1.xml").getAllRuleSets()[0];
        }

        return this.ruleSet1;
    }

    /**
     * Load the 2nd ruleset
     * @return the 2nd ruleset
     * @throws RuleSetNotFoundException
     */
    private RuleSet getRuleSet2() throws RuleSetNotFoundException {
        if (this.ruleSet2 == null) {
            RuleSetFactory factory = new RuleSetFactory();
            this.ruleSet2 = factory.createRuleSets("rulesets/extra2.xml").getAllRuleSets()[0];
        }

        return this.ruleSet2;
    }

}
