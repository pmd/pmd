package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTArrayDimExprDiffblueTest {
    /**
     * Method under test: {@link ASTArrayDimExpr#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArrayDimExpr astArrayDimExpr = new ASTArrayDimExpr(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArrayDimExpr>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArrayDimExpr.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArrayDimExpr.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTArrayDimExpr#getLengthExpression()}
     */
    @Test
    void testGetLengthExpression() {
        // Arrange
        ASTArrayDimExpr astArrayDimExpr = new ASTArrayDimExpr(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astArrayDimExpr.addChild(child, 1);

        // Act and Assert
        assertSame(child, astArrayDimExpr.getLengthExpression());
    }

    /**
     * Method under test: {@link ASTArrayDimExpr#getLengthExpression()}
     */
    @Test
    void testGetLengthExpression2() {
        // Arrange
        ASTArrayDimExpr astArrayDimExpr = new ASTArrayDimExpr(1);
        astArrayDimExpr.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astArrayDimExpr.addChild(child, 1);

        // Act and Assert
        assertSame(child, astArrayDimExpr.getLengthExpression());
    }

    /**
     * Method under test: {@link ASTArrayDimExpr#ASTArrayDimExpr(int)}
     */
    @Test
    void testNewASTArrayDimExpr() {
        // Arrange and Act
        ASTArrayDimExpr actualAstArrayDimExpr = new ASTArrayDimExpr(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstArrayDimExpr.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassBody", actualAstArrayDimExpr.getXPathNodeName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Varargs", nextResult.getName());
        assertNull(actualAstArrayDimExpr.getImage());
        assertNull(actualAstArrayDimExpr.getFirstChild());
        assertNull(actualAstArrayDimExpr.getLastChild());
        assertNull(actualAstArrayDimExpr.getNextSibling());
        assertNull(actualAstArrayDimExpr.getPreviousSibling());
        assertNull(actualAstArrayDimExpr.getFirstToken());
        assertNull(actualAstArrayDimExpr.getLastToken());
        assertNull(actualAstArrayDimExpr.getEnclosingType());
        assertNull(actualAstArrayDimExpr.symbolTable);
        assertEquals(0, actualAstArrayDimExpr.getIndexInParent());
        assertEquals(0, actualAstArrayDimExpr.getNumChildren());
        assertFalse(actualAstArrayDimExpr.isFindBoundary());
        assertFalse(actualAstArrayDimExpr.isVarargs());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstArrayDimExpr.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstArrayDimExpr.children().toList().isEmpty());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstArrayDimExpr, nextResult.getParent());
        assertSame(ancestorsResult, actualAstArrayDimExpr.descendants());
        assertSame(ancestorsResult, actualAstArrayDimExpr.getDeclaredAnnotations());
    }
}
