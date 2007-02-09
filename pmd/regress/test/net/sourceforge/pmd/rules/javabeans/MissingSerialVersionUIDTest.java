
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.javabeans;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class MissingSerialVersionUIDTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() throws Exception {
         rule = findRule("javabeans", "MissingSerialVersionUID");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(MissingSerialVersionUIDTest.class);
     }
 }
