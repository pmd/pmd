/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.ExcessiveImportsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ExcessiveImportsRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new ExcessiveImportsRule();
        rule.addProperty("minimum", "3");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad", 1, rule),
           new TestDescriptor(TEST2, "ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "import java.util.Vector;" + PMD.EOL +
    "import java.util.Vector;" + PMD.EOL +
    "import java.util.Vector;" + PMD.EOL +
    "import java.util.Vector;" + PMD.EOL +
    "public class Foo{}";

    private static final String TEST2 =
    "import java.util.Vector;" + PMD.EOL +
    "public class Foo{}";


}
