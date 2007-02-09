
 package test.net.sourceforge.pmd.jsp.rules;
 
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Test the "NoScriptlets" rule.
  *
  * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
  */
 public class NoScriptletsTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("jsp", "NoScriptlets");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(NoScriptletsTest.class);
     }
 }
