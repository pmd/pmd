/*
 * Created on 22 juin 2006
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

package net.sourceforge.pmd.core.rulesets.impl;

import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.core.rulesets.IRuleSetsManager;
import net.sourceforge.pmd.core.rulesets.vo.RuleSet;
import junit.framework.TestCase;

/**
 * RuleSetsManager unit tests
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/21 23:06:54  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 * 
 */

public class RuleSetsManagerImplTest extends TestCase {

    /**
     * Test the valueOf method in its expected usage.
     * 
     * @throws RuleSetNotFoundException
     */
    public void testValueOf1() throws RuleSetNotFoundException {
        IRuleSetsManager rsm = new RuleSetsManagerImpl();
        RuleSet ruleSet = rsm.valueOf(new String[] { "rulesets/basic.xml" });

        Collection pmdRules = ruleSet.getPmdRuleSet().getRules();
        Collection basicRules = new RuleSetFactory().createSingleRuleSet("rulesets/basic.xml").getRules();

        // dump("PMD Rules", pmdRules);
        // dump("Basic Rules", basicRules);

        assertTrue("All the basic rules have not been loaded !", pmdRules.containsAll(basicRules));
        assertTrue("All the loaded rules are not in the basic rules!", basicRules.containsAll(pmdRules));
    }

    /**
     * Passing a null array to valueOf is not allowed
     * 
     * @throws RuleSetNotFoundException
     * 
     */
    public void testValueOf2() throws RuleSetNotFoundException {
        try {
            IRuleSetsManager rsm = new RuleSetsManagerImpl();
            RuleSet ruleSet = rsm.valueOf(null);
            fail("Getting a rule set from a null array is not allowed");
        } catch (IllegalArgumentException e) {
            // Sucess
        }
    }

    /**
     * Passing an empty array to valueOf is not allowed
     * 
     * @throws RuleSetNotFoundException
     * 
     */
    public void testValueOf3() throws RuleSetNotFoundException {
        try {
            IRuleSetsManager rsm = new RuleSetsManagerImpl();
            RuleSet ruleSet = rsm.valueOf(new String[] {});
            fail("Getting a rule set from an empty array is not allowed");
        } catch (IllegalArgumentException e) {
            // Sucess
        }
    }

    /**
     * Dump a collection of rules
     * 
     * @param message
     * @param rules
     */
    private void dump(String message, Collection rules) {
        System.out.println("Dump " + message);
        for (Iterator i = rules.iterator(); i.hasNext();) {
            Rule rule = (Rule) i.next();
            System.out.println(rule.getName());
        }
        System.out.println();
    }
}
