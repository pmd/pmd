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

class ASTGuardDiffblueTest {
    /**
     * Method under test: {@link ASTGuard#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTGuard astGuard = new ASTGuard(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTGuard>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astGuard.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTGuard.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTGuard#getGuard()}
     */
    @Test
    void testGetGuard() {
        // Arrange
        ASTGuard astGuard = new ASTGuard(1);
        astGuard.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astGuard.getGuard());
    }

    /**
     * Method under test: {@link ASTGuard#getGuard()}
     */
    @Test
    void testGetGuard2() {
        // Arrange
        ASTGuard astGuard = new ASTGuard(1);
        astGuard.setSymbolTable(mock(JSymbolTable.class));
        astGuard.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astGuard.getGuard());
    }

    /**
     * Method under test: {@link ASTGuard#ASTGuard(int)}
     */
    @Test
    void testNewASTGuard() {
        // Arrange and Act
        ASTGuard actualAstGuard = new ASTGuard(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstGuard.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstGuard.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstGuard.getImage());
        assertNull(actualAstGuard.getFirstChild());
        assertNull(actualAstGuard.getLastChild());
        assertNull(actualAstGuard.getNextSibling());
        assertNull(actualAstGuard.getPreviousSibling());
        assertNull(actualAstGuard.getFirstToken());
        assertNull(actualAstGuard.getLastToken());
        assertNull(actualAstGuard.getEnclosingType());
        assertNull(actualAstGuard.symbolTable);
        assertEquals(0, actualAstGuard.getIndexInParent());
        assertEquals(0, actualAstGuard.getNumChildren());
        assertFalse(actualAstGuard.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstGuard.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstGuard.children().toList().isEmpty());
        assertSame(actualAstGuard, nextResult.getParent());
        assertSame(ancestorsResult, actualAstGuard.descendants());
    }
}
