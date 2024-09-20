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

class ASTForeachStatementDiffblueTest {
    /**
     * Method under test:
     * {@link ASTForeachStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTForeachStatement astForeachStatement = new ASTForeachStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTForeachStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astForeachStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTForeachStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTForeachStatement#getVarId()}
     */
    @Test
    void testGetVarId() {
        // Arrange
        ASTForeachStatement astForeachStatement = new ASTForeachStatement(1);
        astForeachStatement.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertNull(astForeachStatement.getVarId());
    }

    /**
     * Method under test: {@link ASTForeachStatement#getIterableExpr()}
     */
    @Test
    void testGetIterableExpr() {
        // Arrange, Act and Assert
        assertNull((new ASTForeachStatement(1)).getIterableExpr());
    }

    /**
     * Method under test: {@link ASTForeachStatement#getIterableExpr()}
     */
    @Test
    void testGetIterableExpr2() {
        // Arrange
        ASTForeachStatement astForeachStatement = new ASTForeachStatement(1);
        astForeachStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astForeachStatement.getIterableExpr());
    }

    /**
     * Method under test: {@link ASTForeachStatement#ASTForeachStatement(int)}
     */
    @Test
    void testNewASTForeachStatement() {
        // Arrange and Act
        ASTForeachStatement actualAstForeachStatement = new ASTForeachStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstForeachStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstForeachStatement.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstForeachStatement.getImage());
        assertNull(actualAstForeachStatement.getFirstChild());
        assertNull(actualAstForeachStatement.getLastChild());
        assertNull(actualAstForeachStatement.getNextSibling());
        assertNull(actualAstForeachStatement.getPreviousSibling());
        assertNull(actualAstForeachStatement.getFirstToken());
        assertNull(actualAstForeachStatement.getLastToken());
        assertNull(actualAstForeachStatement.getIterableExpr());
        assertNull(actualAstForeachStatement.getCondition());
        assertNull(actualAstForeachStatement.getBody());
        assertNull(actualAstForeachStatement.getEnclosingType());
        assertNull(actualAstForeachStatement.symbolTable);
        assertEquals(0, actualAstForeachStatement.getIndexInParent());
        assertEquals(0, actualAstForeachStatement.getNumChildren());
        assertFalse(actualAstForeachStatement.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstForeachStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstForeachStatement.children().toList().isEmpty());
        assertSame(actualAstForeachStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstForeachStatement.descendants());
    }
}
