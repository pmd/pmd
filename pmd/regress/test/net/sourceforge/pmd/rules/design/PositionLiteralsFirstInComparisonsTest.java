
 package test.net.sourceforge.pmd.rules.design;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class PositionLiteralsFirstInComparisonsTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("design", "PositionLiteralsFirstInComparisons");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }
 

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(PositionLiteralsFirstInComparisonsTest.class);
     }
 }
