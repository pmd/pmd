package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.design.UseSingletonRule;
import net.sourceforge.pmd.stat.Metric;
import test.net.sourceforge.pmd.rules.RuleTst;

public class UseSingletonRuleTest extends RuleTst implements ReportListener {

    public void testAllStaticsPublicConstructor() throws Throwable {
        runTestFromString(TEST1, 1, new UseSingletonRule());
    }

    public void testOKDueToNonStaticMethod() throws Throwable {
        runTestFromString(TEST2, 0, new UseSingletonRule());
    }

    public void testNoConstructorCoupleOfStatics() throws Throwable {
        runTestFromString(TEST3, 1, new UseSingletonRule());
    }

    public void testNoConstructorOneStatic() throws Throwable {
        runTestFromString(TEST4, 0, new UseSingletonRule());
    }

    public void testClassicSingleton() throws Throwable {
        runTestFromString(TEST5, 0, new UseSingletonRule());
    }

    public void testAbstractSingleton() throws Throwable {
        runTestFromString(TEST6, 0, new UseSingletonRule());
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

    private int callbacks;

    public void ruleViolationAdded(RuleViolation ruleViolation) {
        callbacks++;
    }

    public void metricAdded(Metric metric) {
    }

    private static final String TEST1 =
    "public class Foo {" + CPD.EOL +
    " // Should trigger UseSingleton rule?" + CPD.EOL +
    " public Foo() { }" + CPD.EOL +
    " public static void doSomething() {}" + CPD.EOL +
    " public static void main(String args[]) {" + CPD.EOL +
    "  doSomething();" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class UseSingleton2" + CPD.EOL +
    "{" + CPD.EOL +
    "    // Should not trigger UseSingleton rule." + CPD.EOL +
    "    public UseSingleton2() { }" + CPD.EOL +
    "    public void doSomething() { }" + CPD.EOL +
    "    public static void main(String args[]) { }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class UseSingleton3" + CPD.EOL +
    "{" + CPD.EOL +
    "    // Should trigger it." + CPD.EOL +
    "    public static void doSomething1() { }" + CPD.EOL +
    "    public static void doSomething2() { }" + CPD.EOL +
    "    public static void doSomething3() { }" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class UseSingleton4" + CPD.EOL +
    "{" + CPD.EOL +
    "    public UseSingleton4() { }" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class UseSingleton5 {" + CPD.EOL +
    " private UseSingleton5() {}" + CPD.EOL +
    " public static UseSingleton5 get() {" + CPD.EOL +
    "  return null;" + CPD.EOL +
    " }     " + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public abstract class Foo {" + CPD.EOL +
    "    public static void doSomething1() { }" + CPD.EOL +
    "    public static void doSomething2() { }" + CPD.EOL +
    "    public static void doSomething3() { }" + CPD.EOL +
    "}";

}
