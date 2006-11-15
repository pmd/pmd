
 package test.net.sourceforge.pmd.rules.clone;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class CloneThrowsCloneNotSupportedExceptionTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("clone", "CloneThrowsCloneNotSupportedException");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
