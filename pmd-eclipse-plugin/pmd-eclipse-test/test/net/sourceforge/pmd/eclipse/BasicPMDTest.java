/*
 * Created on 6 f�vr. 2005
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.eclipse;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.eclipse.core.PluginConstants;

/**
 * Test if PMD can be run correctly
 * 
 * @author Philippe Herlin
 * 
 */
public class BasicPMDTest extends TestCase {

    /**
     * Test case constructor
     * 
     * @param name of the test case
     */
    public BasicPMDTest(String name) {
        super(name);
    }

    /**
     * One first thing the plugin must be able to do is to run PMD
     * 
     */
    public void testRunPmdJdk13() {

        try {
            PMD pmd = new PMD();
            pmd.setJavaVersion(SourceType.JAVA_13);

            String sourceCode = "public class Foo {\n public void foo() {\nreturn;\n}}";
            Reader input = new StringReader(sourceCode);

            RuleContext context = new RuleContext();
            context.setSourceCodeFilename("foo.java");
            context.setReport(new Report());

            RuleSet basicRuleSet = new RuleSetFactory().createSingleRuleSet("rulesets/basic.xml");
            pmd.processFile(input, basicRuleSet, context);

            Iterator iter = context.getReport().iterator();
            assertTrue("There should be at least one violation", iter.hasNext());

            RuleViolation violation = (RuleViolation) iter.next();
            assertEquals(violation.getRule().getName(), "UnnecessaryReturn");
            assertEquals(3, violation.getBeginLine());

        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (PMDException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Let see with Java 1.4
     * 
     */
    public void testRunPmdJdk14() {

        try {
            PMD pmd = new PMD();
            pmd.setJavaVersion(SourceType.JAVA_14);

            String sourceCode = "public class Foo {\n public void foo() {\nreturn;\n}}";
            Reader input = new StringReader(sourceCode);

            RuleContext context = new RuleContext();
            context.setSourceCodeFilename("foo.java");
            context.setReport(new Report());

            RuleSet basicRuleSet = new RuleSetFactory().createSingleRuleSet("rulesets/basic.xml");
            pmd.processFile(input, basicRuleSet, context);

            Iterator iter = context.getReport().iterator();
            assertTrue("There should be at least one violation", iter.hasNext());

            RuleViolation violation = (RuleViolation) iter.next();
            assertEquals(violation.getRule().getName(), "UnnecessaryReturn");
            assertEquals(3, violation.getBeginLine());

        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (PMDException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Let see with Java 1.5
     * 
     */
    public void testRunPmdJdk15() {

        try {
            PMD pmd = new PMD();
            pmd.setJavaVersion(SourceType.JAVA_15);

            String sourceCode = "public class Foo {\n public void foo() {\nreturn;\n}}";
            Reader input = new StringReader(sourceCode);

            RuleContext context = new RuleContext();
            context.setSourceCodeFilename("foo.java");
            context.setReport(new Report());

            RuleSet basicRuleSet = new RuleSetFactory().createSingleRuleSet("rulesets/basic.xml");
            pmd.processFile(input, basicRuleSet, context);

            Iterator iter = context.getReport().iterator();
            assertTrue("There should be at least one violation", iter.hasNext());

            RuleViolation violation = (RuleViolation) iter.next();
            assertEquals(violation.getRule().getName(), "UnnecessaryReturn");
            assertEquals(3, violation.getBeginLine());

        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (PMDException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Try to load all the plugin known rulesets
     * 
     */
    public void testDefaulltRuleSets() {
        RuleSetFactory factory = new RuleSetFactory();
        String allRuleSets[] = PluginConstants.PMD_RULESETS;
        for (int i = 0; i < allRuleSets.length; i++) {
            try {
                RuleSet ruleSet = factory.createSingleRuleSet(allRuleSets[i]);
            } catch (RuleSetNotFoundException e) {
                e.printStackTrace();
                fail("unable to load ruleset " + allRuleSets[i]);
            }
        }
    }
}