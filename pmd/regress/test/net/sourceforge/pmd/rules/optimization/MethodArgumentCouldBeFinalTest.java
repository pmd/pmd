
 /*
  * Created on Jan 10, 2005 
  *
  * $Id$
  */
 package test.net.sourceforge.pmd.rules.optimization;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class MethodArgumentCouldBeFinalTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("optimizations", "MethodArgumentCouldBeFinal");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
