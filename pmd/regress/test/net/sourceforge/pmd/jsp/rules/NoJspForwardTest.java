
 package test.net.sourceforge.pmd.jsp.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Test the "NoJspForward" rule.
  *
  * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
  */
 public class NoJspForwardTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("jsp", "NoJspForward");
     }
 
     public void testViolation() {
         runTests(rule);
     }
 }
