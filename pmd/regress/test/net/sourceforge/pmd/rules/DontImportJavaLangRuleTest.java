package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class DontImportJavaLangRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ImportDeclaration"
                + "[starts-with(Name/@Image, 'java.lang')]"
                + "[not(starts-with(Name/@Image, 'java.lang.ref'))]"
                + "[not(starts-with(Name/@Image, 'java.lang.reflect'))]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 1, rule),
           new TestDescriptor(TEST3, "", 0, rule),
       });
    }

    private static final String TEST1 =
    "import java.lang.String;" + PMD.EOL +
    "" + PMD.EOL +
    "public class DontImportJavaLang1 {}";

    private static final String TEST2 =
    "import java.lang.*;" + PMD.EOL +
    "" + PMD.EOL +
    "public class DontImportJavaLang2 {}";

    private static final String TEST3 =
    "import java.lang.ref.*;" + PMD.EOL +
    "import java.lang.reflect.*;" + PMD.EOL +
    "" + PMD.EOL +
    "public class DontImportJavaLang3 {}";

}
