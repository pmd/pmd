/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.BeanMembersShouldSerializeRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

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
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = foo;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private static String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private transient String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    " public String getFoo() {return foo;}" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    " public void setFoo(Foo foo) {this.foo = foo;}" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    " public void setFoo(Foo foo) {this.foo = foo;}" + PMD.EOL +
    " public String getFoo() {return foo;}" + PMD.EOL +
    "}";

}
