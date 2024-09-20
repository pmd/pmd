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

class ASTDefaultValueDiffblueTest {
    /**
     * Method under test: {@link ASTDefaultValue#getConstant()}
     */
    @Test
    void testGetConstant() {
        // Arrange
        ASTDefaultValue astDefaultValue = new ASTDefaultValue(1);
        astDefaultValue.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astDefaultValue.getConstant());
    }

    /**
     * Method under test: {@link ASTDefaultValue#getConstant()}
     */
    @Test
    void testGetConstant2() {
        // Arrange
        ASTDefaultValue astDefaultValue = new ASTDefaultValue(1);
        astDefaultValue.setSymbolTable(mock(JSymbolTable.class));
        astDefaultValue.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astDefaultValue.getConstant());
    }

    /**
     * Method under test: {@link ASTDefaultValue#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTDefaultValue astDefaultValue = new ASTDefaultValue(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTDefaultValue>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astDefaultValue.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTDefaultValue.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTDefaultValue#ASTDefaultValue(int)}
     */
    @Test
    void testNewASTDefaultValue() {
        // Arrange and Act
        ASTDefaultValue actualAstDefaultValue = new ASTDefaultValue(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstDefaultValue.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstDefaultValue.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstDefaultValue.getImage());
        assertNull(actualAstDefaultValue.getFirstChild());
        assertNull(actualAstDefaultValue.getLastChild());
        assertNull(actualAstDefaultValue.getNextSibling());
        assertNull(actualAstDefaultValue.getPreviousSibling());
        assertNull(actualAstDefaultValue.getFirstToken());
        assertNull(actualAstDefaultValue.getLastToken());
        assertNull(actualAstDefaultValue.getEnclosingType());
        assertNull(actualAstDefaultValue.symbolTable);
        assertEquals(0, actualAstDefaultValue.getIndexInParent());
        assertEquals(0, actualAstDefaultValue.getNumChildren());
        assertFalse(actualAstDefaultValue.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstDefaultValue.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstDefaultValue.children().toList().isEmpty());
        assertSame(actualAstDefaultValue, nextResult.getParent());
        assertSame(ancestorsResult, actualAstDefaultValue.descendants());
    }
}
