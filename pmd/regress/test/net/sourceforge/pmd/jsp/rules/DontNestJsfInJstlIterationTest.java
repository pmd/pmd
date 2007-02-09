
 package test.net.sourceforge.pmd.jsp.rules;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Test the "DontNestJsfInJstlIteration" rule.
  *
  * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
  */
 public class DontNestJsfInJstlIterationTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before 
     public void setUp() {
         rule = findRule("jsf", "DontNestJsfInJstlIteration");
     }
 
     @Test
     public void testViolation() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(DontNestJsfInJstlIterationTest.class);
     }
 }
