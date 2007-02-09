
 package test.net.sourceforge.pmd.rules.strictexception;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Tests the <code>AvoidRethrowingException</code> rule.
  *
  * @author George Thomas
  */
 public class AvoidRethrowingExceptionTest extends SimpleAggregatorTst {
 
     private Rule rule;

     @Before
     public void setUp() {
         rule = findRule("strictexception", "AvoidRethrowingException");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(AvoidRethrowingExceptionTest.class);
     }
 }
