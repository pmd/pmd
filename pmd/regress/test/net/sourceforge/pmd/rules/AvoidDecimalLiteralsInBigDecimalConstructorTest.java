package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidDecimalLiteralsInBigDecimalConstructorTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "AvoidDecimalLiteralsInBigDecimalConstructor");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "bad, new BigDecimal(.1)", 1, rule),
            new TestDescriptor(TEST2, "ok, new BigDecimal(\".1\")", 0, rule),
            new TestDescriptor(TEST3, "bad, same as #1 but outside an initializer context", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  BigDecimal bd = new BigDecimal(.1);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  BigDecimal bd = new BigDecimal(\".1\");" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " BigDecimal bar() {" + PMD.EOL +
            "  return new BigDecimal(1.0);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


}