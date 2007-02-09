
 package test.net.sourceforge.pmd.rules.optimizations;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UseArraysAsListTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("optimizations", "UseArraysAsList");
     }
 
     // FIXME should be able to catch case where Integer[] is passed
     // as an argument... but may need to rewrite in Java for that.
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(UseArraysAsListTest.class);
     }
 }
