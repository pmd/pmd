
 package test.net.sourceforge.pmd.rules.strictexception;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidThrowingNullPointerExceptionTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("strictexception", "AvoidThrowingNullPointerException");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
