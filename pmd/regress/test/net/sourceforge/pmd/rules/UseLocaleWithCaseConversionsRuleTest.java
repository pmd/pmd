
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UseLocaleWithCaseConversionsRuleTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("design", "UseLocaleWithCaseConversions");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
