
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class UncommentedEmptyConstructorRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
     private TestDescriptor[] tests;
 
     public void setUp() {
         rule = findRule("design", "UncommentedEmptyConstructor");
         tests = extractTestsFromXml(rule);
     }
 
     public void testDefault() {
         runTests(tests);
     }
 
     public void testIgnoredConstructorInvocation() {
         rule.addProperty("ignoreExplicitConstructorInvocation", "true");
         runTests(new TestDescriptor[]{
             new TestDescriptor(tests[0].getCode(), "simple failure", 1, rule),
             new TestDescriptor(tests[1].getCode(), "only 'this(...)' failure", 1, rule),
             new TestDescriptor(tests[2].getCode(), "only 'super(...)' failure", 1, rule),
             new TestDescriptor(tests[3].getCode(), "single-line comment is OK", 0, rule),
             new TestDescriptor(tests[4].getCode(), "multiple-line comment is OK", 0, rule),
             new TestDescriptor(tests[5].getCode(), "Javadoc comment is OK", 0, rule),
             new TestDescriptor(tests[6].getCode(), "ok", 0, rule),
             new TestDescriptor(tests[7].getCode(), "with 'this(...)' ok", 0, rule),
             new TestDescriptor(tests[8].getCode(), "with 'super(...)' ok", 0, rule),
             new TestDescriptor(tests[9].getCode(), "private is ok", 0, rule),
         });
     }
 }
