package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyFinallyBlockRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//TryStatement[@Finally='true']/Block[position() = last()][count(*) = 0]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 1, rule),
           new TestDescriptor(TEST3, "", 0, rule),
           new TestDescriptor(TEST4, "multiple catch blocks with finally", 1, rule),
       });
    }

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

}
