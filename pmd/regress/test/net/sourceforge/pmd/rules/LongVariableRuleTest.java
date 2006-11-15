
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class LongVariableRuleTest extends SimpleAggregatorTst {
     private Rule rule;
     private TestDescriptor[] tests;
 
     public void setUp() {
         rule = findRule("naming", "LongVariable");
         tests = extractTestsFromXml(rule);
     }
 
     public void testAll() {
         runTests(new TestDescriptor[] {tests[0], tests[1], tests[2], tests[3], tests[4]});
     }
     
     public void testThreshold() {
         runTest(tests[5]);  //Need a fresh rule to work around caching
     }
 }
