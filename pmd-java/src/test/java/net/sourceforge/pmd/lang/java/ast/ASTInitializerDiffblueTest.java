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

class ASTInitializerDiffblueTest {
    /**
     * Method under test: {@link ASTInitializer#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTInitializer astInitializer = new ASTInitializer(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTInitializer>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astInitializer.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTInitializer.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTInitializer#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange
        ASTInitializer astInitializer = new ASTInitializer(1);
        astInitializer.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astInitializer.getBody());
    }

    /**
     * Method under test: {@link ASTInitializer#getBody()}
     */
    @Test
    void testGetBody2() {
        // Arrange
        ASTInitializer astInitializer = new ASTInitializer(1);
        astInitializer.setSymbolTable(mock(JSymbolTable.class));
        astInitializer.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astInitializer.getBody());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTInitializer#setStatic()}
     *   <li>{@link ASTInitializer#isStatic()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTInitializer astInitializer = new ASTInitializer(1);

        // Act
        astInitializer.setStatic();

        // Assert that nothing has changed
        assertTrue(astInitializer.isStatic());
    }

    /**
     * Method under test: {@link ASTInitializer#ASTInitializer(int)}
     */
    @Test
    void testNewASTInitializer() {
        // Arrange and Act
        ASTInitializer actualAstInitializer = new ASTInitializer(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstInitializer.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassBody", actualAstInitializer.getXPathNodeName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Static", nextResult.getName());
        assertNull(actualAstInitializer.getImage());
        assertNull(actualAstInitializer.getFirstChild());
        assertNull(actualAstInitializer.getLastChild());
        assertNull(actualAstInitializer.getNextSibling());
        assertNull(actualAstInitializer.getPreviousSibling());
        assertNull(actualAstInitializer.getFirstToken());
        assertNull(actualAstInitializer.getLastToken());
        assertNull(actualAstInitializer.getEnclosingType());
        assertNull(actualAstInitializer.symbolTable);
        assertEquals(0, actualAstInitializer.getIndexInParent());
        assertEquals(0, actualAstInitializer.getNumChildren());
        assertFalse(actualAstInitializer.isFindBoundary());
        assertFalse(actualAstInitializer.isStatic());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstInitializer.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstInitializer.children().toList().isEmpty());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstInitializer, nextResult.getParent());
        assertSame(ancestorsResult, actualAstInitializer.descendants());
    }
}
