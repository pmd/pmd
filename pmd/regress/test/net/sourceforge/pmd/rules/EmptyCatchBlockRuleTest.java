/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 1:56:19 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.EmptyCatchBlockRule;

public class EmptyCatchBlockRuleTest extends RuleTst {

    public void testEmptyCatchBlock() throws Throwable {
        runTest("EmptyCatchBlock.java", 1, new EmptyCatchBlockRule());
    }

    public void testEmptyCatchBlock2() throws Throwable {
        runTest("EmptyCatchBlock2.java", 0, new EmptyCatchBlockRule());
    }

    public void testEmptyCatchBlock3() throws Throwable {
        runTest("EmptyCatchBlock3.java", 1, new EmptyCatchBlockRule());
    }


}

