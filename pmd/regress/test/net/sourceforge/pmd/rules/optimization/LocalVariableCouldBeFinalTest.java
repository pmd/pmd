/*
 * Created on Jan 10, 2005 
 *
 * $Id$
 */
package test.net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

/**
 * Tests for the rule LocalVariableCouldBeFinal
 * 
 * @author mgriffa
 */
public class LocalVariableCouldBeFinalTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "LocalVariableCouldBeFinal");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 0, rule),
               new TestDescriptor(TEST3, "TEST3", 0, rule),
               new TestDescriptor(TEST4, "TEST4", 0, rule),
               new TestDescriptor(TEST5, "TEST5", 2, rule),
               new TestDescriptor(TEST6, "TEST6", 0, rule),
               new TestDescriptor(TEST7, "TEST7", 0, rule),
               new TestDescriptor(TEST8, "TEST8", 0, rule),
               new TestDescriptor(TEST9, "TEST9", 1, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  int a = 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  final int a = 0;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  int a = 0;" + PMD.EOL +
        "  a = 100;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  int a = 0;" + PMD.EOL +
        "  a += 100;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    /*
     * It can be discussed if this is a violation or not, 
     * not always the value of a, b is constant and he logic could of course be more complex
     * */
    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  int a = 0;" + PMD.EOL +
        "  int b = 0;" + PMD.EOL +
        "  int c ;" + PMD.EOL +
        "  c = a + b;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  int a = 0;" + PMD.EOL +
        "  ++a;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  int a = 0;" + PMD.EOL +
        "  a+=1;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  int a = 0;" + PMD.EOL +
        "  a++;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST9 =
        "public class Foo {" + PMD.EOL +
        " public void test1() {" + PMD.EOL +
        "  int a = 0;" + PMD.EOL +
        "  int b = 0;" + PMD.EOL +
        "  a++;" + PMD.EOL +
        "  a+=b;" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

}
