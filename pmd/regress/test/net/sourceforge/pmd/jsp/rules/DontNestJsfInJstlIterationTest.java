package test.net.sourceforge.pmd.jsp.rules;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Test the "DontNestJsfInJstlIteration" rule.
  *
  * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
  */
 public class DontNestJsfInJstlIterationTest extends SimpleAggregatorTst {
 
     @Before 
     public void setUp() {
         addRule("jsf", "DontNestJsfInJstlIteration");
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(DontNestJsfInJstlIterationTest.class);
     }
 }
