package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class StringInstantiationRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class StringInstantiation1 {" + CPD.EOL +
    " private String bar = new String(\"bar\");" + CPD.EOL +
    " private String baz = new String();" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class StringInstantiation2 {" + CPD.EOL +
    " private String[] bar = new String[5];" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class StringInstantiation3 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  byte[] bytes = new byte[50];" + CPD.EOL +
    "  String bar = new String(bytes, 0, bytes.length);" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class StringInstantiation4 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  byte[] bytes = new byte[50];" + CPD.EOL +
    "  String bar = new String(bytes, 0, bytes.length, \"some-encoding\");" + CPD.EOL +
    " }" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//AllocationExpression[Name/@Image='String'][count(.//Expression) < 2][not(ArrayDimsAndInits)]");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 2, rule);
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
    public void test4() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
}
