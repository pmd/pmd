/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.design.UseSingletonRule;
import net.sourceforge.pmd.stat.Metric;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class UseSingletonRuleTest extends SimpleAggregatorTst implements ReportListener {

    private int callbacks;

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "should be singleton since all static, public constructor", 1, new UseSingletonRule()),
           new TestDescriptor(TEST2, "ok, uses non-static", 0, new UseSingletonRule()),
           new TestDescriptor(TEST3, "should be singleton, couple of statics, no constructor", 1, new UseSingletonRule()),
           new TestDescriptor(TEST4, "no constructor, one static - ok", 0, new UseSingletonRule()),
           new TestDescriptor(TEST5, "classic singleton - ok", 0, new UseSingletonRule()),
           new TestDescriptor(TEST6, "abstract, so ok", 0, new UseSingletonRule()),
           new TestDescriptor(TEST7, "has no fields, so ok", 0, new UseSingletonRule()),
       });
    }

    public void testResetState() throws Throwable {
        callbacks = 0;
        Rule rule = new UseSingletonRule();
        Report report = new Report();
        report.addListener(this);
        runTestFromString(TEST3, rule, report);
        runTestFromString(TEST4, rule, report);
        assertEquals(1, callbacks);
    }

    public void ruleViolationAdded(RuleViolation ruleViolation) {
        callbacks++;
    }
    public void metricAdded(Metric metric) {}

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public Foo() { }" + PMD.EOL +
    " public static void doSomething() {}" + PMD.EOL +
    " public static void main(String args[]) {" + PMD.EOL +
    "  doSomething();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "    public Foo() { }" + PMD.EOL +
    "    public void doSomething() { }" + PMD.EOL +
    "    public static void main(String args[]) { }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    "    public static void doSomething1() { }" + PMD.EOL +
    "    public static void doSomething2() { }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    "    public Foo() { }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " private Foo() {}" + PMD.EOL +
    " public static Foo get() {" + PMD.EOL +
    "  return null;" + PMD.EOL +
    " }     " + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public abstract class Foo {" + PMD.EOL +
    "    public static void doSomething1() { }" + PMD.EOL +
    "    public static void doSomething2() { }" + PMD.EOL +
    "    public static void doSomething3() { }" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    " public Foo() { }" + PMD.EOL +
    " private int x;" + PMD.EOL +
    " public static void doSomething() {}" + PMD.EOL +
    "}";
}
