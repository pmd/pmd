/*
 * Created on 18 juin 2006
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

import net.sourceforge.pmd.RuleSetNotFoundException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the RuleSet class
 * 
 * @author Herlin
 * 
 */

public class RuleSetTest {

  /**
   * Adding a rule.
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testAddRule1() throws RuleSetNotFoundException {
    try {
      final RuleSet rs = new RuleSet();
      final Rule rule = new Rule();
      rule.setPmdRule(TestManager.getRule(0));
      rs.addRule(rule);
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("adding any rule object is legal !");
    }
  }

  /**
   * Adding a null rule is illegal.
   * 
   */
  @Test
  public void testAddRule2() {
    try {
      final RuleSet rs = new RuleSet();
      rs.addRule(null);
      Assert.fail("Adding a null rule is illegal !");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Test the default attributes of a rule set
   * 
   */
  @Test
  public void testDefaults() {
    final RuleSet rs = new RuleSet();

    Assert.assertNotNull("Name should not be null", rs.getName());
    Assert.assertNotNull("Description should not be null", rs.getDescription());
    Assert.assertNotNull("Rules should not be null", rs.getRules());
    Assert.assertNotNull("Language should not be null", rs.getLanguage());

    Assert.assertEquals("Name should be empty", 0, rs.getName().length());
    Assert.assertEquals("Description should be empty", 0, rs.getDescription().length());
    Assert.assertEquals("Rules collecction should be empty", 0, rs.getRules().size());
    Assert.assertEquals("Default language should be set to Java", RuleSet.LANGUAGE_JAVA, rs.getLanguage());
  }

  /**
   * An instance of a rule set is equals to itself
   * 
   */
  @Test
  public void testEquals1() {
    final RuleSet rs = new RuleSet();
    rs.setName("default");
    Assert.assertEquals("A ruleset is equal to itself", rs, rs);
  }

  /**
   * A rule set is never equal to null
   * 
   */
  @Test
  public void testEquals2() {
    final RuleSet rs = new RuleSet();
    rs.setName("default");
    Assert.assertNotNull("A ruleset is never equal to null", rs);
  }

  /**
   * RuleSets are equal if they have the same name, language and rules
   * collection whatever the description.
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testEquals3() throws RuleSetNotFoundException {
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
    rs2.setName("default");
    rs2.setLanguage(RuleSet.LANGUAGE_JSP);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    Assert.assertEquals("These rule sets should be equals", rs1, rs2);
  }

  /**
   * Rule sets with different names are different
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testEquals4() throws RuleSetNotFoundException {
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

    Assert.assertFalse("Rule sets with different names are different", rs1.equals(rs2));
  }

  /**
   * Rule sets with different languages are different
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testEquals5() throws RuleSetNotFoundException {
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
    rs2.setName("default");
    rs2.setLanguage(RuleSet.LANGUAGE_JAVA);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    Assert.assertFalse("Rule sets with different languages are different", rs1.equals(rs2));
  }

  /**
   * Rule sets with different rules collection are different
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testEquals7() throws RuleSetNotFoundException {
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
    r4.setRef("ref to yet another rule");
    r4.setPmdRule(TestManager.getRule(3));

    final RuleSet rs1 = new RuleSet();
    rs1.setName("default");
    rs1.setLanguage(RuleSet.LANGUAGE_JSP);
    rs1.addRule(r1);
    rs1.addRule(r2);

    final RuleSet rs2 = new RuleSet();
    rs2.setName("default");
    rs2.setLanguage(RuleSet.LANGUAGE_JSP);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    Assert.assertFalse("Rule sets with different rules collections are different", rs1.equals(rs2));
  }

  /**
   * The PMD Rule Set should contain the we have added
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testGetPmdRuleSet() throws RuleSetNotFoundException {
    final Rule rule = new Rule();
    rule.setPmdRule(TestManager.getRule(0));

    final RuleSet ruleSet = new RuleSet();
    ruleSet.addRule(rule);

    Assert.assertNotNull("The PMD Rule Set should not be null", ruleSet.getPmdRuleSet());
    Assert.assertTrue("The added rule set should be present in the PMD rule set",
        ruleSet.getPmdRuleSet().getRules().contains(TestManager.getRule(0)));
  }

  /**
   * Equal rule sets must have the same hash code
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testHashCode1() throws RuleSetNotFoundException {
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
    rs2.setName("default");
    rs2.setLanguage(RuleSet.LANGUAGE_JSP);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    Assert.assertEquals("Equal rule sets must have the same hash code", rs1.hashCode(), rs2.hashCode());
  }

  /**
   * Different rule sets should have different hash codes
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testHashCode2() throws RuleSetNotFoundException {
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

    Assert.assertFalse("Different rule sets should have different hash codes", rs1.hashCode() == rs2.hashCode());
  }

  /**
   * Different rule sets should have different hash codes
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testHashCode3() throws RuleSetNotFoundException {
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
    rs2.setName("default");
    rs2.setLanguage(RuleSet.LANGUAGE_JAVA);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    Assert.assertFalse("Different rule sets should have different hash codes", rs1.hashCode() == rs2.hashCode());
  }

  /**
   * Different rule sets should have different hash codes
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testHashCode4() throws RuleSetNotFoundException {
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
    r4.setRef("ref to yet another rule");
    r4.setPmdRule(TestManager.getRule(3));

    final RuleSet rs1 = new RuleSet();
    rs1.setName("default");
    rs1.setLanguage(RuleSet.LANGUAGE_JSP);
    rs1.addRule(r1);
    rs1.addRule(r2);

    final RuleSet rs2 = new RuleSet();
    rs2.setName("default");
    rs2.setLanguage(RuleSet.LANGUAGE_JSP);
    rs2.addRule(r3);
    rs2.addRule(r4);
    rs2.setDescription("Description does not make the difference");

    Assert.assertFalse("Different rule sets should have different hash codes", rs1.hashCode() == rs2.hashCode());
  }

  /**
   * Setting any description is legal
   * 
   */
  @Test
  public void testSetDescription1() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setDescription("any description");
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("setting description to any string is legal");
    }
  }

  /**
   * Setting description to null is illegal
   * 
   */
  @Test
  public void testSetDescription2() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setDescription(null);
      Assert.fail("setting description to null is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting Description to empty string is legal
   * 
   */
  @Test
  public void testSetDescription3() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setDescription("");
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("setting description to empty string is legal");
    }
  }

  /**
   * Setting Description to empty string is legal
   * 
   */
  @Test
  public void testSetDescription4() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setDescription(" \t\n");
      Assert.assertEquals("The string should not be modified", " \t\n", rs.getDescription());
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("setting description to empty string is legal");
    }
  }

  /**
   * Language could be assigned the JAVA value
   * 
   */
  @Test
  public void testSetLanguage1() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setLanguage("JAVA");
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("setting language to JAVA is legal");
    }
  }

  /**
   * Language could be assigned the JSP value
   * 
   */
  @Test
  public void testSetLanguage2() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setLanguage("JSP");
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("setting language to JSP is legal");
    }
  }

  /**
   * Setting language to null is illegal
   * 
   */
  @Test
  public void testSetLanguage3() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setLanguage(null);
      Assert.fail("setting language to null is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting language to empty string is illegal
   * 
   */
  @Test
  public void testSetLanguage4() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setLanguage("");
      Assert.fail("setting language to empty string is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting language to empty string is illegal
   * 
   */
  @Test
  public void testSetLanguage5() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setLanguage(" \t\n");
      Assert.fail("setting language to empty string is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting language to any string is illegal
   * 
   */
  @Test
  public void testSetLanguage6() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setLanguage("any string");
      Assert.fail("setting language to any string is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting any name is legal
   * 
   */
  @Test
  public void testSetName1() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setName("any name");
    }
    catch (final IllegalArgumentException e) {
      Assert.fail("setting name to any name is legal");
    }
  }

  /**
   * Setting name to null is illegal
   * 
   */
  @Test
  public void testSetName2() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setName(null);
      Assert.fail("setting name to null is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting name to empty string is illegal
   * 
   */
  @Test
  public void testSetName3() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setName("");
      Assert.fail("setting name to empty string is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }

  /**
   * Setting name to empty string is illegal
   * 
   */
  @Test
  public void testSetName4() {
    try {
      final RuleSet rs = new RuleSet();
      rs.setName(" \t\n");
      Assert.fail("setting name to empty string is illegal");
    }
    catch (final IllegalArgumentException e) {
      // success
    }
  }
}
