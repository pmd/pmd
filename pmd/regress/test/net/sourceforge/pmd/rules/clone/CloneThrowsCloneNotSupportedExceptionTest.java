
 package test.net.sourceforge.pmd.rules.clone;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class CloneThrowsCloneNotSupportedExceptionTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("clone", "CloneThrowsCloneNotSupportedException");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(CloneThrowsCloneNotSupportedExceptionTest.class);
     }
 }
