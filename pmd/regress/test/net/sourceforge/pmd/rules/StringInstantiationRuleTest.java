/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class StringInstantiationRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/strings.xml", "StringInstantiation");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "new 'new String's", 2, rule),
           new TestDescriptor(TEST2, "new String array", 0, rule),
           new TestDescriptor(TEST3, "using multiple parameter constructor", 0, rule),
           new TestDescriptor(TEST4, "using 4 parameter constructor", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private String bar = new String(\"bar\");" + PMD.EOL +
    " private String baz = new String();" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private String[] bar = new String[5];" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  byte[] bytes = new byte[50];" + PMD.EOL +
    "  String bar = new String(bytes, 0, bytes.length);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  byte[] bytes = new byte[50];" + PMD.EOL +
    "  String bar = new String(bytes, 0, bytes.length, \"some-encoding\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


}
