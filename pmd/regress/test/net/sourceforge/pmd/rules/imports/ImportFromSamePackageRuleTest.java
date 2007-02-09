
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.imports;
 
 import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class ImportFromSamePackageRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("imports", "ImportFromSamePackage");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(ImportFromSamePackageRuleTest.class);
     }
 }
