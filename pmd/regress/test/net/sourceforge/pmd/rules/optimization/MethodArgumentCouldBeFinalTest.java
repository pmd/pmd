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
public class MethodArgumentCouldBeFinalTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/optimizations.xml", "MethodArgumentCouldBeFinal");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 2, rule),
               new TestDescriptor(TEST3, "TEST3", 2, rule),
               new TestDescriptor(TEST4, "TEST4", 1, rule),
               new TestDescriptor(TEST5, "TEST5", 1, rule),
               new TestDescriptor(TEST6, "TEST6", 0, rule),
               new TestDescriptor(TEST7, "BUG 1117983, false +", 0, rule),
       });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        " public void bar(int a) {}" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " public void bar(int a, Object o) {}" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " public void bar(int a, Object o) {" + PMD.EOL +
        "  int z = a;" + PMD.EOL +
        "  Object x = o.clone();" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " public void bar(final int a, Object o) {" + PMD.EOL +
        "  int z = a;" + PMD.EOL +
        "  Object x = o.clone();" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " public void bar(int a, final Object o) {" + PMD.EOL +
        "  int z = a;" + PMD.EOL +
        "  Object x = o.clone();" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    
    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " public void bar(final int a, final Object o) {" + PMD.EOL +
        "  int z = a;" + PMD.EOL +
        "  Object x = o.clone();" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        " protected void method() { }" + PMD.EOL +
        " public void bar(final List batch) {" + PMD.EOL +
        "   try { " + PMD.EOL +
        "   method(); " + PMD.EOL +
        "   } catch (final Exception e) {" + PMD.EOL +
        "       for (Iterator it = batch.iterator() ; it.hasNext() ; ) {" + PMD.EOL +
        "           try {" + PMD.EOL +
        "               method(); " + PMD.EOL +
        "           } catch (final Exception ee) { " + PMD.EOL +
        "               throw new RuntimeException(ee);" + PMD.EOL +
        "           }" + PMD.EOL +
        "       }" + PMD.EOL +
        "   }" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

}
