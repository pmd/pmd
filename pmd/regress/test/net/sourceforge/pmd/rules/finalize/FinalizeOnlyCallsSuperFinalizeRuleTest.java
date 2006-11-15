
 package test.net.sourceforge.pmd.rules.finalize;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class FinalizeOnlyCallsSuperFinalizeRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("finalizers", "FinalizeOnlyCallsSuperFinalize");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
