
 package test.net.sourceforge.pmd.rules.controversial;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UnnecessaryParenthesesTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() throws Exception {
         rule = findRule("controversial", "UnnecessaryParentheses");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(UnnecessaryParenthesesTest.class);
     }
 }
