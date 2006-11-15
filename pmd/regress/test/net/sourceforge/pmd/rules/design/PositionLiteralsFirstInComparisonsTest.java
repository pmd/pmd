
 package test.net.sourceforge.pmd.rules.design;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class PositionLiteralsFirstInComparisonsTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("design", "PositionLiteralsFirstInComparisons");
     }
 
     public void testAll() {
         runTests(rule);
     }
 
 }
