package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
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
    "import java.lang.String;" + CPD.EOL +
    "" + CPD.EOL +
    "public class DontImportJavaLang1 {}";

    private static final String TEST2 =
    "import java.lang.*;" + CPD.EOL +
    "" + CPD.EOL +
    "public class DontImportJavaLang2 {}";

    private static final String TEST3 =
    "import java.lang.ref.*;" + CPD.EOL +
    "import java.lang.reflect.*;" + CPD.EOL +
    "" + CPD.EOL +
    "public class DontImportJavaLang3 {}";

}
