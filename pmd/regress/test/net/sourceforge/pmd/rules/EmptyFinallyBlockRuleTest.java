package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyFinallyBlockRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class EmptyFinallyBlock1 {" + CPD.EOL +
    "    public void foo() {" + CPD.EOL +
    "       try {" + CPD.EOL +
    "       } catch (Exception e) {} finally {}" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptyFinallyBlock2 {" + CPD.EOL +
    "    public void foo() {" + CPD.EOL +
    "       try {" + CPD.EOL +
    "       } finally {}" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class EmptyFinallyBlock3 {" + CPD.EOL +
    "    public void foo() {" + CPD.EOL +
    "       try {" + CPD.EOL +
    "       } finally {int x =2;}" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class EmptyFinallyBlock4 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "  } catch (IOException e ){" + CPD.EOL +
    "  } catch (Exception e ) {" + CPD.EOL +
    "  } catch (Throwable t ) {" + CPD.EOL +
    "  } finally{" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//TryStatement[@Finally='true']/Block[position() = last()][count(*) = 0]");
    }
    public void testEmptyFinallyBlock1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testEmptyFinallyBlock2() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }
    public void testEmptyFinallyBlock3() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
    public void testMultipleCatchBlocksWithFinally() throws Throwable {
        runTestFromString(TEST4, 1, rule);
    }
}
