package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UselessOperationOnImmutableTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = findRule("basic", "UselessOperationOnImmutable");
    }

    // TODO - should use symbol table to catch all misuses of these types
    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "useless operation on BigDecimal", 1, rule),
            new TestDescriptor(TEST2, "useless operation on BigInteger", 1, rule),
            new TestDescriptor(TEST3, "using the result, so OK", 0, rule),
            new TestDescriptor(TEST4, "using the result in a method call, so OK", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  BigDecimal bd = new BigDecimal(5);" + PMD.EOL +
            "  bd.add(new BigDecimal(5));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  BigInteger bi = new BigInteger(5);" + PMD.EOL +
            "  bi.add(new BigInteger(5));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  BigInteger bi = new BigInteger(5);" + PMD.EOL +
            "  bi = bi.add(new BigInteger(5));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  BigInteger bi = new BigInteger(5);" + PMD.EOL +
            "  bar(bi.add(new BigInteger(5)));" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
