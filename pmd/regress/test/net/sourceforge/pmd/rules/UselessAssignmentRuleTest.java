
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UselessAssignmentRuleTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("rulesets/scratchpad.xml", "UselessAssignment");
     }
 
     @Ignore("Scratchpad rule - throwing exception")
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(UselessAssignmentRuleTest.class);
     }
 }
