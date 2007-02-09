
 package test.net.sourceforge.pmd.rules.finalizers;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class FinalizeDoesNotCallSuperFinalizeRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("finalizers", "FinalizeDoesNotCallSuperFinalize");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(FinalizeDoesNotCallSuperFinalizeRuleTest.class);
     }
 }
