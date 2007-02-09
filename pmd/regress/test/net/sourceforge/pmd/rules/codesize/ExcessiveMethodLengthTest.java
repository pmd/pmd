
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.codesize;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class ExcessiveMethodLengthTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("codesize", "ExcessiveMethodLength");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     @Ignore
     @Test
     public void testOverrideMinimumWithTopScore() throws Throwable {
/*         Rule r = findRule("codesize", "ExcessiveMethodLength");
         r.addProperty("minimum", "1");
         r.addProperty("topscore", "2");
         Report rpt = new Report();
         runTestFromString(tests[5].getCode(), r, rpt);
         for (Iterator i = rpt.iterator(); i.hasNext();) {
             RuleViolation rv = (RuleViolation)i.next();
             assertTrue(rv.getLine() == 2 || rv.getLine() == 6);
         }
*/     }
 

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(ExcessiveMethodLengthTest.class);
     }
 }
 
