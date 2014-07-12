 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
package net.sourceforge.pmd.lang.java.rule.codesize;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Iterator;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.testframework.RuleTst;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import net.sourceforge.pmd.testframework.TestDescriptor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(SimpleAggregatorTst.CustomXmlTestClassMethodsRunner.class)
public class ModifiedCyclomaticComplexityTest extends RuleTst {
     private Rule rule;
     private TestDescriptor[] tests;
 
     @Before public void setUp() {
         rule = findRule("java-codesize", "ModifiedCyclomaticComplexity");
         tests = extractTestsFromXml(rule);
     }
 
     @Test
     public void testOneMethod() throws Throwable {
         rule.setProperty(ModifiedCyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 1);
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         Iterator<RuleViolation> i = report.iterator();
         RuleViolation rv = i.next();
         assertNotSame(rv.getDescription().indexOf("Highest = 1"), -1);
     }
 
     @Test
     public void testNastyComplicatedMethod() throws Throwable {
         rule.setProperty(ModifiedCyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 8);
         Report report = new Report();
         runTestFromString(tests[1].getCode(), rule, report);
         Iterator<RuleViolation> i = report.iterator();
         RuleViolation rv = i.next();
         assertNotSame(rv.getDescription().indexOf("Highest = 9"), -1);
     }
 
     @Test
     public void testConstructor() throws Throwable {
         rule.setProperty(ModifiedCyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 1);
         Report report = new Report();
         runTestFromString(tests[2].getCode(), rule, report);
         Iterator<RuleViolation> i = report.iterator();
         RuleViolation rv = i.next();
         assertNotSame(rv.getDescription().indexOf("Highest = 1"), -1);
     }
 
     @Test
     public void testLessComplicatedThanReportLevel() throws Throwable {
         rule.setProperty(ModifiedCyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 10);
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         assertEquals(0, report.size());
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(ModifiedCyclomaticComplexityTest.class);
     }
 }
