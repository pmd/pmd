
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UnnecessaryParenthesesTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() throws Exception {
         rule = findRule("controversial", "UnnecessaryParentheses");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
