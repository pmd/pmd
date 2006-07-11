/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class OverrideBothEqualsAndHashcodeTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "OverrideBothEqualsAndHashcode");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "hash code only", 1, rule),
            new TestDescriptor(TEST2, "equals only", 1, rule),
            new TestDescriptor(TEST3, "overrides both", 0, rule),
            new TestDescriptor(TEST4, "overrides neither", 0, rule),
            new TestDescriptor(TEST5, "equals sig uses String, not Object", 1, rule),
            new TestDescriptor(TEST6, "interface", 0, rule),
            new TestDescriptor(TEST7, "java.lang.Object", 0, rule),
            new TestDescriptor(TEST8, "skip Comparable implementations", 0, rule),
            new TestDescriptor(TEST9, "implements equals but with 2 args", 0, rule),
            new TestDescriptor(TEST10, "overloaded hashCode", 0, rule),
            new TestDescriptor(TEST11, "overloaded both", 0, rule),
            new TestDescriptor(TEST12, "overloaded hashCode, should fail on equals", 1, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public int hashCode() {}" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public boolean equals(Object other) {}" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public boolean equals(Object other) {}" + PMD.EOL +
            " public int hashCode() {}" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " public boolean equals(String o) {" + PMD.EOL +
            "  return true;" + PMD.EOL +
            " }" + PMD.EOL +
            " public int hashCode() {" + PMD.EOL +
            "  return 0;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public interface Foo {" + PMD.EOL +
            " public boolean equals(Object o);" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " public boolean equals(java.lang.Object o) {" + PMD.EOL +
            "  return true;" + PMD.EOL +
            " }" + PMD.EOL +
            " public int hashCode() {" + PMD.EOL +
            "  return 0;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo implements Comparable {" + PMD.EOL +
            " public boolean equals(Object other) { return false; }" + PMD.EOL +
            " public int compareTo(Object other) { return 42; }" + PMD.EOL +
            "}";

    private static final String TEST9 =
        "public class Foo {" + PMD.EOL +
        " public boolean equals(java.lang.Object o1, java.lang.Object o2) {" + PMD.EOL +
        "  return true;" + PMD.EOL +
        " }" + PMD.EOL +
        " public int hashCode(java.lang.Object o) {" + PMD.EOL +
        "  return 0;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    private static final String TEST10 =
        "public class Foo  {" + PMD.EOL +
        " public int hashCode(Object o1) { return false; }" + PMD.EOL +
        "}";

    private static final String TEST11 =
        "public class Foo  {" + PMD.EOL +
        " public boolean equals(Object o1,Object o2) { return false; }" + PMD.EOL +
        " public int hashCode(Object o1) { return false; }" + PMD.EOL +
        "}";

    private static final String TEST12 =
        "public class Foo  {" + PMD.EOL +
        " public boolean equals(Object o1) { return false; }" + PMD.EOL +
        " public int hashCode(Object o1) { return false; }" + PMD.EOL +
        "}";
}
