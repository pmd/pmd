package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ReturnFromFinallyBlockTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//TryStatement[@Finally='true']/Block[position() = last()]//ReturnStatement");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "throw exception but return from finally", 1, rule),
           new TestDescriptor(TEST2, "lots of returns", 1, rule),
           new TestDescriptor(TEST3, "ok", 0, rule),
       });
    }
    private static final String TEST1 =
    "public class ReturnFromFinallyBlock1 {" + CPD.EOL +
    " public String bugga() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   throw new Exception( \"My Exception\" );" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "   throw e;" + CPD.EOL +
    "  } finally {" + CPD.EOL +
    "   return \"A. O. K.\"; // Very bad." + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ReturnFromFinallyBlock2 {" + CPD.EOL +
    " public String getBar() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   return \"buz\";" + CPD.EOL +
    "  } catch (Exception e) {" + CPD.EOL +
    "   return \"biz\";" + CPD.EOL +
    "  } finally {" + CPD.EOL +
    "   return \"fiddle!\"; // bad!" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class ReturnFromFinallyBlock3 {" + CPD.EOL +
    " public String getBar() {" + CPD.EOL +
    "  try {" + CPD.EOL +
    "   return \"buz\";" + CPD.EOL +
    "  } finally {" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

}
