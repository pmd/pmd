package test.net.sourceforge.pmd.rules;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.XPathRule;

public class DontImportSunRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ImportDeclaration[starts-with(Name/@Image, 'sun.')][not(starts-with(Name/@Image, 'sun.misc.Signal'))]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "bad, import from sun.", 1, rule),
           new TestDescriptor(TEST2, "ok, signal is ok", 0, rule),
       });
    }

    private static final String TEST1 =
    "import sun.Foo;" + PMD.EOL +
    "public class Bar {}";

    private static final String TEST2 =
    "import sun.misc.Signal;" + PMD.EOL +
    "public class Bar {}";
}
