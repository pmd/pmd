
 package test.net.sourceforge.pmd.rules.migrating;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class ReplaceVectorWithListTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("migrating", "ReplaceVectorWithList");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
