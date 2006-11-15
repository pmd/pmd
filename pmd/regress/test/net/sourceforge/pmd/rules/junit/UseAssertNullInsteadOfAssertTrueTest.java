
 package test.net.sourceforge.pmd.rules.junit;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UseAssertNullInsteadOfAssertTrueTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("junit", "UseAssertNullInsteadOfAssertTrue");
     }
 
     public void testAll() throws Throwable {
         runTests(rule);
     }
 }
 
