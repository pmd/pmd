
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.junit;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class JUnitTestsShouldContainAssertsTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("junit", "JUnitTestsShouldIncludeAssert");
     }
 
     public void testAll() throws Throwable {
         runTests(rule);
     }
 }
