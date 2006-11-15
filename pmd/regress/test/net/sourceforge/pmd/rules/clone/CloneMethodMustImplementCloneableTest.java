
 package test.net.sourceforge.pmd.rules.clone;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class CloneMethodMustImplementCloneableTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("clone", "CloneMethodMustImplementCloneable");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
