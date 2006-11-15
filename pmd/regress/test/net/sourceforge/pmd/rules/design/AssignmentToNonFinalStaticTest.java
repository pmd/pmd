
 package test.net.sourceforge.pmd.rules.design;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AssignmentToNonFinalStaticTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("design", "AssignmentToNonFinalStatic");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
