
 package test.net.sourceforge.pmd.jsp.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Test the "DontNestJsfInJstlIteration" rule.
  *
  * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
  */
 public class DontNestJsfInJstlIterationTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("jsf", "DontNestJsfInJstlIteration");
     }
 
     public void testViolation() {
         runTests(rule);
     }
 }
