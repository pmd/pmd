/*
 * User: tom
 * Date: Jul 10, 2002
 * Time: 3:22:57 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.EmptyFinallyBlockRule;

public class EmptyFinallyBlockRuleTest extends RuleTst {

    public void testEmptyFinallyBlock1() throws Throwable {
        runTest("EmptyFinallyBlock1.java", 1, new EmptyFinallyBlockRule());
    }

    public void testEmptyFinallyBlock2() throws Throwable {
        runTest("EmptyFinallyBlock2.java", 1, new EmptyFinallyBlockRule());
    }

    public void testEmptyFinallyBlock3() throws Throwable {
        runTest("EmptyFinallyBlock3.java", 0, new EmptyFinallyBlockRule());
    }

    public void testMultipleCatchBlocksWithFinally() throws Throwable {
        runTest("EmptyFinallyBlock4.java", 1, new EmptyFinallyBlockRule());
    }
}
