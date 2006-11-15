
 package test.net.sourceforge.pmd.rules.strictexception;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidThrowingRawExceptionTypesTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("strictexception", "AvoidThrowingRawExceptionTypes");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
