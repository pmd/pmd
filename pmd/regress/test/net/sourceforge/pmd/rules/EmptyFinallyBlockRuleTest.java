package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
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
    "public class EmptyFinallyBlock1 {" + PMD.EOL +
    "    public void foo() {" + PMD.EOL +
    "       try {" + PMD.EOL +
    "       } catch (Exception e) {} finally {}" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class EmptyFinallyBlock2 {" + PMD.EOL +
    "    public void foo() {" + PMD.EOL +
    "       try {" + PMD.EOL +
    "       } finally {}" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class EmptyFinallyBlock3 {" + PMD.EOL +
    "    public void foo() {" + PMD.EOL +
    "       try {" + PMD.EOL +
    "       } finally {int x =2;}" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class EmptyFinallyBlock4 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "  } catch (IOException e ){" + PMD.EOL +
    "  } catch (Exception e ) {" + PMD.EOL +
    "  } catch (Throwable t ) {" + PMD.EOL +
    "  } finally{" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
