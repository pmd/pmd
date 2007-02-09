
 package test.net.sourceforge.pmd.rules.migrating;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidAssertAsIdentifierTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("migrating", "AvoidAssertAsIdentifier");
     }
 
     @Test
     public void testOne() throws Throwable {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(AvoidAssertAsIdentifierTest.class);
     }
 }
