package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyCatchBlockRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//TryStatement[@Catch='true']"
                + "/Block"
                + "[position() > 1]"
                + "[count(*) = 0]"
                + "[../@Finally='false' or following-sibling::Block]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
           new TestDescriptor(TEST3, "no catch with nested catch in finally", 1, rule),
           new TestDescriptor(TEST4, "multiple catch blocks", 2, rule),
           new TestDescriptor(TEST5, "empty try with finally", 0, rule),
       });
    }

    public static final String TEST1 =
    "import java.io.*;" + CPD.EOL +
    "public class Foo {" + CPD.EOL +
    " public Foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   FileReader fr = new FileReader(\"/dev/null\");" + CPD.EOL +
    "  } catch (Exception e) {}" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   FileReader fr = new FileReader(\"/dev/null\");" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "   e.printStackTrace();" + CPD.EOL +
    "   // this shouldn't show up on the report" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + CPD.EOL +
    "    public Foo() {" + CPD.EOL +
    "       try {" + CPD.EOL +
    "       } catch (RuntimeException e) {e.getMessage();}" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + CPD.EOL +
    " private void foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "  } finally {" + CPD.EOL +
    "   try {" + CPD.EOL +
    "    int x =2;" + CPD.EOL +
    "   } catch (Exception e) {}" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + CPD.EOL +
    " private void foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "  } catch (Throwable t) {" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "   blah.blah();" + CPD.EOL +
    "  } finally {}" + CPD.EOL +
    " }" + CPD.EOL +
    "}";
}

