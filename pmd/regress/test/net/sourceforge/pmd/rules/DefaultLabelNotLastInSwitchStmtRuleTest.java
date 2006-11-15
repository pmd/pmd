
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class DefaultLabelNotLastInSwitchStmtRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("design", "DefaultLabelNotLastInSwitchStmt");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
