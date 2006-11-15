
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UnusedNullCheckInEqualsTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("basic", "UnusedNullCheckInEquals");
     }
 
     public void testAll() {
         runTests(rule);
         //FIXME: new TestDescriptor(TESTN, "shouldn't this fail?", 1, rule),
     }
 
     /*private static final String TESTN =
             "public class Foo {" + PMD.EOL +
             " public void bar() {" + PMD.EOL +
             "  if (x != null && y.equals(x)) {} " + PMD.EOL +
             " }" + PMD.EOL +
             "}";
     */
 
 }
