
 package test.net.sourceforge.pmd.rules.design;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidConstantsInterfaceTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("design", "AvoidConstantsInterface");
     }
 
     public void testAll() {
         runTests(rule);
     }
 
 }
