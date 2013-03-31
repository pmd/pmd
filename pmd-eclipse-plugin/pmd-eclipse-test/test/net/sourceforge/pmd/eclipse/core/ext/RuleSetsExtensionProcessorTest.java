/*
 * Created on 2 juillet 2005
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
package net.sourceforge.pmd.eclipse.core.ext;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the ruleset extension
 * 
 * @author Philippe Herlin
 * 
 */
public class RuleSetsExtensionProcessorTest {

  /**
   * Tests the additional default rulesets has been registered. For this
   * test to work, the test plugin fragment must be installed.
   * 
   */
  @Test
  public void testAdditionalDefaultRuleSetsRegistered() throws RuleSetNotFoundException {
    final Set<RuleSet> registeredRuleSets = PMDPlugin.getDefault().getRuleSetManager().getDefaultRuleSets();
    Assert.assertFalse("No registered default rulesets!", registeredRuleSets.isEmpty());

    final RuleSetFactory factory = new RuleSetFactory();
    RuleSet ruleSet = factory.createRuleSet("rulesets/extra1.xml");
    Assert.assertTrue("RuleSet \"rulesets/extra1.xml\" has not been registered", ruleSetRegistered(ruleSet, registeredRuleSets));

    ruleSet = factory.createRuleSet("rulesets/extra2.xml");
    Assert.assertTrue("RuleSet \"rulesets/extra2.xml\" has not been registered", ruleSetRegistered(ruleSet, registeredRuleSets));
  }

  /**
   * Tests the additional rulesets has been registered. For this test to
   * work, the test plugin fragment must be installed.
   * 
   */
  @Test
  public void testAdditionalRuleSetsRegistered() throws RuleSetNotFoundException {
    final Set<RuleSet> registeredRuleSets = PMDPlugin.getDefault().getRuleSetManager().getRegisteredRuleSets();
    Assert.assertFalse("No registered rulesets!", registeredRuleSets.isEmpty());

    final RuleSetFactory factory = new RuleSetFactory();
    RuleSet ruleSet = factory.createRuleSet("rulesets/extra1.xml");
    Assert.assertTrue("RuleSet \"rulesets/extra1.xml\" has not been registered", ruleSetRegistered(ruleSet, registeredRuleSets));

    ruleSet = factory.createRuleSet("rulesets/extra2.xml");
    Assert.assertTrue("RuleSet \"rulesets/extra2.xml\" has not been registered", ruleSetRegistered(ruleSet, registeredRuleSets));
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
