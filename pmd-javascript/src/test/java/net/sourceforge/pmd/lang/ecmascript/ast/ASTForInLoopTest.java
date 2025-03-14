/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ASTForInLoopTest extends EcmascriptParserTestBase {

    /**
     * Note: for each loops are deprecated.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/for_each...in">for each...in</a>
     */
    @Test
    void testForEachLoop() {
        ASTAstRoot node = js.parse("for each (var item in items) {}");
        ASTForInLoop loop = (ASTForInLoop) node.getChild(0);
        assertFalse(loop.isForOf());
        assertTrue(loop.isForEach());
    }

    @Test
    void testForOfLoop() {
        ASTAstRoot node = js.parse("for (var item of items) {}");
        ASTForInLoop loop = (ASTForInLoop) node.getChild(0);
        assertTrue(loop.isForOf());
        assertFalse(loop.isForEach());
    }
}
