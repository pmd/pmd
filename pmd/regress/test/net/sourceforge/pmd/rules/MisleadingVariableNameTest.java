package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MisleadingVariableNameTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("naming", "MisleadingVariableName");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "misnamed param", 1, rule),
            new TestDescriptor(TEST2, "misnamed local", 1, rule),
            new TestDescriptor(TEST3, "all's well", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "	void main(String m_foo) {" + PMD.EOL +
            "	}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "	void main() {" + PMD.EOL +
            "	 int m_foo = 42;" + PMD.EOL +
            "	}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            "	private int m_bar;" + PMD.EOL +
            "	public static final int m_buz = 42;" + PMD.EOL +
            "	private void buz(String biz) {}" + PMD.EOL +
            "}";
}
