/*
 * Created on 7 juin 2005
 * 
 * Copyright (c) 2005, PMD for Eclipse Development Team All rights reserved.
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
package net.sourceforge.pmd.eclipse.core;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.core.impl.RuleSetManagerImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the ruleset manager.
 * 
 * @author Philippe Herlin
 * 
 */
public class RuleSetManagerTest {
  private IRuleSetManager ruleSetManager;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Before
  public void setUp() throws Exception {
    this.ruleSetManager = new RuleSetManagerImpl();
  }

  /**
   * Registering twice the same rule set results in no addition
   * 
   */
  @Test
  public void testDuplicateRegister() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerRuleSet(ruleSet);
    this.ruleSetManager.registerRuleSet(ruleSet);
    Assert.assertEquals("Only one rule set should have been registered", 1, this.ruleSetManager.getRegisteredRuleSets().size());
  }

  /**
   * Registering twice the same default rule set results in no addition
   * 
   */
  @Test
  public void testDuplicateRegisterDefault() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerDefaultRuleSet(ruleSet);
    this.ruleSetManager.registerDefaultRuleSet(ruleSet);
    Assert.assertEquals("Only one rule set should have been registered", 1, this.ruleSetManager.getDefaultRuleSets().size());
  }

  /**
   * Unregistering twice the same rule set has no effect
   * 
   */
  @Test
  public void testDuplicateUnregister() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerRuleSet(ruleSet);

    this.ruleSetManager.unregisterRuleSet(ruleSet);
    this.ruleSetManager.unregisterRuleSet(ruleSet);
    Assert.assertEquals("RuleSet not unregistered", 0, this.ruleSetManager.getRegisteredRuleSets().size());
  }

  /**
   * Unregistering twice the same Default rule set has no effect
   * 
   */
  @Test
  public void testDuplicateUnregisterDefault() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerRuleSet(ruleSet);

    this.ruleSetManager.unregisterDefaultRuleSet(ruleSet);
    this.ruleSetManager.unregisterDefaultRuleSet(ruleSet);
    Assert.assertEquals("Default RuleSet not unregistered", 0, this.ruleSetManager.getDefaultRuleSets().size());
  }

  /**
   * Test the register default ruleset
   * 
   */
  @Test
  public void testRegisterDefaultRuleSet() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerDefaultRuleSet(ruleSet);
    Assert.assertEquals("Default RuleSet not registrered!", 1, this.ruleSetManager.getDefaultRuleSets().size());
  }

  /**
   * Test the registration of a null ruleset
   * 
   */
  @Test
  public void testRegisterNullDefaultRuleSet() {
    try {
      this.ruleSetManager.registerDefaultRuleSet(null);
      Assert.fail("Should return an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      ; // cool
    }
  }

  /**
   * Test the registration of a null ruleset
   * 
   */
  @Test
  public void testRegisterNullRuleSet() {
    try {
      this.ruleSetManager.registerRuleSet(null);
      Assert.fail("Should return an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      ; // cool
    }
  }

  /**
   * Test the register ruleset
   * 
   */
  @Test
  public void testRegisterRuleSet() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerRuleSet(ruleSet);
    Assert.assertEquals("RuleSet not registrered!", 1, this.ruleSetManager.getRegisteredRuleSets().size());
  }

  /**
   * Test unregistration default
   * 
   */
  @Test
  public void testUnregisterDefaultRuleSet() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerDefaultRuleSet(ruleSet);
    Assert.assertEquals("Default RuleSet not registered!", 1, this.ruleSetManager.getDefaultRuleSets().size());

    this.ruleSetManager.unregisterDefaultRuleSet(ruleSet);
    Assert.assertEquals("Default RuleSet not unregistered", 0, this.ruleSetManager.getDefaultRuleSets().size());
  }

  /**
   * Unregistering a null default ruleset is illegal
   * 
   */
  @Test
  public void testUnregisterNullDefaultRuleSet() {
    try {
      this.ruleSetManager.unregisterDefaultRuleSet(null);
      Assert.fail("An IllegalArgumentException should be returned");
    }
    catch (final RuntimeException e) {
      ; // cool
    }
  }

  /**
   * Unregistering a null ruleset is illegal
   * 
   */
  @Test
  public void testUnregisterNullRuleSet() {
    try {
      this.ruleSetManager.unregisterRuleSet(null);
      Assert.fail("An IllegalArgumentException should be returned");
    }
    catch (final RuntimeException e) {
      ; // cool
    }
  }

  /**
   * Test unregistration
   * 
   */
  @Test
  public void testUnregisterRuleSet() {
    final RuleSet ruleSet = new RuleSet();
    this.ruleSetManager.registerRuleSet(ruleSet);
    Assert.assertEquals("RuleSet not registered!", 1, this.ruleSetManager.getRegisteredRuleSets().size());

    this.ruleSetManager.unregisterRuleSet(ruleSet);
    Assert.assertEquals("RuleSet not unregistered", 0, this.ruleSetManager.getRegisteredRuleSets().size());
  }
}
