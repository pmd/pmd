/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mozilla.javascript.ast.AstRoot;

/**
 * See the following bugs: #1141 ECMAScript: getFinallyBlock() is buggy. #1142
 * ECMAScript: getCatchClause() is buggy
 */
class ASTTryStatementTest extends EcmascriptParserTestBase {

    private ASTTryStatement getTryStmt(String js) {
        EcmascriptNode<AstRoot> node = this.js.parse(js);
        List<ASTTryStatement> trys = node.findDescendantsOfType(ASTTryStatement.class);
        assertEquals(1, trys.size());
        ASTTryStatement tryStmt = trys.get(0);
        return tryStmt;
    }

    @Test
    void testFinallyBlockOnly() {
        ASTTryStatement tryStmt = getTryStmt("function() { try { } finally { } }");
        assertNull(tryStmt.getCatchClause(0));
        assertFalse(tryStmt.hasCatch());
        assertEquals(0, tryStmt.getNumCatchClause());
        assertNotNull(tryStmt.getFinallyBlock());
        assertTrue(tryStmt.hasFinally());
    }

    @Test
    void testCatchBlockOnly() {
        ASTTryStatement tryStmt = getTryStmt("function() { try { } catch (error) { } }");
        assertNotNull(tryStmt.getCatchClause(0));
        assertTrue(tryStmt.hasCatch());
        assertEquals(1, tryStmt.getNumCatchClause());
        assertNull(tryStmt.getFinallyBlock());
        assertFalse(tryStmt.hasFinally());
    }

    @Test
    void testCatchAndFinallyBlock() {
        ASTTryStatement tryStmt = getTryStmt("function() { try { } catch (error) { } finally { } }");
        assertNotNull(tryStmt.getCatchClause(0));
        assertTrue(tryStmt.hasCatch());
        assertEquals(1, tryStmt.getNumCatchClause());
        assertNotNull(tryStmt.getFinallyBlock());
        assertTrue(tryStmt.hasFinally());
    }

    @Test
    void testMultipleCatchAndFinallyBlock() {
        ASTTryStatement tryStmt = getTryStmt(
                "function() { " + "try { } " + "catch (error if error instanceof BadError) { } "
                        + "catch (error2 if error2 instanceof OtherError) { } " + "finally { } }");
        assertNotNull(tryStmt.getCatchClause(0));
        assertNotNull(tryStmt.getCatchClause(1));
        assertTrue(tryStmt.hasCatch());
        assertEquals(2, tryStmt.getNumCatchClause());
        assertNotNull(tryStmt.getFinallyBlock());
        assertTrue(tryStmt.hasFinally());
    }
}
