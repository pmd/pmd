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

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test the PMD Core plugin
 * 
 * @author Philippe Herlin
 * 
 */
@Ignore("Needs a OSGI context")
public class PMDCorePluginTest {

  /**
   * Test the default rulesets has been registered For this test to work, no
   * Fragment or only the test plugin fragment should be installed.
   * 
   */
  @Test
  public void testDefaultPMDRuleSetsRegistered() throws RuleSetNotFoundException {
    final Set<RuleSet> defaultRuleSets = PMDPlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
    Assert.assertFalse("No registered default rulesets!", defaultRuleSets.isEmpty());

    final RuleSetFactory factory = new RuleSetFactory();
    final Iterator<RuleSet> iterator = factory.getRegisteredRuleSets();
    while (iterator.hasNext()) {
      final RuleSet ruleSet = iterator.next();
      Assert.assertTrue("RuleSet \"" + ruleSet.getName() + "\" has not been registered", ruleSetRegistered(ruleSet, defaultRuleSets));
    }
  }

  /**
   * Test that the core plugin has been instantiated
   * 
   */
  @Test
  public void testPMDPluginNotNull() {
    Assert.assertNotNull("The Core Plugin has not been instantiated", PMDPlugin.getDefault());
  }

  /**
   * Test that we can get a ruleset manager
   * 
   */
  @Test
  public void testRuleSetManagerNotNull() {
    Assert.assertNotNull("Cannot get a ruleset manager", PMDPlugin.getDefault().getRuleSetManager());
  }

  /**
   * Test all the known PMD rulesets has been registered For this test to
   * work, no fragment or only the test plugin fragment should be installed.
   * 
   */
  @Test
  public void testStandardPMDRuleSetsRegistered() throws RuleSetNotFoundException {
    final Set<RuleSet> registeredRuleSets = PMDPlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
    Assert.assertFalse("No registered rulesets!", registeredRuleSets.isEmpty());

    final RuleSetFactory factory = new RuleSetFactory();
    final Iterator<RuleSet> iterator = factory.getRegisteredRuleSets();
    while (iterator.hasNext()) {
      final RuleSet ruleSet = iterator.next();
      Assert.assertTrue("RuleSet \"" + ruleSet.getName() + "\" has not been registered", ruleSetRegistered(ruleSet, registeredRuleSets));
    }
  }

  /**
   * test if a ruleset is registered
   * 
   * @param ruleSet
   * @param set
   * @return true if OK
   */
  private boolean ruleSetRegistered(final RuleSet ruleSet, final Set<RuleSet> set) {
    boolean registered = false;

    final Iterator<RuleSet> i = set.iterator();
    while (i.hasNext() && !registered) {
      final RuleSet registeredRuleSet = i.next();
      registered = registeredRuleSet.getName().equals(ruleSet.getName());
    }

    return registered;
  }
}
