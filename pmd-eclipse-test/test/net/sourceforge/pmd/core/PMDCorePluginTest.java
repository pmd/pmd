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
package net.sourceforge.pmd.core;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.core.PMDCorePlugin;
import net.sourceforge.pmd.core.PluginConstants;

/**
 * Test the PMD Core plugin
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/18 22:29:52  phherlin
 * Begin refactoring the unit tests for the plugin
 * Revision 1.2 2005/07/02 14:32:01 phherlin
 * Implement the RuleSets extension points new tests
 * 
 * Revision 1.1 2005/06/15 21:14:56 phherlin Create the project for the Eclipse
 * plugin unit tests
 * 
 * 
 */
public class PMDCorePluginTest extends TestCase {

    /**
     * Constructor for PMDCorePluginTest.
     * 
     * @param name
     */
    public PMDCorePluginTest(String name) {
        super(name);
    }

    /**
     * Test that the core plugin has been instantiated
     * 
     */
    public void testPMDCorePluginNotNull() {
        assertNotNull("The Core Plugin has not been instantiated", PMDCorePlugin.getDefault());
    }

    /**
     * Test that we can get a ruleset manager
     * 
     */
    public void testRuleSetManagerNotNull() {
        assertNotNull("Cannot get a ruleset manager", PMDCorePlugin.getDefault().getRuleSetManager());
    }

    /**
     * Test all the known PMD rulesets has been registered For this test to
     * work, no fragement or only the test plugin fragment should be installed.
     * 
     */
    public void testStandardPMDRuleSetsRegistered() throws RuleSetNotFoundException {
        Set registeredRuleSets = PMDCorePlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
        assertFalse("No registered rulesets!", registeredRuleSets.isEmpty());

        RuleSetFactory factory = new RuleSetFactory();
        for (int i = 0; i < PluginConstants.PMD_RULESETS.length; i++) {
            RuleSet ruleSet = factory.createSingleRuleSet(PluginConstants.PMD_RULESETS[i]);
            assertTrue("RuleSet \"" + PluginConstants.PMD_RULESETS[i] + "\" has not been registered", ruleSetRegistered(ruleSet,
                    registeredRuleSets));
        }
    }

    /**
     * Test the default rulesets has been registered For this test to work, no
     * fragement or only the test plugin fragment should be installed.
     * 
     */
    public void testDefaultPMDRuleSetsRegistered() throws RuleSetNotFoundException {
        Set defaultRuleSets = PMDCorePlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
        assertFalse("No registered default rulesets!", defaultRuleSets.isEmpty());

        RuleSetFactory factory = new RuleSetFactory();
        for (int i = 0; i < PluginConstants.PMD_RULESETS.length; i++) {
            RuleSet ruleSet = factory.createSingleRuleSet(PluginConstants.PMD_RULESETS[i]);
            assertTrue("RuleSet \"" + PluginConstants.PMD_RULESETS[i] + "\" has not been registered", ruleSetRegistered(ruleSet,
                    defaultRuleSets));
        }
    }

    /**
     * test if a ruleset is registered
     * 
     * @param ruleSet
     * @param set
     * @return true if ok
     */
    private boolean ruleSetRegistered(RuleSet ruleSet, Set set) {
        boolean registered = false;

        Iterator i = set.iterator();
        while (i.hasNext() && !registered) {
            RuleSet registeredRuleSet = (RuleSet) i.next();
            registered = registeredRuleSet.getName().equals(ruleSet.getName());
        }

        return registered;
    }
}
