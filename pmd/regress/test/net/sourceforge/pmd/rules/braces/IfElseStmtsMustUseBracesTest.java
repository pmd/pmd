
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.braces;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class IfElseStmtsMustUseBracesTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() throws Exception {
         rule = findRule("braces", "IfElseStmtsMustUseBraces");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(IfElseStmtsMustUseBracesTest.class);
     }
 }
