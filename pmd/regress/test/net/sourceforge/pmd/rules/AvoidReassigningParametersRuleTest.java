/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AvoidReassigningParametersRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidReassigningParametersRuleTest extends SimpleAggregatorTst {

    private AvoidReassigningParametersRule rule;

    public void setUp() {
        rule = new AvoidReassigningParametersRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "instance variable and parameter have same name", 1, rule),
           new TestDescriptor(TEST4, "qualified instance variable same name as parameter", 0, rule),
           new TestDescriptor(TEST5, "qualified name same as parameter", 0, rule),
           new TestDescriptor(TEST6, "assignment to parameter public field", 0, rule),
           new TestDescriptor(TEST7, "assignment to array parameter slot", 0, rule),
       });
    }

    public static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  bar = \"something else\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private void foo(String bar) {}" + PMD.EOL +
    "}";

    public static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private int bar;" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  bar = \"hi\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " private int bar;" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  this.bar = \"hi\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST5 =
    "public class Faz {" + PMD.EOL +
    " private class Foo {" + PMD.EOL +
    "  public String bar;" + PMD.EOL +
    " }" + PMD.EOL +
    " private void foo(String bar) {" + PMD.EOL +
    "  Foo f = new Foo();" + PMD.EOL +
    "  f.bar = bar;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST6 =
    "import java.awt.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " private void foo(GridBagConstraints gbc) {" + PMD.EOL +
    "  gbc.gridx = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST7 =
    "import java.awt.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " private void foo(Bar[] bar) {" + PMD.EOL +
    "  bar[0] = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
