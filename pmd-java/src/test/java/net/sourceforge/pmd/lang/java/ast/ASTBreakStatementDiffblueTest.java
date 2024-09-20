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

class ASTBreakStatementDiffblueTest {
    /**
     * Method under test:
     * {@link ASTBreakStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTBreakStatement astBreakStatement = new ASTBreakStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTBreakStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astBreakStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTBreakStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTBreakStatement#getLabel()}
     */
    @Test
    void testGetLabel() {
        // Arrange, Act and Assert
        assertNull((new ASTBreakStatement(1)).getLabel());
    }

    /**
     * Method under test: {@link ASTBreakStatement#getLabel()}
     */
    @Test
    void testGetLabel2() {
        // Arrange
        ASTBreakStatement astBreakStatement = new ASTBreakStatement(1);
        astBreakStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astBreakStatement.getLabel());
    }

    /**
     * Method under test: {@link ASTBreakStatement#getTarget()}
     */
    @Test
    void testGetTarget() {
        // Arrange, Act and Assert
        assertNull((new ASTBreakStatement(1)).getTarget());
    }

    /**
     * Method under test: {@link ASTBreakStatement#getTarget()}
     */
    @Test
    void testGetTarget2() {
        // Arrange
        ASTBreakStatement astBreakStatement = new ASTBreakStatement(1);
        astBreakStatement.setImage("foo");

        // Act and Assert
        assertNull(astBreakStatement.getTarget());
    }

    /**
     * Method under test: {@link ASTBreakStatement#getTarget()}
     */
    @Test
    void testGetTarget3() {
        // Arrange
        ASTBreakStatement astBreakStatement = new ASTBreakStatement(1);
        astBreakStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astBreakStatement.getTarget());
    }

    /**
     * Method under test: {@link ASTBreakStatement#ASTBreakStatement(int)}
     */
    @Test
    void testNewASTBreakStatement() {
        // Arrange and Act
        ASTBreakStatement actualAstBreakStatement = new ASTBreakStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstBreakStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstBreakStatement.getXPathNodeName());
        assertEquals("Label", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstBreakStatement.getImage());
        assertNull(actualAstBreakStatement.getLabel());
        assertNull(actualAstBreakStatement.getFirstChild());
        assertNull(actualAstBreakStatement.getLastChild());
        assertNull(actualAstBreakStatement.getNextSibling());
        assertNull(actualAstBreakStatement.getPreviousSibling());
        assertNull(actualAstBreakStatement.getFirstToken());
        assertNull(actualAstBreakStatement.getLastToken());
        assertNull(actualAstBreakStatement.getTarget());
        assertNull(actualAstBreakStatement.getEnclosingType());
        assertNull(actualAstBreakStatement.symbolTable);
        assertEquals(0, actualAstBreakStatement.getIndexInParent());
        assertEquals(0, actualAstBreakStatement.getNumChildren());
        assertFalse(actualAstBreakStatement.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstBreakStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstBreakStatement.children().toList().isEmpty());
        assertSame(actualAstBreakStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstBreakStatement.descendants());
    }
}
