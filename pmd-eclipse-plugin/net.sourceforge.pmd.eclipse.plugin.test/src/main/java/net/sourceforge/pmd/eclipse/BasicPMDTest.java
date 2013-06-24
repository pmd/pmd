/*
 * Created on 6 f�vr. 2005
 * 
 * Copyright (c) 2004, PMD for Eclipse Development Team All rights reserved.
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
package net.sourceforge.pmd.eclipse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test if PMD can be run correctly
 * 
 * @author Philippe Herlin
 * 
 */
public class BasicPMDTest {

  static class StringDataSource implements DataSource {
    private final ByteArrayInputStream is;

    public StringDataSource(final String source) {
        try {
            this.is = new ByteArrayInputStream(source.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStream() {
        return is;
    }

    @Override
    public String getNiceFileName(final boolean shortNames, final String inputFileName) {
      return "somefile.txt";
    }
  }

  /**
   * Try to load all the plugin known rulesets
   * 
   */
  @Test
  public void testDefaulltRuleSets() {
    try {
      final RuleSetFactory factory = new RuleSetFactory();
      final Iterator<RuleSet> iterator = factory.getRegisteredRuleSets();
      while (iterator.hasNext()) {
        iterator.next();
      }
    }
    catch (final RuleSetNotFoundException e) {
      e.printStackTrace();
      Assert.fail("unable to load registered rulesets ");
    }
  }

  /**
   * One first thing the plugin must be able to do is to run PMD
   * 
   */
  @Test
  public void testRunPmdJdk13() {

    try {
      PMDConfiguration configuration = new PMDConfiguration();
      configuration.setDefaultLanguageVersion(LanguageVersion.JAVA_13);

      final String sourceCode = "public class Foo {\n public void foo() {\nreturn;\n}}";

      final RuleContext context = new RuleContext();
      context.setSourceCodeFilename("foo.java");
      context.setReport(new Report());

      final RuleSet basicRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/basic.xml");
      RuleSets rSets = new RuleSets(basicRuleSet);
      new SourceCodeProcessor(configuration).processSourceCode(
              new StringDataSource(sourceCode).getInputStream(), rSets, context);

      final Iterator<RuleViolation> iter = context.getReport().iterator();
      Assert.assertTrue("There should be at least one violation", iter.hasNext());

      final RuleViolation violation = iter.next();
      Assert.assertEquals(violation.getRule().getName(), "UnnecessaryReturn");
      Assert.assertEquals(3, violation.getBeginLine());

    }
    catch (final RuleSetNotFoundException e) {
      e.printStackTrace();
      Assert.fail();
    }
    catch (final PMDException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /**
   * Let see with Java 1.4
   * 
   */
  @Test
  public void testRunPmdJdk14() {

    try {
      PMDConfiguration configuration = new PMDConfiguration();
      configuration.setDefaultLanguageVersion(LanguageVersion.JAVA_14);

      final String sourceCode = "public class Foo {\n public void foo() {\nreturn;\n}}";

      final RuleContext context = new RuleContext();
      context.setSourceCodeFilename("foo.java");
      context.setReport(new Report());

      final RuleSet basicRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/basic.xml");
      RuleSets rSets = new RuleSets(basicRuleSet);
      new SourceCodeProcessor(configuration).processSourceCode(
              new StringDataSource(sourceCode).getInputStream(), rSets, context);

      final Iterator<RuleViolation> iter = context.getReport().iterator();
      Assert.assertTrue("There should be at least one violation", iter.hasNext());

      final RuleViolation violation = iter.next();
      Assert.assertEquals(violation.getRule().getName(), "UnnecessaryReturn");
      Assert.assertEquals(3, violation.getBeginLine());

    }
    catch (final RuleSetNotFoundException e) {
      e.printStackTrace();
      Assert.fail();
    }
    catch (final PMDException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /**
   * Let see with Java 1.5
   * 
   */
  @Test
  public void testRunPmdJdk15() {

    try {
      PMDConfiguration configuration = new PMDConfiguration();
      configuration.setDefaultLanguageVersion(LanguageVersion.JAVA_15);

      final String sourceCode = "public class Foo {\n public void foo() {\nreturn;\n}}";

      final RuleContext context = new RuleContext();
      context.setSourceCodeFilename("foo.java");
      context.setReport(new Report());

      final RuleSet basicRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/basic.xml");
      RuleSets rSets = new RuleSets(basicRuleSet);
      new SourceCodeProcessor(configuration).processSourceCode(
              new StringDataSource(sourceCode).getInputStream(), rSets, context);

      final Iterator<RuleViolation> iter = context.getReport().iterator();
      Assert.assertTrue("There should be at least one violation", iter.hasNext());

      final RuleViolation violation = iter.next();
      Assert.assertEquals(violation.getRule().getName(), "UnnecessaryReturn");
      Assert.assertEquals(3, violation.getBeginLine());

    }
    catch (final RuleSetNotFoundException e) {
      e.printStackTrace();
      Assert.fail();
    }
    catch (final PMDException e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}