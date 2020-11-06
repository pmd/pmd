/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

/**
 * See the following bugs: #1141 ECMAScript: getFinallyBlock() is buggy. #1142
 * ECMAScript: getCatchClause() is buggy
 */
public class ASTTryStatementTest extends EcmascriptParserTestBase {

    private ASTTryStatement getTryStmt(String js) {
        EcmascriptNode<AstRoot> node = this.js.parse(js);
        List<ASTTryStatement> trys = node.findDescendantsOfType(ASTTryStatement.class);
        Assert.assertEquals(1, trys.size());
        ASTTryStatement tryStmt = trys.get(0);
        return tryStmt;
    }

    @Test
    public void testFinallyBlockOnly() {
        ASTTryStatement tryStmt = getTryStmt("function() { try { } finally { } }");
        Assert.assertNull(tryStmt.getCatchClause(0));
        Assert.assertFalse(tryStmt.hasCatch());
        Assert.assertEquals(0, tryStmt.getNumCatchClause());
        Assert.assertNotNull(tryStmt.getFinallyBlock());
        Assert.assertTrue(tryStmt.hasFinally());
    }

    @Test
    public void testCatchBlockOnly() {
        ASTTryStatement tryStmt = getTryStmt("function() { try { } catch (error) { } }");
        Assert.assertNotNull(tryStmt.getCatchClause(0));
        Assert.assertTrue(tryStmt.hasCatch());
        Assert.assertEquals(1, tryStmt.getNumCatchClause());
        Assert.assertNull(tryStmt.getFinallyBlock());
        Assert.assertFalse(tryStmt.hasFinally());
    }

    @Test
    public void testCatchAndFinallyBlock() {
        ASTTryStatement tryStmt = getTryStmt("function() { try { } catch (error) { } finally { } }");
        Assert.assertNotNull(tryStmt.getCatchClause(0));
        Assert.assertTrue(tryStmt.hasCatch());
        Assert.assertEquals(1, tryStmt.getNumCatchClause());
        Assert.assertNotNull(tryStmt.getFinallyBlock());
        Assert.assertTrue(tryStmt.hasFinally());
    }

    @Test
    public void testMultipleCatchAndFinallyBlock() {
        ASTTryStatement tryStmt = getTryStmt(
                "function() { " + "try { } " + "catch (error if error instanceof BadError) { } "
                        + "catch (error2 if error2 instanceof OtherError) { } " + "finally { } }");
        Assert.assertNotNull(tryStmt.getCatchClause(0));
        Assert.assertNotNull(tryStmt.getCatchClause(1));
        Assert.assertTrue(tryStmt.hasCatch());
        Assert.assertEquals(2, tryStmt.getNumCatchClause());
        Assert.assertNotNull(tryStmt.getFinallyBlock());
        Assert.assertTrue(tryStmt.hasFinally());
    }
}
