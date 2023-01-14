/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTTryCatchFinallyBlockStatementTest extends ApexParserTestBase {

    @Test
    public void testTryFinally() {
        ApexNode<?> node = parse("class Foo { void bar() { try { methodCall(); } finally { methodCall(); } } }");
        ASTTryCatchFinallyBlockStatement statement = node.getFirstDescendantOfType(ASTTryCatchFinallyBlockStatement.class);
        Assert.assertNotNull(statement.getTryBlock());
        Assert.assertEquals(0, statement.getTryBlock().getIndexInParent());
        Assert.assertNotNull(statement.getFinallyBlock());
        Assert.assertEquals(1, statement.getFinallyBlock().getIndexInParent());
        Assert.assertEquals(0, statement.getCatchClauses().size());
    }

    @Test
    public void testTryCatch() {
        ApexNode<?> node = parse("class Foo { void bar() { try { methodCall(); } catch (Exception e) { methodCall(); } } }");
        ASTTryCatchFinallyBlockStatement statement = node.getFirstDescendantOfType(ASTTryCatchFinallyBlockStatement.class);
        Assert.assertNotNull(statement.getTryBlock());
        Assert.assertEquals(0, statement.getTryBlock().getIndexInParent());
        Assert.assertNull(statement.getFinallyBlock());
        Assert.assertEquals(1, statement.getCatchClauses().size());
        Assert.assertNotNull(statement.getCatchClauses().get(0).getBody());
        Assert.assertEquals(1, statement.getCatchClauses().get(0).getIndexInParent());
    }

    @Test
    public void testTryCatchFinally() {
        ApexNode<?> node = parse("class Foo { void bar() { try { methodCall(); } catch (Exception e) { methodCall(); } finally { } } }");
        ASTTryCatchFinallyBlockStatement statement = node.getFirstDescendantOfType(ASTTryCatchFinallyBlockStatement.class);
        Assert.assertNotNull(statement.getTryBlock());
        Assert.assertEquals(0, statement.getTryBlock().getIndexInParent());
        Assert.assertNotNull(statement.getFinallyBlock());
        Assert.assertEquals(2, statement.getFinallyBlock().getIndexInParent());
        Assert.assertEquals(1, statement.getCatchClauses().size());
        Assert.assertNotNull(statement.getCatchClauses().get(0).getBody());
        Assert.assertEquals(1, statement.getCatchClauses().get(0).getIndexInParent());
    }
}
