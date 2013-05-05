/*
 * Created on 22 juin 2006
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

package net.sourceforge.pmd.eclipse.core.rulesets.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.core.PMDCoreException;
import net.sourceforge.pmd.eclipse.core.rulesets.IRuleSetsManager;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSet;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSets;

import org.junit.Assert;
import org.junit.Test;

/**
 * RuleSetsManager unit tests
 * 
 * @author Herlin
 * 
 */

public class RuleSetsManagerImplTest {

  /**
   * Test the valueOf method in its expected usage.
   * 
   * @throws RuleSetNotFoundException
   */
  @Test
  public void testValueOf1() throws PMDCoreException, RuleSetNotFoundException {
    final IRuleSetsManager rsm = new RuleSetsManagerImpl();
    final RuleSet ruleSet = rsm.valueOf(new String[] { "rulesets/java/basic.xml" });

    final Collection<Rule> pmdRules = ruleSet.getPmdRuleSet().getRules();
    final Collection<Rule> basicRules = new RuleSetFactory().createRuleSet("rulesets/java/basic.xml").getRules();

    final Collection<String> pmdRulesDescriptions = new ArrayList<String>();
    for (final Rule rule : pmdRules) {
      pmdRulesDescriptions.add(rule.getDescription());
    }

    final Collection<String> basicRulesDescriptions = new ArrayList<String>();
    for (final Rule rule : basicRules) {
      basicRulesDescriptions.add(rule.getDescription());
    }

    // dump("PMD Rules", pmdRules);
    // dump("Basic Rules", basicRules);

    Assert.assertTrue("All the basic rules have not been loaded !", pmdRulesDescriptions.containsAll(basicRulesDescriptions));
    Assert.assertTrue("All the loaded rules are not in the basic rules!", basicRulesDescriptions.containsAll(pmdRulesDescriptions));
  }

  /**
   * Passing a null array to valueOf is not allowed
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testValueOf2() throws PMDCoreException {
    try {
      final IRuleSetsManager rsm = new RuleSetsManagerImpl();
      final RuleSet ruleSet = rsm.valueOf(null);
      Assert.fail("Getting a rule set from a null array is not allowed");
    }
    catch (final IllegalArgumentException e) {
      // Success
    }
  }

  /**
   * Passing an empty array to valueOf is not allowed
   * 
   * @throws RuleSetNotFoundException
   * 
   */
  @Test
  public void testValueOf3() throws PMDCoreException {
    try {
      final IRuleSetsManager rsm = new RuleSetsManagerImpl();
      final RuleSet ruleSet = rsm.valueOf(new String[] {});
      Assert.fail("Getting a rule set from an empty array is not allowed");
    }
    catch (final IllegalArgumentException e) {
      // Success
    }
  }

  /**
   * Basically test the writeToXml operation.
   * 
   */
  @Test
  public void testWriteToXml() throws PMDCoreException, UnsupportedEncodingException, IOException {
    ByteArrayOutputStream out = null;
    final InputStream in = new FileInputStream("./test/testRuleSetsManager.rulesets");
    if (in == null)
      throw new IllegalStateException("The test file testRuleSetsManager.rulesets cannot be found. The test cannot be performed.");

    final byte[] bytes = new byte[in.available()];
    in.read(bytes);

    final String reference = new String(bytes, "UTF-8");
    in.close();

    System.out.println("--reference");
    System.out.println(reference);

    try {
      final IRuleSetsManager rsm = new RuleSetsManagerImpl();
      final RuleSet ruleSet = rsm.valueOf(new String[] { "rulesets/java/basic.xml" });
      ruleSet.setName("basic");
      ruleSet.setLanguage(RuleSet.LANGUAGE_JAVA);

      final List<RuleSet> ruleSetsList = new ArrayList<RuleSet>();
      ruleSetsList.add(ruleSet);

      final RuleSets ruleSets = new RuleSets();
      ruleSets.setRuleSets(ruleSetsList);
      ruleSets.setDefaultRuleSet(ruleSet);

      out = new ByteArrayOutputStream();
      rsm.writeToXml(ruleSets, out);

      final String result = new String(out.toByteArray(), "UTF-8");

      System.out.println("--result");
      System.out.println(result);

      Assert.assertEquals("The output rulesets is not the expected one", reference, result);
    }
    finally {
      if (out != null) {
        out.close();
      }
    }
  }

  /**
   * Dump a collection of rules
   * 
   * @param message
   * @param rules
   */
  private void dump(final String message, final Collection<Rule> rules) {
    System.out.println("Dump " + message);
    for (final Rule rule : rules) {
      System.out.println(rule.getName());
    }
    System.out.println();
  }
}
