
 package test.net.sourceforge.pmd.rules.finalize;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class FinalizeOverloadedRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("finalizers", "FinalizeOverloaded");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
