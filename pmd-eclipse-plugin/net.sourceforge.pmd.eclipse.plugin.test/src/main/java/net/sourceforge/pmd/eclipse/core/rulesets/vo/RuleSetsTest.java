/*
 * Created on 20 juin 2006
 * 
 * Copyright (c) 2006, PMD for Eclipse Development Team All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgement: "This product includes software developed in part by
 * support from the Defense Advanced Research Project Agency (DARPA)" *
 * Neither the name of "PMD for Eclipse Development Team" nor the names of
 * its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
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

import net.sourceforge.pmd.RuleSetNotFoundException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for RuleSets class
 * 
 * @author Herlin
 * 
 */

public class RuleSetsTest {

  /**
   * Test the defaults of a RuleSets object
   * 
   */
  @Test
  public void testDefaults() {
    final RuleSets rs = new RuleSets();
    Assert.assertNull("the default rule set should be null", rs.getDefaultRuleSet());
    Assert.assertNotNull("the rule sets list should not be null", rs.getRuleSets());
    Assert.assertEquals("The rule sets list should be empty", 0, rs.getRuleSets().size());
  }

  /**
   * 2 rule sets are equals if they are the same instance
   * 
   */
  @Test
  public void testEquals1() {
    final RuleSets rs1 = new RuleSets();
    final RuleSets rs2 = rs1;

    Assert.assertEquals("Rule sets should be equal", rs1, rs2);
  }

  /**
   * 2 rule sets are different if they are different instance
   * 
   */
  @Test
  public void testEquals2() {
    final RuleSets rs1 = new RuleSets();
    final RuleSets rs2 = new RuleSets();

    Assert.assertFalse("Rule sets should not be equal", rs1.equals(rs2));
  }

  /**
   * A rule sets should not be equals to null
   * 
   */
  @Test
  public void testEquals3() {
    final RuleSets rs1 = new RuleSets();

    Assert.assertNotNull("Rule sets should not be equal to null", rs1);
  }

  /**
   * 2 equals rule sets should have the same hash code
   * 
   */
  @Test
  public void testHashCode1() {
    final RuleSets rs1 = new RuleSets();
    final RuleSets rs2 = rs1;

    Assert.assertEquals("Equal Rule sets should have the same hash code", rs1.hashCode(), rs2.hashCode());
  }

  /**
   * 2 different rule sets should have different hash code
   * 
   */
  @Test
  public void testHashCode2() {
    final RuleSets rs1 = new RuleSets();
    final RuleSets rs2 = new RuleSets();

    Assert.assertFalse("Different rule sets should have different hash code", rs1.hashCode() == rs2.hashCode());
  }

  /**
   * Setting a null default rule set is illegal
   * 
   */
  @Test
  public void testSetDefaultRuleSet1() {
    try {
      final RuleSets rs = new RuleSets();
      rs.setDefaultRuleSet(null);
      Assert.fail("setting a default rule set to null should be illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * A default rule set should belong to the rule sets list.
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testSetDefaultRuleSet2() throws RuleSetNotFoundException {
    final Rule r1 = new Rule();
    r1.setRef("ref to a rule");
    r1.setPmdRule(TestManager.getRule(0));
    final Rule r2 = new Rule();
    r2.setRef("ref to another rule");
    r2.setPmdRule(TestManager.getRule(1));

    final Rule r3 = new Rule();
    r3.setRef("ref to a rule");
    r3.setPmdRule(TestManager.getRule(2));
    final Rule r4 = new Rule();
    r4.setRef("ref to another rule");
    r4.setPmdRule(TestManager.getRule(3));

    final RuleSet rs1 = new RuleSet();
    rs1.setName("default");
    rs1.setLanguage(RuleSet.LANGUAGE_JSP);
    rs1.addRule(r1);
    rs1.addRule(r2);

    final RuleSet rs2 = new RuleSet();
    rs2.setName("custom");
    rs2.setLanguage(RuleSet.LANGUAGE_JSP);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    try {
      final RuleSets rs = new RuleSets();
      rs.getRuleSets().add(rs1);
      rs.setDefaultRuleSet(rs2);
      Assert.fail("The default rule set should belong to the rulesets list");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Test the definition of a default rule set.
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testSetDefaultRuleSet3() throws RuleSetNotFoundException {
    final Rule r1 = new Rule();
    r1.setRef("ref to a rule");
    r1.setPmdRule(TestManager.getRule(0));
    final Rule r2 = new Rule();
    r2.setRef("ref to another rule");
    r2.setPmdRule(TestManager.getRule(1));

    final Rule r3 = new Rule();
    r3.setRef("ref to a rule");
    r3.setPmdRule(TestManager.getRule(2));
    final Rule r4 = new Rule();
    r4.setRef("ref to another rule");
    r4.setPmdRule(TestManager.getRule(3));

    final RuleSet rs1 = new RuleSet();
    rs1.setName("default");
    rs1.setLanguage(RuleSet.LANGUAGE_JSP);
    rs1.addRule(r1);
    rs1.addRule(r2);

    final RuleSet rs2 = new RuleSet();
    rs2.setName("custom");
    rs2.setLanguage(RuleSet.LANGUAGE_JSP);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    final RuleSets rs = new RuleSets();
    rs.getRuleSets().add(rs1);
    rs.getRuleSets().add(rs2);
    rs.setDefaultRuleSet(rs2);

    Assert.assertSame("The default rule set has not been defined!", rs2, rs.getDefaultRuleSet());
  }

  /**
   * Test the basic usage of the default ruleset setter
   * 
   */
  @Test
  public void testSetDefaultRuleSetName() throws RuleSetNotFoundException {
    final Rule r1 = new Rule();
    r1.setRef("ref to a rule");
    r1.setPmdRule(TestManager.getRule(0));

    final Rule r2 = new Rule();
    r2.setRef("ref to another rule");
    r2.setPmdRule(TestManager.getRule(1));

    final RuleSet rs1 = new RuleSet();
    rs1.setName("default");
    rs1.setLanguage(RuleSet.LANGUAGE_JSP);
    rs1.addRule(r1);
    rs1.addRule(r2);

    final List ruleSetsList = new ArrayList();
    ruleSetsList.add(rs1);

    final RuleSets ruleSets = new RuleSets();
    ruleSets.setRuleSets(ruleSetsList);

    ruleSets.setDefaultRuleSetName("default");

    Assert.assertSame("The default ruleset has not been set correctly", rs1, ruleSets.getDefaultRuleSet());
  }

  /**
   * Test setting a default ruleset name to null
   * 
   */
  @Test
  public void testSetDefaultRuleSetNameNull() {
    try {
      final RuleSets ruleSets = new RuleSets();
      ruleSets.setDefaultRuleSetName(null);
      Assert.fail("Setting a default ruleset name to null is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting a null rule sets list is not allowed
   * 
   */
  @Test
  public void testSetRuleSets1() {
    try {
      final RuleSets rs = new RuleSets();
      rs.setRuleSets(null);
      Assert.fail("Setting a null rule sets is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting an empty rule sets list is not allowed
   * 
   */
  @Test
  public void testSetRuleSets2() {
    try {
      final RuleSets rs = new RuleSets();
      rs.setRuleSets(new ArrayList());
      Assert.fail("Setting an empty rule sets is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting any non empty list is OK. We do not validate the element type
   * for now.
   * 
   */
  @Test
  public void testSetRuleSets3() {
    final List l = new ArrayList();
    l.add("a ruleset");
    final RuleSets rs = new RuleSets();
    rs.setRuleSets(l);

    Assert.assertSame("The rule set list has not been set", l, rs.getRuleSets());
  }
}
