
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UseArrayListInsteadOfVectorTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("optimizations", "UseArrayListInsteadOfVector");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
