
 package test.net.sourceforge.pmd.rules.finalize;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidCallingFinalizeTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("finalizers", "AvoidCallingFinalize");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
