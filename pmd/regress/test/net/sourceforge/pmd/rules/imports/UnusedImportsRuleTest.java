/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnusedImportsRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("imports", "UnusedImports");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "simple unused single type import", 1, rule),
            new TestDescriptor(TEST2, "one used single type import", 0, rule),
            new TestDescriptor(TEST3, "2 unused single-type imports", 2, rule),
            new TestDescriptor(TEST4, "1 used single type import", 0, rule),
            new TestDescriptor(TEST5, "1 import stmt, used only in throws clause", 0, rule),
        });
    }

    public void testForLoop() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST6, rule, rpt);
        assertEquals(0, rpt.size());
    }

    public void testGenerics() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST7, rule, rpt);
        assertEquals(0, rpt.size());
    }

    public void testAnnotations() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST8, rule, rpt);
        assertEquals(0, rpt.size());
    }

    public void testAnnotations2() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST9, rule, rpt);
        assertEquals(0, rpt.size());
    }

    private static final String TEST1 =
            "import java.io.File;" + PMD.EOL +
            "public class Foo {}";

    private static final String TEST2 =
            "import java.io.File;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " private File file;" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "import java.io.File;" + PMD.EOL +
            "import java.util.List;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "import java.security.AccessController;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " public void foo() {" + PMD.EOL +
            "  AccessController.doPrivileged(null);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "import java.rmi.RemoteException;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " public void foo() throws RemoteException {}" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "import java.util.ArrayList;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " public void foo(ArrayList list) {" + PMD.EOL +
            "  for (String s : list) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "import foo.TestInterfaceTwo;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            " private List<TestInterfaceTwo> x = new ArrayList<TestInterfaceTwo>();" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "import foo.annotation.Retention;" + PMD.EOL +
            "import foo.annotation.RetentionPolicy;" + PMD.EOL +
            "@Retention(RetentionPolicy.RUNTIME)" + PMD.EOL +
            "public @interface Foo {" + PMD.EOL +
            "}";

    private static final String TEST9 =
            "import foo.FooAnnotation1;" + PMD.EOL +
            "import foo.FooAnnotation2;" + PMD.EOL +
            "@FooAnnotation1" + PMD.EOL +
            "@FooAnnotation2" + PMD.EOL +
            "public class Foo {}";

}
