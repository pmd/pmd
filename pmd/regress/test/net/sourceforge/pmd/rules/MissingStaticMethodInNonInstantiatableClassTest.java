/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MissingStaticMethodInNonInstantiatableClassTest extends SimpleAggregatorTst {
    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/design.xml", "MissingStaticMethodInNonInstantiatableClass");
    }
    
    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "TEST1", 0, rule),
               new TestDescriptor(TEST2, "TEST2", 0, rule),
               new TestDescriptor(TEST3, "TEST3", 1, rule),
               new TestDescriptor(TEST4, "TEST4", 1, rule),
               new TestDescriptor(TEST5, "TEST5", 0, rule),
               new TestDescriptor(TEST6, "TEST6", 0, rule),
       });
    }
    
    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        "}";
    
    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " public void bar() {} ;" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " private Foo(){};" + PMD.EOL +
        " public void bar() {} ;" + PMD.EOL +
        "}";

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " private Foo(){};" + PMD.EOL +
        " private Foo(Object o){};" + PMD.EOL +
        " public void bar() {} ;" + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " private Foo(){};" + PMD.EOL +
        " protected Foo(Object o){};" + PMD.EOL +
        " public void bar() {} ;" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " private Foo(){};" + PMD.EOL +
        " private Foo(Object o){};" + PMD.EOL +
        " public static void bar() {} ;" + PMD.EOL +
        "}";

}
