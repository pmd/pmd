package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class UnnecessaryConstructorRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class UnnecessaryConstructor1 {" + CPD.EOL +
    " public UnnecessaryConstructor1() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class UnnecessaryConstructor2 {" + CPD.EOL +
    " private UnnecessaryConstructor2() {}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class UnnecessaryConstructor3 {" + CPD.EOL +
    " public UnnecessaryConstructor3(int x) {}" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class UnnecessaryConstructor4 {" + CPD.EOL +
    " public UnnecessaryConstructor4() {  " + CPD.EOL +
    "  int x = 2;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class UnnecessaryConstructor5 {" + CPD.EOL +
    " public UnnecessaryConstructor5() throws IOException {  " + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class UnnecessaryConstructor6 {" + CPD.EOL +
    " public UnnecessaryConstructor6() {" + CPD.EOL +
    " }" + CPD.EOL +
    " public UnnecessaryConstructor6(String foo) {}" + CPD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ConstructorDeclaration[1][count(//ConstructorDeclaration)=1][@Public='true'][not(FormalParameters/*)][not(BlockStatement)][not(NameList)]");
    }

    public void testSimpleFailureCase() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testPrivate() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
    public void testHasArgs() throws Throwable {
        runTestFromString(TEST3, 0, rule);
    }
    public void testHasBody() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void testHasExceptions() throws Throwable {
        runTestFromString(TEST5, 0, rule);
    }
    public void testMultipleConstructors() throws Throwable {
        runTestFromString(TEST6, 0, rule);
    }
}
