package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.MethodNamingConventionsRule;

public class MethodNamingConventionsRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "method names should start with lowercase character", 1, new MethodNamingConventionsRule()),
           new TestDescriptor(TEST2, "method names should not contain underscores", 1, new MethodNamingConventionsRule()),
           new TestDescriptor(TEST3, "all is well", 0, new MethodNamingConventionsRule()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    " void Bar() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + CPD.EOL +
    " void bar_foo() {}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + CPD.EOL +
    " void foo() {}" + CPD.EOL +
    "}";
}
