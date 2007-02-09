
 package test.net.sourceforge.pmd.rules.design;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class InstantiationToGetClassRuleTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("design", "InstantiationToGetClass");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(InstantiationToGetClassRuleTest.class);
     }
 }
