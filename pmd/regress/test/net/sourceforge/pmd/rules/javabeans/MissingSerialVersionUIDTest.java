/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.javabeans;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class MissingSerialVersionUIDTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("javabeans", "MissingSerialVersionUID");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "Happy case", 0, rule),
            new TestDescriptor(TEST2, "Simple failure case", 1, rule),
            new TestDescriptor(TEST3, "failure using java.io.Serializable", 1, rule),
            new TestDescriptor(TEST4, "implements Serializable and provides a serialVersionUID", 0, rule),
            new TestDescriptor(TEST5, "TEST5", 0, rule),
            new TestDescriptor(TEST6, "interface", 0, rule)
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo implements Serializable {" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo implements java.io.Serializable {" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo implements Serializable {" + PMD.EOL +
            "public static final long serialVersionUID = 43L;" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo implements java.io.Serializable {" + PMD.EOL +
            "public static final long serialVersionUID = 43L;" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public interface Foo implements Bar{" + PMD.EOL +
            " void getName();" + PMD.EOL +
            "}";
}
