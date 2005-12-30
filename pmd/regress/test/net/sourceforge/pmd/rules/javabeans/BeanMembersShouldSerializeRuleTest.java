/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.javabeans;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class BeanMembersShouldSerializeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("javabeans", "BeanMembersShouldSerialize");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "private String, no accessor", 1, rule),
           new TestDescriptor(TEST2, "private static String", 0, rule),
           new TestDescriptor(TEST3, "private transient String", 0, rule),
           new TestDescriptor(TEST4, "getter, no setter", 1, rule),
           new TestDescriptor(TEST5, "setter, no getter", 1, rule),
           new TestDescriptor(TEST6, "both accessors, yay!", 0, rule),
           new TestDescriptor(TEST7, "setFoo and isFoo is OK for booleans", 0, rule),
           new TestDescriptor(TEST8, "setFoo and isFoo is not OK for Strings", 1, rule),
           new TestDescriptor(TEST9, "prefix is off by default", 1, rule),
       });
    }

    public void testPrefixProperty() throws Throwable {
        Rule lclrule = findRule("javabeans", "BeanMembersShouldSerialize");
        lclrule.addProperty("prefix", "m_");
        runTestFromString(TEST10, 0, lclrule);
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

    private static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    " private boolean foo;" + PMD.EOL +
    " public void setFoo(boolean foo) {this.foo = foo;}" + PMD.EOL +
    " public boolean isFoo() {return foo;}" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " public void setFoo(String foo) {this.foo = foo;}" + PMD.EOL +
    " public String isFoo() {return foo;}" + PMD.EOL +
    "}";

    private static final String TEST9 =
    "public class Foo {" + PMD.EOL +
    " private String m_foo;" + PMD.EOL +
    " public void setFoo(String foo) {m_foo = foo;}" + PMD.EOL +
    " public String getFoo() {return m_foo;}" + PMD.EOL +
    "}";

    private static final String TEST10 =
    "public class Foo {" + PMD.EOL +
    " private String m_foo;" + PMD.EOL +
    " public void setFoo(String foo) {m_foo = foo;}" + PMD.EOL +
    " public String getFoo() {return m_foo;}" + PMD.EOL +
    "}";

}
