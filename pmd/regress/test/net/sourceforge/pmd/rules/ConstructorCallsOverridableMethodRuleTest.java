package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ConstructorCallsOverridableMethodRule;
import net.sourceforge.pmd.cpd.CPD;

public class ConstructorCallsOverridableMethodRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, new ConstructorCallsOverridableMethodRule()),
           new TestDescriptor(TEST2, "", 1, new ConstructorCallsOverridableMethodRule()),
           new TestDescriptor(TEST3, "", 1, new ConstructorCallsOverridableMethodRule()),
           new TestDescriptor(TEST4, "", 0, new ConstructorCallsOverridableMethodRule()),
           new TestDescriptor(TEST5, "", 1, new ConstructorCallsOverridableMethodRule()),
           // FIXME
           new TestDescriptor(TEST6, "calling method on literal bug", 0, new ConstructorCallsOverridableMethodRule()),
           //new TestDescriptor(TEST7, "method in anonymous inner class is ok", 0, new ConstructorCallsOverridableMethodRule()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    " public Foo() {" + CPD.EOL +
    "  bar();" + CPD.EOL +
    " }" + CPD.EOL +
    " public void bar() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + CPD.EOL +
    " public Foo() {" + CPD.EOL +
    "  bar();" + CPD.EOL +
    " }" + CPD.EOL +
    " protected void bar() {}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + CPD.EOL +
    " public Foo() {" + CPD.EOL +
    "  bar();" + CPD.EOL +
    " }" + CPD.EOL +
    " void bar() {}" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + CPD.EOL +
    " public Foo() {" + CPD.EOL +
    "  bar();" + CPD.EOL +
    " }" + CPD.EOL +
    " private void bar() {}" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + CPD.EOL +
    " public Foo() {" + CPD.EOL +
    "  this(\"Bar\");" + CPD.EOL +
    " }" + CPD.EOL +
    " private Foo(String bar) {" + CPD.EOL +
    "  bar();" + CPD.EOL +
    " }" + CPD.EOL +
    " public void bar() {}" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + CPD.EOL +
    " public Foo(String s) {" + CPD.EOL +
    "  \"foo\".equals(s);" + CPD.EOL +
    " }" + CPD.EOL +
    " public void equals(String bar) {}" + CPD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + CPD.EOL +
    " public Foo(String s) {" + CPD.EOL +
    "  addActionListener(new ActionListener() {" + CPD.EOL +
    "   public void actionPerformed(ActionEvent e) {bar();}" + CPD.EOL +
    "  });" + CPD.EOL +
    " }" + CPD.EOL +
    " public void bar() {}" + CPD.EOL +
    "}";


}
