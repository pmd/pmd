
 package test.net.sourceforge.pmd.rules.migrating;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidAssertAsIdentifierTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("migrating", "AvoidAssertAsIdentifier");
     }
 
     public void testOne() throws Throwable {
         runTests(rule);
     }
 }
