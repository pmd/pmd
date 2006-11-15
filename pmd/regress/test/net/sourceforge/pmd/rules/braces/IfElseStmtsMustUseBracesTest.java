
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.braces;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class IfElseStmtsMustUseBracesTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() throws Exception {
         rule = findRule("braces", "IfElseStmtsMustUseBraces");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
