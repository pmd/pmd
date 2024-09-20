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

class ASTDoStatementDiffblueTest {
    /**
     * Method under test: {@link ASTDoStatement#getCondition()}
     */
    @Test
    void testGetCondition() {
        // Arrange
        ASTDoStatement astDoStatement = new ASTDoStatement(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astDoStatement.addChild(child, 1);

        // Act and Assert
        assertSame(child, astDoStatement.getCondition());
    }

    /**
     * Method under test: {@link ASTDoStatement#getCondition()}
     */
    @Test
    void testGetCondition2() {
        // Arrange
        ASTDoStatement astDoStatement = new ASTDoStatement(1);
        astDoStatement.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astDoStatement.addChild(child, 1);

        // Act and Assert
        assertSame(child, astDoStatement.getCondition());
    }

    /**
     * Method under test: {@link ASTDoStatement#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange
        ASTDoStatement astDoStatement = new ASTDoStatement(1);
        astDoStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astDoStatement.getBody());
    }

    /**
     * Method under test: {@link ASTDoStatement#getBody()}
     */
    @Test
    void testGetBody2() {
        // Arrange
        ASTDoStatement astDoStatement = new ASTDoStatement(1);
        astDoStatement.setSymbolTable(mock(JSymbolTable.class));
        astDoStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astDoStatement.getBody());
    }

    /**
     * Method under test: {@link ASTDoStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTDoStatement astDoStatement = new ASTDoStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTDoStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astDoStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTDoStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTDoStatement#ASTDoStatement(int)}
     */
    @Test
    void testNewASTDoStatement() {
        // Arrange and Act
        ASTDoStatement actualAstDoStatement = new ASTDoStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstDoStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstDoStatement.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstDoStatement.getImage());
        assertNull(actualAstDoStatement.getFirstChild());
        assertNull(actualAstDoStatement.getLastChild());
        assertNull(actualAstDoStatement.getNextSibling());
        assertNull(actualAstDoStatement.getPreviousSibling());
        assertNull(actualAstDoStatement.getFirstToken());
        assertNull(actualAstDoStatement.getLastToken());
        assertNull(actualAstDoStatement.getEnclosingType());
        assertNull(actualAstDoStatement.symbolTable);
        assertEquals(0, actualAstDoStatement.getIndexInParent());
        assertEquals(0, actualAstDoStatement.getNumChildren());
        assertFalse(actualAstDoStatement.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstDoStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstDoStatement.children().toList().isEmpty());
        assertSame(actualAstDoStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstDoStatement.descendants());
    }
}
