
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.design;
 
 import net.sourceforge.pmd.Rule;
 import net.sourceforge.pmd.rules.design.PositionalIteratorRule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class PositionalIteratorRuleTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = new PositionalIteratorRule();
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
