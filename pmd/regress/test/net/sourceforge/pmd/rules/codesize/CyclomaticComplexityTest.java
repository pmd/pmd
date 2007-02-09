 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
package test.net.sourceforge.pmd.rules.codesize;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.RuleTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

import java.util.Iterator;

 public class CyclomaticComplexityTest extends RuleTst {
     private Rule rule;
     private TestDescriptor[] tests;
 
     @Before public void setUp() {
         rule = findRule("codesize", "CyclomaticComplexity");
         tests = extractTestsFromXml(rule);
     }
 
     @Test
     public void testOneMethod() throws Throwable {
         rule.addProperty("reportLevel", "1");
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         Iterator i = report.iterator();
         RuleViolation rv = (RuleViolation) i.next();
         assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
     }
 
     @Test
     public void testNastyComplicatedMethod() throws Throwable {
         rule.addProperty("reportLevel", "10");
         Report report = new Report();
         runTestFromString(tests[1].getCode(), rule, report);
         Iterator i = report.iterator();
         RuleViolation rv = (RuleViolation) i.next();
         assertTrue(rv.getDescription().indexOf("Highest = 11") != -1);
     }
 
     @Test
     public void testConstructor() throws Throwable {
         rule.addProperty("reportLevel", "1");
         Report report = new Report();
         runTestFromString(tests[2].getCode(), rule, report);
         Iterator i = report.iterator();
         RuleViolation rv = (RuleViolation) i.next();
         assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
     }
 
     @Test
     public void testLessComplicatedThanReportLevel() throws Throwable {
         rule.addProperty("reportLevel", "10");
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         assertEquals(0, report.size());
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(CyclomaticComplexityTest.class);
     }
 }
