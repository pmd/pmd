
 package test.net.sourceforge.pmd.rules.strictexception;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class ExceptionAsFlowControlTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() throws Exception {
         rule = findRule("strictexception", "ExceptionAsFlowControl");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(ExceptionAsFlowControlTest.class);
     }
 }
