
 package test.net.sourceforge.pmd.rules.strictexception;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class ExceptionAsFlowControlTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() throws Exception {
         rule = findRule("strictexception", "ExceptionAsFlowControl");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
