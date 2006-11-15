
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.design;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class ExcessiveMethodLengthTest extends SimpleAggregatorTst {
     private Rule rule;
     private TestDescriptor[] tests;
 
     public void setUp() {
         rule = findRule("codesize", "ExcessiveMethodLength");
         tests = extractTestsFromXml(rule);
     }
 
     public void testAll() {
         rule.addProperty("minimum", "10");
         runTests(tests);
     }
 
     public void testReallyLongMethodWithLongerRange() throws Throwable {
         rule.addProperty("minimum", "20");
         runTest(new TestDescriptor(tests[1].getCode(), 
             "minimum with longer range", 0, rule));
     }
 
 /*
     public void testOverrideMinimumWithTopScore() throws Throwable {
         Rule r = findRule("codesize", "ExcessiveMethodLength");
         r.addProperty("minimum", "1");
         r.addProperty("topscore", "2");
         Report rpt = new Report();
         runTestFromString(tests[4].getCode(), r, rpt);
         for (Iterator i = rpt.iterator(); i.hasNext();) {
             RuleViolation rv = (RuleViolation)i.next();
             assertTrue(rv.getLine() == 2 || rv.getLine() == 6);
         }
     }
 */
 
 }
 
