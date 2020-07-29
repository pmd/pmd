/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.processor.PmdRunnable;
import net.sourceforge.pmd.testframework.RuleTst;
import net.sourceforge.pmd.testframework.TestDescriptor;
import net.sourceforge.pmd.util.datasource.DataSource;

public class ExcludeLinesTest extends RuleTst {
    private Rule rule;

    @Before
    public void setUp() {
        rule = findRule("java-unusedcode", "UnusedLocalVariable");
    }

    @Test
    public void testAcceptance() {
        runTest(new TestDescriptor(TEST1, "NOPMD should work", 0, rule));
        runTest(new TestDescriptor(TEST2, "Should fail without exclude marker", 1, rule));
    }

    @Test
    public void testAlternateMarker() throws Exception {
        PMDConfiguration config = new PMDConfiguration();
        config.setSuppressMarker("FOOBAR");

        RuleSet rules = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);

        Report r = new PmdRunnable(
            DataSource.forString(TEST3, "test.java"),
            new RuleContext(),
            listOf(rules),
            config
        ).call();

        assertTrue(r.getViolations().isEmpty());
        assertEquals(r.getSuppressedViolations().size(), 1);
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int x; //NOPMD "
            + PMD.EOL + " } " + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int x;"
            + PMD.EOL + " } " + PMD.EOL + "}";

    private static final String TEST3 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  int x; // FOOBAR" + PMD.EOL + " } " + PMD.EOL + "}";
}
