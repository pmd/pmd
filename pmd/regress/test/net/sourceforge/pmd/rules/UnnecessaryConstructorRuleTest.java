package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class UnnecessaryConstructorRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ConstructorDeclaration[1][count(//ConstructorDeclaration)=1][@Public='true'][not(FormalParameters/*)][not(BlockStatement)][not(NameList)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure case", 1, rule),
           new TestDescriptor(TEST2, "private constructor", 0, rule),
           new TestDescriptor(TEST3, "constructor with arguments", 0, rule),
           new TestDescriptor(TEST4, "constructor with contents", 0, rule),
           new TestDescriptor(TEST5, "constructor throws exception", 0, rule),
           new TestDescriptor(TEST6, "two constructors", 0, rule)
       });
    }

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


}
