
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class SuspiciousHashcodeMethodNameRuleTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("naming", "SuspiciousHashcodeMethodName");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
