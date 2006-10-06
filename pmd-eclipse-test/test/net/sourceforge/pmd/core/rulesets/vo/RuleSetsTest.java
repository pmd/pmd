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

package net.sourceforge.pmd.core.rulesets.vo;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.RuleSetNotFoundException;

import junit.framework.TestCase;

/**
 * Unit tests for RuleSets class
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/06 16:42:03  phherlin
 * Continue refactoring of rullesets management
 *
 * Revision 1.1  2006/06/21 23:06:52  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 * Revision 1.1  2006/06/20 22:04:18  phherlin
 * Implement the last object of the rulesets structure
 *
 * 
 */

public class RuleSetsTest extends TestCase {

    /**
     * Test the defaults of a RuleSets object
     * 
     */
    public void testDefaults() {
        RuleSets rs = new RuleSets();
        assertNull("the default rule set should be null", rs.getDefaultRuleSet());
        assertNotNull("the rule sets list should not be null", rs.getRuleSets());
        assertEquals("The rule sets list should be empty", 0, rs.getRuleSets().size());
    }
    
    /**
     * Setting a null rule sets list is not allowed
     *
     */
    public void testSetRuleSets1() {
        try {
            RuleSets rs = new RuleSets();
            rs.setRuleSets(null);
            fail("Setting a null rule sets is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
    
    /**
     * Setting an empty rule sets list is not allowed
     *
     */
    public void testSetRuleSets2() {
        try {
            RuleSets rs = new RuleSets();
            rs.setRuleSets(new ArrayList());
            fail("Setting an empty rule sets is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
    
    /**
     * Setting any non empty list is OK.
     * We do not validate the element type for now.
     *
     */
    public void testSetRuleSets3() {
        List l = new ArrayList();
        l.add("a ruleset");
        RuleSets rs = new RuleSets();
        rs.setRuleSets(l);
        
        assertSame("The rule set list has not been set", l, rs.getRuleSets());
    }

    /**
     * Setting a null default rule set is illegal
     * 
     */
    public void testSetDefaultRuleSet1() {
        try {
            RuleSets rs = new RuleSets();
            rs.setDefaultRuleSet(null);
            fail("setting a default rule set to null should be illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * A default rule set should belong to the rule sets list.
     * @throws RuleSetNotFoundException 
     * 
     */
    public void testSetDefaultRuleSet2() throws RuleSetNotFoundException {
        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setPmdRule(TestManager.getRule(0));
        Rule r2 = new Rule();
        r2.setRef("ref to another rule");
        r2.setPmdRule(TestManager.getRule(1));

        Rule r3 = new Rule();
        r3.setRef("ref to a rule");
        r3.setPmdRule(TestManager.getRule(2));
        Rule r4 = new Rule();
        r4.setRef("ref to another rule");
        r4.setPmdRule(TestManager.getRule(3));

        RuleSet rs1 = new RuleSet();
        rs1.setName("default");
        rs1.setLanguage(RuleSet.LANGUAGE_JSP);
        rs1.addRule(r1);
        rs1.addRule(r2);

        RuleSet rs2 = new RuleSet();
        rs2.setName("custom");
        rs2.setLanguage(RuleSet.LANGUAGE_JSP);
        rs2.addRule(r3);
        rs2.addRule(r4);
        rs2.setDescription("Description does not make the difference");

        try {
            RuleSets rs = new RuleSets();
            rs.getRuleSets().add(rs1);
            rs.setDefaultRuleSet(rs2);
            fail("The default rule set should belong to the rulesets list");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * Test the definition of a default rule set.
     * @throws RuleSetNotFoundException 
     * 
     */
    public void testSetDefaultRuleSet3() throws RuleSetNotFoundException {
        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setPmdRule(TestManager.getRule(0));
        Rule r2 = new Rule();
        r2.setRef("ref to another rule");
        r2.setPmdRule(TestManager.getRule(1));

        Rule r3 = new Rule();
        r3.setRef("ref to a rule");
        r3.setPmdRule(TestManager.getRule(2));
        Rule r4 = new Rule();
        r4.setRef("ref to another rule");
        r4.setPmdRule(TestManager.getRule(3));

        RuleSet rs1 = new RuleSet();
        rs1.setName("default");
        rs1.setLanguage(RuleSet.LANGUAGE_JSP);
        rs1.addRule(r1);
        rs1.addRule(r2);

        RuleSet rs2 = new RuleSet();
        rs2.setName("custom");
        rs2.setLanguage(RuleSet.LANGUAGE_JSP);
        rs2.addRule(r3);
        rs2.addRule(r4);
        rs2.setDescription("Description does not make the difference");

        RuleSets rs = new RuleSets();
        rs.getRuleSets().add(rs1);
        rs.getRuleSets().add(rs2);
        rs.setDefaultRuleSet(rs2);
        
        assertSame("The default rule set has not been defined!", rs2, rs.getDefaultRuleSet());
    }
    
    /**
     * 2 rule sets are equals if they are the same instance
     *
     */
    public void testEquals1() {
        RuleSets rs1 = new RuleSets();
        RuleSets rs2 = rs1;
        
        assertEquals("Rule sets should be equal", rs1, rs2);
    }
    
    /**
     * 2 rule sets are different if they are different instance
     *
     */
    public void testEquals2() {
        RuleSets rs1 = new RuleSets();
        RuleSets rs2 = new RuleSets();
        
        assertFalse("Rule sets should not be equal", rs1.equals(rs2));
    }
    
    /**
     * A rule sets should not be equals to null
     *
     */
    public void testEquals3() {
        RuleSets rs1 = new RuleSets();
        
        assertFalse("Rule sets should not be equal to null", rs1.equals(null));
    }
    
    /**
     * 2 equals rule sets should have the same hash code
     *
     */
    public void testHashCode1() {
        RuleSets rs1 = new RuleSets();
        RuleSets rs2 = rs1;
        
        assertEquals("Equal Rule sets should have the same hash code", rs1.hashCode(), rs2.hashCode());
    }
    
    /**
     * 2 different rule sets should have different hash code
     *
     */
    public void testHashCode2() {
        RuleSets rs1 = new RuleSets();
        RuleSets rs2 = new RuleSets();
        
        assertFalse("Different rule sets should have different hash code", rs1.hashCode() == rs2.hashCode());
    }
    
    /**
     * Test the basic usage of the default ruleset setter
     *
     */
    public void testSetDefaultRuleSetName() throws RuleSetNotFoundException {
        Rule r1 = new Rule();
        r1.setRef("ref to a rule");
        r1.setPmdRule(TestManager.getRule(0));

        Rule r2 = new Rule();
        r2.setRef("ref to another rule");
        r2.setPmdRule(TestManager.getRule(1));

        RuleSet rs1 = new RuleSet();
        rs1.setName("default");
        rs1.setLanguage(RuleSet.LANGUAGE_JSP);
        rs1.addRule(r1);
        rs1.addRule(r2);
        
        List ruleSetsList = new ArrayList();
        ruleSetsList.add(rs1);
        
        RuleSets ruleSets = new RuleSets();
        ruleSets.setRuleSets(ruleSetsList);
        
        ruleSets.setDefaultRuleSetName("default");
        
        assertSame("The default ruleset has not been set correctly", rs1, ruleSets.getDefaultRuleSet());
    }
    
    /**
     * Test setting a default ruleset name to null
     *
     */
    public void testSetDefaultRuleSetNameNull() {
        try {
            RuleSets ruleSets = new RuleSets();
            ruleSets.setDefaultRuleSetName(null);
            fail("Setting a default ruleset name to null is illegal");
        } catch (IllegalArgumentException e) {
            // success
        }
    }
}
