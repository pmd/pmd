
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UnnecessaryFinalModifierTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("basic", "UnnecessaryFinalModifier");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
