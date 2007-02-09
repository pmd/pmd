
 package test.net.sourceforge.pmd.rules.strings;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UnnecessaryCaseChangeRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;

     @Before
     public void setUp() {
         rule = findRule("rulesets/strings.xml", "UnnecessaryCaseChange");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }
 
 /*
    private static final String TEST5 =
     "public class Foo {" + PMD.EOL +
     " private boolean baz(String[] buz) {" + PMD.EOL +
     "  return buz[2].toUpperCase().equalsIgnoreCase(\"foo\");" + PMD.EOL +
     " }" + PMD.EOL +
     "}";
 */

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(UnnecessaryCaseChangeRuleTest.class);
     }
 }
