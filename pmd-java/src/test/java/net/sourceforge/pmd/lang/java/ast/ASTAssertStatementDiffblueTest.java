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

class ASTAssertStatementDiffblueTest {
    /**
     * Method under test:
     * {@link ASTAssertStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTAssertStatement astAssertStatement = new ASTAssertStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTAssertStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astAssertStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTAssertStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTAssertStatement#getCondition()}
     */
    @Test
    void testGetCondition() {
        // Arrange
        ASTAssertStatement astAssertStatement = new ASTAssertStatement(1);
        astAssertStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAssertStatement.getCondition());
    }

    /**
     * Method under test: {@link ASTAssertStatement#getCondition()}
     */
    @Test
    void testGetCondition2() {
        // Arrange
        ASTAssertStatement astAssertStatement = new ASTAssertStatement(1);
        astAssertStatement.setSymbolTable(mock(JSymbolTable.class));
        astAssertStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAssertStatement.getCondition());
    }

    /**
     * Method under test: {@link ASTAssertStatement#hasDetailMessage()}
     */
    @Test
    void testHasDetailMessage() {
        // Arrange, Act and Assert
        assertFalse((new ASTAssertStatement(1)).hasDetailMessage());
    }

    /**
     * Method under test: {@link ASTAssertStatement#hasDetailMessage()}
     */
    @Test
    void testHasDetailMessage2() {
        // Arrange
        ASTAssertStatement astAssertStatement = new ASTAssertStatement(1);
        astAssertStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAssertStatement.hasDetailMessage());
    }

    /**
     * Method under test: {@link ASTAssertStatement#hasDetailMessage()}
     */
    @Test
    void testHasDetailMessage3() {
        // Arrange
        ASTAssertStatement astAssertStatement = new ASTAssertStatement(1);
        astAssertStatement.setSymbolTable(mock(JSymbolTable.class));
        astAssertStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAssertStatement.hasDetailMessage());
    }

    /**
     * Method under test: {@link ASTAssertStatement#getDetailMessageNode()}
     */
    @Test
    void testGetDetailMessageNode() {
        // Arrange, Act and Assert
        assertNull((new ASTAssertStatement(1)).getDetailMessageNode());
    }

    /**
     * Method under test: {@link ASTAssertStatement#getDetailMessageNode()}
     */
    @Test
    void testGetDetailMessageNode2() {
        // Arrange
        ASTAssertStatement astAssertStatement = new ASTAssertStatement(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astAssertStatement.addChild(child, 1);

        // Act and Assert
        assertSame(child, astAssertStatement.getDetailMessageNode());
    }

    /**
     * Method under test: {@link ASTAssertStatement#getDetailMessageNode()}
     */
    @Test
    void testGetDetailMessageNode3() {
        // Arrange
        ASTAssertStatement astAssertStatement = new ASTAssertStatement(1);
        astAssertStatement.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astAssertStatement.addChild(child, 1);

        // Act and Assert
        assertSame(child, astAssertStatement.getDetailMessageNode());
    }

    /**
     * Method under test: {@link ASTAssertStatement#ASTAssertStatement(int)}
     */
    @Test
    void testNewASTAssertStatement() {
        // Arrange and Act
        ASTAssertStatement actualAstAssertStatement = new ASTAssertStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstAssertStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassBody", actualAstAssertStatement.getXPathNodeName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("DetailMessage", nextResult.getName());
        assertNull(actualAstAssertStatement.getImage());
        assertNull(actualAstAssertStatement.getFirstChild());
        assertNull(actualAstAssertStatement.getLastChild());
        assertNull(actualAstAssertStatement.getNextSibling());
        assertNull(actualAstAssertStatement.getPreviousSibling());
        assertNull(actualAstAssertStatement.getFirstToken());
        assertNull(actualAstAssertStatement.getLastToken());
        assertNull(actualAstAssertStatement.getDetailMessageNode());
        assertNull(actualAstAssertStatement.getEnclosingType());
        assertNull(actualAstAssertStatement.symbolTable);
        assertEquals(0, actualAstAssertStatement.getIndexInParent());
        assertEquals(0, actualAstAssertStatement.getNumChildren());
        assertFalse(actualAstAssertStatement.isFindBoundary());
        assertFalse(actualAstAssertStatement.hasDetailMessage());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstAssertStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstAssertStatement.children().toList().isEmpty());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstAssertStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstAssertStatement.descendants());
    }
}
