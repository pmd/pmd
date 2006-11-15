
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class EmptyCatchBlockRuleTest extends SimpleAggregatorTst {
     private Rule rule;
     private TestDescriptor[] tests;
 
     public void setUp() {
         rule = findRule("basic", "EmptyCatchBlock");
         tests = extractTestsFromXml(rule);
     }
 
     public void testAll() {
         runTests(tests);
     }
 
     public void testCommentedBlocksAllowed() {
         rule.addProperty("allowCommentedBlocks", "true");
         runTests(new TestDescriptor[]{
             new TestDescriptor(tests[7].getCode(), "single-line comment is OK", 0, rule),
             new TestDescriptor(tests[8].getCode(), "multiple-line comment is OK", 0, rule),
             new TestDescriptor(tests[9].getCode(), "Javadoc comment is OK", 0, rule),
         });
     }
 }
 
