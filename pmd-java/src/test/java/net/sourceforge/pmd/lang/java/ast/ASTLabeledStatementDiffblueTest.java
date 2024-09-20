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

class ASTLabeledStatementDiffblueTest {
    /**
     * Method under test:
     * {@link ASTLabeledStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTLabeledStatement astLabeledStatement = new ASTLabeledStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTLabeledStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astLabeledStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTLabeledStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTLabeledStatement#getLabel()}
     */
    @Test
    void testGetLabel() {
        // Arrange, Act and Assert
        assertNull((new ASTLabeledStatement(1)).getLabel());
    }

    /**
     * Method under test: {@link ASTLabeledStatement#getLabel()}
     */
    @Test
    void testGetLabel2() {
        // Arrange
        ASTLabeledStatement astLabeledStatement = new ASTLabeledStatement(1);
        astLabeledStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astLabeledStatement.getLabel());
    }

    /**
     * Method under test: {@link ASTLabeledStatement#getStatement()}
     */
    @Test
    void testGetStatement() {
        // Arrange
        ASTLabeledStatement astLabeledStatement = new ASTLabeledStatement(1);
        astLabeledStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLabeledStatement.getStatement());
    }

    /**
     * Method under test: {@link ASTLabeledStatement#getStatement()}
     */
    @Test
    void testGetStatement2() {
        // Arrange
        ASTLabeledStatement astLabeledStatement = new ASTLabeledStatement(1);
        astLabeledStatement.setSymbolTable(mock(JSymbolTable.class));
        astLabeledStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLabeledStatement.getStatement());
    }

    /**
     * Method under test: {@link ASTLabeledStatement#ASTLabeledStatement(int)}
     */
    @Test
    void testNewASTLabeledStatement() {
        // Arrange and Act
        ASTLabeledStatement actualAstLabeledStatement = new ASTLabeledStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstLabeledStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstLabeledStatement.getXPathNodeName());
        assertEquals("Label", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstLabeledStatement.getImage());
        assertNull(actualAstLabeledStatement.getLabel());
        assertNull(actualAstLabeledStatement.getFirstChild());
        assertNull(actualAstLabeledStatement.getLastChild());
        assertNull(actualAstLabeledStatement.getNextSibling());
        assertNull(actualAstLabeledStatement.getPreviousSibling());
        assertNull(actualAstLabeledStatement.getFirstToken());
        assertNull(actualAstLabeledStatement.getLastToken());
        assertNull(actualAstLabeledStatement.getEnclosingType());
        assertNull(actualAstLabeledStatement.symbolTable);
        assertEquals(0, actualAstLabeledStatement.getIndexInParent());
        assertEquals(0, actualAstLabeledStatement.getNumChildren());
        assertFalse(actualAstLabeledStatement.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstLabeledStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstLabeledStatement.children().toList().isEmpty());
        assertSame(actualAstLabeledStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstLabeledStatement.descendants());
    }
}
