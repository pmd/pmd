/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 1:56:19 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptyCatchBlockRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//TryStatement[@Catch='true']"
                + "/Block"
                + "[position() > 1]"
                + "[count(*) = 0]"
                + "[../@Finally='false' or following-sibling::Block]");
    }

    public void testSimple() throws Throwable {
        runTest("EmptyCatchBlock1.java", 1, rule);
    }

    public void testNotEmpty() throws Throwable {
        runTest("EmptyCatchBlock2.java", 0, rule);
    }

    public void testNoCatchWithNestedCatchInFinally() throws Throwable {
        runTest("EmptyCatchBlock3.java", 1, rule);
    }

    public void testMultipleCatchBlocks() throws Throwable {
        runTest("EmptyCatchBlock4.java", 2, rule);
    }

    public void testEmptyTryAndFinally() throws Throwable {
        runTest("EmptyCatchBlock5.java", 0, rule);
    }
}

