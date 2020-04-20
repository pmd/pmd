/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.ecmascript.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTForInLoopTest extends EcmascriptParserTestBase {

    /**
     * Note: for each loops are deprecated.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/for_each...in">for each...in</a>
     */
    @Test
    public void testForEachLoop() {
        ASTAstRoot node = js.parse("for each (var item in items) {}");
        ASTForInLoop loop = (ASTForInLoop) node.getChild(0);
        Assert.assertFalse(loop.isForOf());
        Assert.assertTrue(loop.isForEach());
    }

    @Test
    public void testForOfLoop() {
        ASTAstRoot node = js.parse("for (var item of items) {}");
        ASTForInLoop loop = (ASTForInLoop) node.getChild(0);
        Assert.assertTrue(loop.isForOf());
        Assert.assertFalse(loop.isForEach());
    }
}
