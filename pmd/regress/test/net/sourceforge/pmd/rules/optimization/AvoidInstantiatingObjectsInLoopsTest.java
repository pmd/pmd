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
 * Tests for the rule AvoidInstantiatingObjectsInLoops
 *
 * @author mgriffa
 */
public class AvoidInstantiatingObjectsInLoopsTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("optimizations", "AvoidInstantiatingObjectsInLoops");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "TEST1", 1, rule),
            new TestDescriptor(TEST2, "TEST2", 1, rule),
            new TestDescriptor(TEST3, "TEST3", 1, rule),
            new TestDescriptor(TEST4, "TEST4", 2, rule),
            new TestDescriptor(TEST5, "throw new is OK", 0, rule),
            new TestDescriptor(TEST6, "return new in loop is OK", 0, rule),
            //new TestDescriptor(BUG_1114051, "BUG [ 1114051 ] Semi-false positive for instantiating new object in loop", 0, rule), //FIXME
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "   for(;;) {" + PMD.EOL +
            "       String a = new String();" + PMD.EOL +
            "   }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "   while(true) {" + PMD.EOL +
            "       String a = new String();" + PMD.EOL +
            "   }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "   do{" + PMD.EOL +
            "       String a = new String();" + PMD.EOL +
            "   }while(true) ;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "   do{" + PMD.EOL +
            "       String a = new String();" + PMD.EOL +
            "       String b = new String();" + PMD.EOL +
            "   }while(true) ;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "   for(;;) {" + PMD.EOL +
            "       throw new Exception();" + PMD.EOL +
            "   }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " public String test1() {" + PMD.EOL +
            "   for(;;) {" + PMD.EOL +
            "       return new String();" + PMD.EOL +
            "   }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String BUG_1114051 =
            "public class Foo {" + PMD.EOL +
            " public void test1() {" + PMD.EOL +
            "   while (true) {" +
            "        try {" +
            "       } catch (Exception e) {" + PMD.EOL +
            "           url = new StringBuffer(); " + PMD.EOL +
            "           break; " + PMD.EOL +
            "       }" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
