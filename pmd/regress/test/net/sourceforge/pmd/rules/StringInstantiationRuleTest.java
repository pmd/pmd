package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class StringInstantiationRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//AllocationExpression[Name/@Image='String'][count(.//Expression) < 2][not(ArrayDimsAndInits)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "new 'new String's", 2, rule),
           new TestDescriptor(TEST2, "new String array", 0, rule),
           new TestDescriptor(TEST3, "using multiple parameter constructor", 0, rule),
           new TestDescriptor(TEST4, "using 4 parameter constructor", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class StringInstantiation1 {" + PMD.EOL +
    " private String bar = new String(\"bar\");" + PMD.EOL +
    " private String baz = new String();" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class StringInstantiation2 {" + PMD.EOL +
    " private String[] bar = new String[5];" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class StringInstantiation3 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  byte[] bytes = new byte[50];" + PMD.EOL +
    "  String bar = new String(bytes, 0, bytes.length);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class StringInstantiation4 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  byte[] bytes = new byte[50];" + PMD.EOL +
    "  String bar = new String(bytes, 0, bytes.length, \"some-encoding\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


}
