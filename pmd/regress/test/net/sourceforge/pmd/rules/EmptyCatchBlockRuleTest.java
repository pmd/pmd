package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyCatchBlockRuleTest extends RuleTst {

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

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testNotEmpty() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testNoCatchWithNestedCatchInFinally() throws Throwable {
        runTestFromString(TEST3, 1, rule);
    }
    public void testMultipleCatchBlocks() throws Throwable {
        runTestFromString(TEST4, 2, rule);
    }
    public void testEmptyTryAndFinally() throws Throwable {
        runTestFromString(TEST5, 0, rule);
    }

    public static final String TEST1 =
    "import java.io.*;" + CPD.EOL +
    "public class EmptyCatchBlock {" + CPD.EOL +
    "    public EmptyCatchBlock() {" + CPD.EOL +
    "       try {" + CPD.EOL +
    "               FileReader fr = new FileReader(\"/dev/null\");" + CPD.EOL +
    "               // howdy" + CPD.EOL +
    "       } catch (Exception e) {" + CPD.EOL +
    "       }" + CPD.EOL +
    "       try {" + CPD.EOL +
    "               FileReader fr = new FileReader(\"/dev/null\");" + CPD.EOL +
    "       } catch (Exception e) {" + CPD.EOL +
    "               e.printStackTrace();" + CPD.EOL +
    "               // this shouldn't show up on the report" + CPD.EOL +
    "       }" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptyCatchBlock2 {" + CPD.EOL +
    "    public EmptyCatchBlock2() {" + CPD.EOL +
    "       try {" + CPD.EOL +
    "       } catch (RuntimeException e) {e.getMessage();}" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class EmptyCatchBlock3 {" + CPD.EOL +
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
    "public class EmptyCatchBlock4 {" + CPD.EOL +
    " private void foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "  } catch (Throwable t) {" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class EmptyCatchBlock5 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "   blah.blah();" + CPD.EOL +
    "  } finally {}" + CPD.EOL +
    " }" + CPD.EOL +
    "}";
}

