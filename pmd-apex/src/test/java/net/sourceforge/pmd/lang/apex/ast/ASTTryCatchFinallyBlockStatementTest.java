/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ASTTryCatchFinallyBlockStatementTest extends ApexParserTestBase {

    @Test
    void testTryFinally() {
        ApexNode<?> node = parse("class Foo { void bar() { try { methodCall(); } finally { methodCall(); } } }");
        ASTTryCatchFinallyBlockStatement statement = node.getFirstDescendantOfType(ASTTryCatchFinallyBlockStatement.class);
        assertNotNull(statement.getTryBlock());
        assertEquals(0, statement.getTryBlock().getIndexInParent());
        assertNotNull(statement.getFinallyBlock());
        assertEquals(1, statement.getFinallyBlock().getIndexInParent());
        assertEquals(0, statement.getCatchClauses().size());
    }

    @Test
    void testTryCatch() {
        ApexNode<?> node = parse("class Foo { void bar() { try { methodCall(); } catch (Exception e) { methodCall(); } } }");
        ASTTryCatchFinallyBlockStatement statement = node.getFirstDescendantOfType(ASTTryCatchFinallyBlockStatement.class);
        assertNotNull(statement.getTryBlock());
        assertEquals(0, statement.getTryBlock().getIndexInParent());
        assertNull(statement.getFinallyBlock());
        assertEquals(1, statement.getCatchClauses().size());
        assertNotNull(statement.getCatchClauses().get(0).getBody());
        assertEquals(1, statement.getCatchClauses().get(0).getIndexInParent());
    }

    @Test
    void testTryCatchFinally() {
        ApexNode<?> node = parse("class Foo { void bar() { try { methodCall(); } catch (Exception e) { methodCall(); } finally { } } }");
        ASTTryCatchFinallyBlockStatement statement = node.getFirstDescendantOfType(ASTTryCatchFinallyBlockStatement.class);
        assertNotNull(statement.getTryBlock());
        assertEquals(0, statement.getTryBlock().getIndexInParent());
        assertNotNull(statement.getFinallyBlock());
        assertEquals(2, statement.getFinallyBlock().getIndexInParent());
        assertEquals(1, statement.getCatchClauses().size());
        assertNotNull(statement.getCatchClauses().get(0).getBody());
        assertEquals(1, statement.getCatchClauses().get(0).getIndexInParent());
    }
}
