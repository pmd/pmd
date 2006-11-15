
 package test.net.sourceforge.pmd.rules.migrating;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidEnumAsIdentifierTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("migrating", "AvoidEnumAsIdentifier");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
