package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SuspiciousHashcodeMethodNameRuleTest  extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclaration[ResultType//PrimitiveType[@Image='int'][//MethodDeclarator[@Image='hashcode' or @Image='HashCode' or @Image='Hashcode'][not(FormalParameters/*)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "ok", 0, rule),
           new TestDescriptor(TEST2, "hashcode", 1, rule),
           new TestDescriptor(TEST3, "HashCode", 1, rule),
           new TestDescriptor(TEST4, "Hashcode", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public int hashCode() {return 42;}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public int hashcode() {return 42;}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public int HashCode() {return 42;}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public int Hashcode() {return 42;}" + PMD.EOL +
    "}";

}
