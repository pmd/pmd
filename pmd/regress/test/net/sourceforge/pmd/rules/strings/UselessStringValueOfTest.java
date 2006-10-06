package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UselessStringValueOfTest extends SimpleAggregatorTst {

	private Rule rule;

	public void setUp() throws Exception {
		rule = findRule("rulesets/strings.xml", "UselessStringValueOf");
	}

	public void testAll() {
		runTests(new TestDescriptor[]{
				new TestDescriptor(TEST1, "valueOf in concatenation", 1, rule),
				new TestDescriptor(TEST2, "valueOf in String conversion", 0, rule),
				new TestDescriptor(TEST3, "valueOf as first expression in concatenation", 0, rule),
		});
	}

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "void bar(int i) {" + PMD.EOL +
            "    String s = \"abc\" + String.valueOf(i);" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "void bar(Object o) {" + PMD.EOL +
            "    String s = String.valueOf(o);" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            "void bar(int i) {" + PMD.EOL +
            "    char c = 'A';" + PMD.EOL +
            "    char low = 'B';" + PMD.EOL +
            "    String s = String.valueOf(c) + low;" + PMD.EOL +
            "}" + PMD.EOL +
            "}";

}
