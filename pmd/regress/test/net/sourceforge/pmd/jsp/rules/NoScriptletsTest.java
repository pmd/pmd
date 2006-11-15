
 package test.net.sourceforge.pmd.jsp.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Test the "NoScriptlets" rule.
  *
  * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
  */
 public class NoScriptletsTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("jsp", "NoScriptlets");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
