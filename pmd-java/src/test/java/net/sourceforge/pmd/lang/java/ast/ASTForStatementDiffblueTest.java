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

class ASTForStatementDiffblueTest {
    /**
     * Method under test: {@link ASTForStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTForStatement astForStatement = new ASTForStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTForStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astForStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTForStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTForStatement#getCondition()}
     */
    @Test
    void testGetCondition() {
        // Arrange, Act and Assert
        assertNull((new ASTForStatement(1)).getCondition());
    }

    /**
     * Method under test: {@link ASTForStatement#getCondition()}
     */
    @Test
    void testGetCondition2() {
        // Arrange
        ASTForStatement astForStatement = new ASTForStatement(1);
        astForStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astForStatement.getCondition());
    }

    /**
     * Method under test: {@link ASTForStatement#getInit()}
     */
    @Test
    void testGetInit() {
        // Arrange, Act and Assert
        assertNull((new ASTForStatement(1)).getInit());
    }

    /**
     * Method under test: {@link ASTForStatement#getInit()}
     */
    @Test
    void testGetInit2() {
        // Arrange
        ASTForStatement astForStatement = new ASTForStatement(1);
        astForStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astForStatement.getInit());
    }

    /**
     * Method under test: {@link ASTForStatement#getInit()}
     */
    @Test
    void testGetInit3() {
        // Arrange
        ASTForStatement astForStatement = new ASTForStatement(1);
        astForStatement.setSymbolTable(mock(JSymbolTable.class));
        astForStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astForStatement.getInit());
    }

    /**
     * Method under test: {@link ASTForStatement#getUpdate()}
     */
    @Test
    void testGetUpdate() {
        // Arrange, Act and Assert
        assertNull((new ASTForStatement(1)).getUpdate());
    }

    /**
     * Method under test: {@link ASTForStatement#getUpdate()}
     */
    @Test
    void testGetUpdate2() {
        // Arrange
        ASTForStatement astForStatement = new ASTForStatement(1);
        astForStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astForStatement.getUpdate());
    }

    /**
     * Method under test: {@link ASTForStatement#ASTForStatement(int)}
     */
    @Test
    void testNewASTForStatement() {
        // Arrange and Act
        ASTForStatement actualAstForStatement = new ASTForStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstForStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstForStatement.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstForStatement.getImage());
        assertNull(actualAstForStatement.getFirstChild());
        assertNull(actualAstForStatement.getLastChild());
        assertNull(actualAstForStatement.getNextSibling());
        assertNull(actualAstForStatement.getPreviousSibling());
        assertNull(actualAstForStatement.getFirstToken());
        assertNull(actualAstForStatement.getLastToken());
        assertNull(actualAstForStatement.getCondition());
        assertNull(actualAstForStatement.getInit());
        assertNull(actualAstForStatement.getBody());
        assertNull(actualAstForStatement.getUpdate());
        assertNull(actualAstForStatement.getEnclosingType());
        assertNull(actualAstForStatement.symbolTable);
        assertEquals(0, actualAstForStatement.getIndexInParent());
        assertEquals(0, actualAstForStatement.getNumChildren());
        assertFalse(actualAstForStatement.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstForStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstForStatement.children().toList().isEmpty());
        assertSame(actualAstForStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstForStatement.descendants());
    }
}
