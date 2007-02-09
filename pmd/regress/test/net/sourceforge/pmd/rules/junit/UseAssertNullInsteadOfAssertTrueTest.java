
 package test.net.sourceforge.pmd.rules.junit;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UseAssertNullInsteadOfAssertTrueTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("junit", "UseAssertNullInsteadOfAssertTrue");
     }
 
     @Test
     public void testAll() throws Throwable {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(UseAssertNullInsteadOfAssertTrueTest.class);
     }
 }
 
