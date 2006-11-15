
 package test.net.sourceforge.pmd.rules.strings;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UnnecessaryCaseChangeRuleTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("rulesets/strings.xml", "UnnecessaryCaseChange");
     }
 
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
 
 }
