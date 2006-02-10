/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidNonConstructorMethodsWithClassNameTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("naming", "AvoidNonConstructorMethodsWithClassName");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 1, rule),
            new TestDescriptor(TEST2, "TEST2", 1, rule),
            new TestDescriptor(TEST3, "TEST3", 2, rule),
            new TestDescriptor(TEST4, "TEST4", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "   public int Foo() {}" + PMD.EOL +
            "} ";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "   public Foo() {}" + PMD.EOL +
            "   public int Foo(Object o) {}" + PMD.EOL +
            "} ";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            "   public Foo() {}" + PMD.EOL +
            "   public int Foo(Object o) {}" + PMD.EOL +
            "   public int Foo(Object o, Object o1) {}" + PMD.EOL +
            "} ";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            "   public Foo() {}" + PMD.EOL +
            "   class Bar{" +
            "		void Bar(){}" +
            "   }" + PMD.EOL +
            "} ";

}
