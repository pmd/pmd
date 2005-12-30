/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UnusedFormalParameterRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = find();
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "failure case", 1, rule),
           new TestDescriptor(TEST2, "method called on param", 0, rule),
           new TestDescriptor(TEST3, "assignment to param", 0, rule),
           new TestDescriptor(TEST4, "interface", 0, rule),
           new TestDescriptor(TEST5, "don't flag public methods by default", 0, rule),
           new TestDescriptor(TEST6, "skip native/abstract methods", 0, rule),
           new TestDescriptor(TEST7, "anonymous inner class npe", 0, rule),
           new TestDescriptor(TEST8, "unused constructor param", 1, rule)
       });
    }

    public void testCheckPublicFlag() throws Throwable {
        Rule r = find();
        r.addProperty("checkall", "true");
        runTestFromString(TEST5, 1, r);
    }

    private Rule find() throws Exception {
        return findRule("unusedcode", "UnusedFormalParameter");
    }

    private static final String TEST1 =
    "class Foo {" + PMD.EOL +
    " private void bar(String x) {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "class Foo {" + PMD.EOL +
    " private void foo (String s) {" + PMD.EOL +
    "  String str = s.toString();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "class Foo {" + PMD.EOL +
    " private void bar(int s) {" + PMD.EOL +
    "  s = 2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public interface Foo {" + PMD.EOL +
    " void foo(int x);" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "class Foo {" + PMD.EOL +
    " public void bar(int s) {}" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "class Foo {" + PMD.EOL +
    " public native void bar(int s);" + PMD.EOL +
    " protected abstract void foo(Long s);" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  ActionListener a  = new ActionListener() {" + PMD.EOL +
    "   public void event(Event e) {}" + PMD.EOL +
    "  };" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "class Foo {" + PMD.EOL +
    " public Foo(int s) {}" + PMD.EOL +
    "}";
}
