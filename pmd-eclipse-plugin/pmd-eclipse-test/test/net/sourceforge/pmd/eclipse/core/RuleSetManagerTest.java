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

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.core.IRuleSetManager;
import net.sourceforge.pmd.eclipse.core.impl.RuleSetManagerImpl;
import junit.framework.TestCase;

/**
 * Test the ruleset manager.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/18 22:29:51  phherlin
 * Begin refactoring the unit tests for the plugin
 *
 * Revision 1.2  2005/07/02 14:32:01  phherlin
 * Implement the RuleSets extension points new tests
 *
 * Revision 1.1  2005/06/15 21:14:56  phherlin
 * Create the project for the Eclipse plugin unit tests
 *
 *
 */
public class RuleSetManagerTest extends TestCase {
    private IRuleSetManager ruleSetManager;

    /**
     * Default constructor
     * @param name
     */
    public RuleSetManagerTest(String name) {
        super(name);
    }
    
    /**
     * Test the register ruleset
     *
     */
    public void testRegisterRuleSet() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerRuleSet(ruleSet);
        assertEquals("RuleSet not registrered!", 1, this.ruleSetManager.getRegisteredRuleSets().size());
    }
    
    /**
     * Test the registration of a null ruleset
     *
     */
    public void testRegisterNullRuleSet() {
        try {
            this.ruleSetManager.registerRuleSet(null);
            fail("Should return an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            ; // cool
        }
    }
    
    /**
     * Registering twice the same rule set results in no addition
     *
     */
    public void testDuplicateRegister() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerRuleSet(ruleSet);
        this.ruleSetManager.registerRuleSet(ruleSet);
        assertEquals("Only one rule set should have been registered", 1, this.ruleSetManager.getRegisteredRuleSets().size());
    }
    
    /**
     * Test unregistration
     *
     */
    public void testUnregisterRuleSet() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerRuleSet(ruleSet);
        assertEquals("RuleSet not registered!", 1, this.ruleSetManager.getRegisteredRuleSets().size());

        this.ruleSetManager.unregisterRuleSet(ruleSet);
        assertEquals("RuleSet not unregistered", 0, this.ruleSetManager.getRegisteredRuleSets().size());
    }
    
    /**
     * Unregistering a null ruleset is illegal
     *
     */
    public void testUnregisterNullRuleSet() {
        try {
            this.ruleSetManager.unregisterRuleSet(null);
            fail("An IllegalArgumentException should be returned");
        } catch (RuntimeException e) {
            ; // cool
        }
    }
    
    /**
     * Unregistering twice the same rule set has no effect
     *
     */
    public void testDuplicateUnregister() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerRuleSet(ruleSet);

        this.ruleSetManager.unregisterRuleSet(ruleSet);
        this.ruleSetManager.unregisterRuleSet(ruleSet);
        assertEquals("RuleSet not unregistered", 0, this.ruleSetManager.getRegisteredRuleSets().size());
    }
    
    /**
     * Test the register default ruleset
     *
     */
    public void testRegisterDefaultRuleSet() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerDefaultRuleSet(ruleSet);
        assertEquals("Default RuleSet not registrered!", 1, this.ruleSetManager.getDefaultRuleSets().size());
    }
    
    /**
     * Test the registration of a null ruleset
     *
     */
    public void testRegisterNullDefaultRuleSet() {
        try {
            this.ruleSetManager.registerDefaultRuleSet(null);
            fail("Should return an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            ; // cool
        }
    }
    
    /**
     * Registering twice the same default rule set results in no addition
     *
     */
    public void testDuplicateRegisterDefault() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerDefaultRuleSet(ruleSet);
        this.ruleSetManager.registerDefaultRuleSet(ruleSet);
        assertEquals("Only one rule set should have been registered", 1, this.ruleSetManager.getDefaultRuleSets().size());
    }
    
    /**
     * Test unregistration default
     *
     */
    public void testUnregisterDefaultRuleSet() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerDefaultRuleSet(ruleSet);
        assertEquals("Default RuleSet not registered!", 1, this.ruleSetManager.getDefaultRuleSets().size());

        this.ruleSetManager.unregisterDefaultRuleSet(ruleSet);
        assertEquals("Default RuleSet not unregistered", 0, this.ruleSetManager.getDefaultRuleSets().size());
    }
    
    /**
     * Unregistering a null default ruleset is illegal
     *
     */
    public void testUnregisterNullDefaultRuleSet() {
        try {
            this.ruleSetManager.unregisterDefaultRuleSet(null);
            fail("An IllegalArgumentException should be returned");
        } catch (RuntimeException e) {
            ; // cool
        }
    }
    
    /**
     * Unregistering twice the same Default rule set has no effect
     *
     */
    public void testDuplicateUnregisterDefault() {
        RuleSet ruleSet = new RuleSet();
        this.ruleSetManager.registerRuleSet(ruleSet);

        this.ruleSetManager.unregisterDefaultRuleSet(ruleSet);
        this.ruleSetManager.unregisterDefaultRuleSet(ruleSet);
        assertEquals("Default RuleSet not unregistered", 0, this.ruleSetManager.getDefaultRuleSets().size());
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.ruleSetManager = new RuleSetManagerImpl();
    }
}
