package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class ForLoopsMustUseBracesRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ForStatement[not(Statement/Block)]");
    }
    public void test1() throws Throwable {
        runTestFromString(TEST1,1, rule);
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2,0, rule);
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3,1, rule);
    }
    public void test4() throws Throwable {
        runTestFromString(TEST4,1, rule);
    }
    public void test5() throws Throwable {
        runTestFromString(TEST5,1, rule);
    }

    private static final String TEST1 =
    "public class ForLoopsNeedBraces1 {" + CPD.EOL +
    " public void foo() {" + CPD.EOL +
    "  for (int i=0; i<42;i++)" + CPD.EOL +
    "       foo();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class ForLoopsNeedBraces2 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (int i=0; i<42;i++) {" + CPD.EOL +
    "       foo();" + CPD.EOL +
    "  }" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class ForLoopsNeedBraces3 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (int i=0; i<42;) " + CPD.EOL +
    "       foo();" + CPD.EOL +
    "  " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class ForLoopsNeedBraces4 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (int i=0;;) " + CPD.EOL +
    "       foo();" + CPD.EOL +
    "  " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class ForLoopsNeedBraces5 {" + CPD.EOL +
    " public void foo() {   " + CPD.EOL +
    "  for (;;) " + CPD.EOL +
    "       foo();" + CPD.EOL +
    "  " + CPD.EOL +
    " }" + CPD.EOL +
    "}";
}
