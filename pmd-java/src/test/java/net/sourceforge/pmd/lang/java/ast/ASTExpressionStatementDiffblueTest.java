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

class ASTExpressionStatementDiffblueTest {
    /**
     * Method under test:
     * {@link ASTExpressionStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTExpressionStatement astExpressionStatement = new ASTExpressionStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTExpressionStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astExpressionStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTExpressionStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTExpressionStatement#getExpr()}
     */
    @Test
    void testGetExpr() {
        // Arrange
        ASTExpressionStatement astExpressionStatement = new ASTExpressionStatement(1);
        astExpressionStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astExpressionStatement.getExpr());
    }

    /**
     * Method under test: {@link ASTExpressionStatement#getExpr()}
     */
    @Test
    void testGetExpr2() {
        // Arrange
        ASTExpressionStatement astExpressionStatement = new ASTExpressionStatement(1);
        astExpressionStatement.setSymbolTable(mock(JSymbolTable.class));
        astExpressionStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astExpressionStatement.getExpr());
    }

    /**
     * Method under test: {@link ASTExpressionStatement#ASTExpressionStatement(int)}
     */
    @Test
    void testNewASTExpressionStatement() {
        // Arrange and Act
        ASTExpressionStatement actualAstExpressionStatement = new ASTExpressionStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstExpressionStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstExpressionStatement.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstExpressionStatement.getImage());
        assertNull(actualAstExpressionStatement.getFirstChild());
        assertNull(actualAstExpressionStatement.getLastChild());
        assertNull(actualAstExpressionStatement.getNextSibling());
        assertNull(actualAstExpressionStatement.getPreviousSibling());
        assertNull(actualAstExpressionStatement.getFirstToken());
        assertNull(actualAstExpressionStatement.getLastToken());
        assertNull(actualAstExpressionStatement.getEnclosingType());
        assertNull(actualAstExpressionStatement.symbolTable);
        assertEquals(0, actualAstExpressionStatement.getIndexInParent());
        assertEquals(0, actualAstExpressionStatement.getNumChildren());
        assertFalse(actualAstExpressionStatement.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstExpressionStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstExpressionStatement.children().toList().isEmpty());
        assertSame(actualAstExpressionStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstExpressionStatement.descendants());
    }
}
