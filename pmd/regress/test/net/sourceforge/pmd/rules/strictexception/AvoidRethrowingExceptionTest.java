
 package test.net.sourceforge.pmd.rules.strictexception;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Tests the <code>AvoidRethrowingException</code> rule.
  *
  * @author George Thomas
  */
 public class AvoidRethrowingExceptionTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("strictexception", "AvoidRethrowingException");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
