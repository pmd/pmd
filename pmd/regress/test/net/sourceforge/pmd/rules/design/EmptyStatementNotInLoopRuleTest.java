package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyStatementNotInLoopRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//Statement/EmptyStatement[not(../../../ForStatement or ../../../WhileStatement or ../../../../../../ForStatement/Statement[1]/Block[1]/BlockStatement[1]/Statement/EmptyStatement or ../../../../../../WhileStatement/Statement[1]/Block[1]/BlockStatement[1]/Statement/EmptyStatement)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok, semicolon after for", 0, rule),
           new TestDescriptor(TEST2, "ok, semicolon after while", 0, rule),
           new TestDescriptor(TEST3, "bad, random semicolon", 1, rule),
           new TestDescriptor(TEST4, "bad, double semicolon", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  for (int i=2; i<10; i++);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  while (i++ < 20);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  ;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public void bar() {" + PMD.EOL +
    "  int x = 2;;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
