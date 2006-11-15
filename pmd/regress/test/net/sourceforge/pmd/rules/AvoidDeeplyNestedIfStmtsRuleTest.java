
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidDeeplyNestedIfStmtsRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("design", "AvoidDeeplyNestedIfStmts");
         rule.addProperty("problemDepth", "3");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
