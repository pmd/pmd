
 package test.net.sourceforge.pmd.rules.optimization;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UseArraysAsListTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("optimizations", "UseArraysAsList");
     }
 
     // FIXME should be able to catch case where Integer[] is passed
     // as an argument... but may need to rewrite in Java for that.
     public void testAll() {
         runTests(rule);
     }
 }
