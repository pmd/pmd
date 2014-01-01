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
import net.sourceforge.pmd.testframework.SimpleAggregatorTst.CustomXmlTestClassMethodsRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;


@RunWith(SimpleAggregatorTst.CustomXmlTestClassMethodsRunner.class)
public class CyclomaticComplexityTest extends RuleTst {
     private Rule rule;
     private TestDescriptor[] tests;
 
     @Before public void setUp() {
         rule = findRule("java-codesize", "CyclomaticComplexity");
         tests = extractTestsFromXml(rule);
     }
 
     @Test
     public void testOneMethod() throws Throwable {
         rule.setProperty(CyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 1);
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         Iterator<RuleViolation> i = report.iterator();
         RuleViolation rv = i.next();
         assertNotSame(rv.getDescription().indexOf("Highest = 1"), -1);
     }
 
     @Test
     public void testNastyComplicatedMethod() throws Throwable {
         rule.setProperty(CyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 10);
         Report report = new Report();
         runTestFromString(tests[1].getCode(), rule, report);
         Iterator<RuleViolation> i = report.iterator();
         RuleViolation rv = i.next();
         assertNotSame(rv.getDescription().indexOf("Highest = 11"), -1);
     }
 
     @Test
     public void testConstructor() throws Throwable {
         rule.setProperty(CyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 1);
         Report report = new Report();
         runTestFromString(tests[2].getCode(), rule, report);
         Iterator<RuleViolation> i = report.iterator();
         RuleViolation rv = i.next();
         assertNotSame(rv.getDescription().indexOf("Highest = 1"), -1);
     }
 
     @Test
     public void testLessComplicatedThanReportLevel() throws Throwable {
         rule.setProperty(CyclomaticComplexityRule.REPORT_LEVEL_DESCRIPTOR, 10);
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         assertEquals(0, report.size());
     }

     @Test
     public void testRemainingTestCases() {
         for (int i = 0; i < tests.length; i++) {
             if (i == 0 || i == 1 || i == 2) {
                 continue; // skip - covered by above test methods
             }

             try {
                 runTest(tests[i]);
             } catch (Throwable t) {
                 Failure f = CustomXmlTestClassMethodsRunner.createFailure(rule, t);
                 CustomXmlTestClassMethodsRunner.addFailure(f);
             }
         }
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(CyclomaticComplexityTest.class);
     }
 }
