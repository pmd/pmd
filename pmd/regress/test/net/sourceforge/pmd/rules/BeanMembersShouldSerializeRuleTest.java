package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.BeanMembersShouldSerializeRule;

public class BeanMembersShouldSerializeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new BeanMembersShouldSerializeRule();
        rule.setMessage("Don't {0} !");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "private String, no accessor", 1, rule),
           new TestDescriptor(TEST2, "private static String", 0, rule),
           new TestDescriptor(TEST3, "private transient String", 0, rule),
           new TestDescriptor(TEST4, "getter, no setter", 1, rule),
           new TestDescriptor(TEST5, "setter, no getter", 1, rule),
           new TestDescriptor(TEST6, "both accessors, yay!", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    " private String foo;" + CPD.EOL +
    " private String bar = foo;" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + CPD.EOL +
    " private static String foo;" + CPD.EOL +
    " private String bar = Foo.foo;" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + CPD.EOL +
    " private transient String foo;" + CPD.EOL +
    " private String bar = Foo.foo;" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + CPD.EOL +
    " private String foo;" + CPD.EOL +
    " private String bar = Foo.foo;" + CPD.EOL +
    " public String getFoo() {return foo;}" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + CPD.EOL +
    " private String foo;" + CPD.EOL +
    " private String bar = Foo.foo;" + CPD.EOL +
    " public void setFoo(Foo foo) {this.foo = foo;}" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + CPD.EOL +
    " private String foo;" + CPD.EOL +
    " private String bar = Foo.foo;" + CPD.EOL +
    " public void setFoo(Foo foo) {this.foo = foo;}" + CPD.EOL +
    " public String getFoo() {return foo;}" + CPD.EOL +
    "}";

}
