/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ReturnFromFinallyBlockTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "ReturnFromFinallyBlock");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "throw exception but return from finally", 1, rule),
           new TestDescriptor(TEST2, "lots of returns", 1, rule),
           new TestDescriptor(TEST3, "ok", 0, rule),
       });
    }
    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " String bugga() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "   throw new Exception( \"My Exception\" );" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "   throw e;" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "   return \"A. O. K.\"; // Very bad." + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " String getBar() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "   return \"buz\";" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "   return \"biz\";" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "   return \"fiddle!\"; // bad!" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    "  String getBar() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "   return \"buz\";" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
