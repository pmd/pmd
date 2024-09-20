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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTIntersectionTypeDiffblueTest {
    /**
     * Method under test: {@link ASTIntersectionType#getComponents()}
     */
    @Test
    void testGetComponents() {
        // Arrange, Act and Assert
        assertTrue((new ASTIntersectionType(1)).getComponents().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTIntersectionType#getComponents()}
     */
    @Test
    void testGetComponents2() {
        // Arrange
        ASTIntersectionType astIntersectionType = new ASTIntersectionType(1);
        astIntersectionType.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertTrue(astIntersectionType.getComponents().toList().isEmpty());
    }

    /**
     * Method under test:
     * {@link ASTIntersectionType#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTIntersectionType astIntersectionType = new ASTIntersectionType(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTIntersectionType>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astIntersectionType.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTIntersectionType.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTIntersectionType#iterator()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIterator() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        ASTIntersectionType astIntersectionType = null;

        // Act
        Iterator<ASTType> actualIteratorResult = astIntersectionType.iterator();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTIntersectionType#ASTIntersectionType(int)}
     */
    @Test
    void testNewASTIntersectionType() {
        // Arrange and Act
        ASTIntersectionType actualAstIntersectionType = new ASTIntersectionType(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstIntersectionType.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstIntersectionType.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstIntersectionType.getImage());
        assertNull(actualAstIntersectionType.getNextSibling());
        assertNull(actualAstIntersectionType.getPreviousSibling());
        assertNull(actualAstIntersectionType.getFirstToken());
        assertNull(actualAstIntersectionType.getLastToken());
        assertNull(actualAstIntersectionType.getEnclosingType());
        assertNull(actualAstIntersectionType.symbolTable);
        assertNull(actualAstIntersectionType.getTypeMirrorInternal());
        assertEquals(0, actualAstIntersectionType.getIndexInParent());
        assertEquals(0, actualAstIntersectionType.getNumChildren());
        assertFalse(actualAstIntersectionType.isFindBoundary());
        assertFalse(actualAstIntersectionType.isVoid());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstIntersectionType.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstIntersectionType.children().toList().isEmpty());
        assertSame(actualAstIntersectionType, nextResult.getParent());
        assertSame(ancestorsResult, actualAstIntersectionType.descendants());
        assertSame(ancestorsResult, actualAstIntersectionType.getComponents());
        assertSame(ancestorsResult, actualAstIntersectionType.getDeclaredAnnotations());
    }
}
