
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules;
 
 import java.util.Iterator;
 
 import net.sourceforge.pmd.Report;
 import net.sourceforge.pmd.Rule;
 import net.sourceforge.pmd.RuleViolation;
 import test.net.sourceforge.pmd.testframework.RuleTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class CyclomaticComplexityTest extends RuleTst {
     private Rule rule;
     private TestDescriptor[] tests;
 
     public void setUp() {
         rule = findRule("codesize", "CyclomaticComplexity");
         tests = extractTestsFromXml(rule);
     }
 
     public void testOneMethod() throws Throwable {
         rule.addProperty("reportLevel", "1");
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         Iterator i = report.iterator();
         RuleViolation rv = (RuleViolation) i.next();
         assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
     }
 
     public void testNastyComplicatedMethod() throws Throwable {
         rule.addProperty("reportLevel", "10");
         Report report = new Report();
         runTestFromString(tests[1].getCode(), rule, report);
         Iterator i = report.iterator();
         RuleViolation rv = (RuleViolation) i.next();
         assertTrue(rv.getDescription().indexOf("Highest = 11") != -1);
     }
 
     public void testConstructor() throws Throwable {
         rule.addProperty("reportLevel", "1");
         Report report = new Report();
         runTestFromString(tests[2].getCode(), rule, report);
         Iterator i = report.iterator();
         RuleViolation rv = (RuleViolation) i.next();
         assertTrue(rv.getDescription().indexOf("Highest = 1") != -1);
     }
 
     public void testLessComplicatedThanReportLevel() throws Throwable {
         rule.addProperty("reportLevel", "10");
         Report report = new Report();
         runTestFromString(tests[0].getCode(), rule, report);
         assertEquals(0, report.size());
     }
 }
