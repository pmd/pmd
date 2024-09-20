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

class ASTArrayInitializerDiffblueTest {
    /**
     * Method under test:
     * {@link ASTArrayInitializer#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArrayInitializer astArrayInitializer = new ASTArrayInitializer(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArrayInitializer>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArrayInitializer.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArrayInitializer.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTArrayInitializer#length()}
     */
    @Test
    void testLength() {
        // Arrange, Act and Assert
        assertEquals(0, (new ASTArrayInitializer(1)).length());
    }

    /**
     * Method under test: {@link ASTArrayInitializer#length()}
     */
    @Test
    void testLength2() {
        // Arrange
        ASTArrayInitializer astArrayInitializer = new ASTArrayInitializer(1);
        astArrayInitializer.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertEquals(0, astArrayInitializer.length());
    }

    /**
     * Method under test: {@link ASTArrayInitializer#iterator()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIterator() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        ASTArrayInitializer astArrayInitializer = null;

        // Act
        Iterator<ASTExpression> actualIteratorResult = astArrayInitializer.iterator();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTArrayInitializer#ASTArrayInitializer(int)}
     */
    @Test
    void testNewASTArrayInitializer() {
        // Arrange and Act
        ASTArrayInitializer actualAstArrayInitializer = new ASTArrayInitializer(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstArrayInitializer.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("0", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstArrayInitializer.getXPathNodeName());
        assertEquals("Length", nextResult.getName());
        assertNull(actualAstArrayInitializer.getImage());
        assertNull(actualAstArrayInitializer.getFirstChild());
        assertNull(actualAstArrayInitializer.getLastChild());
        assertNull(actualAstArrayInitializer.getNextSibling());
        assertNull(actualAstArrayInitializer.getPreviousSibling());
        assertNull(actualAstArrayInitializer.getFirstToken());
        assertNull(actualAstArrayInitializer.getLastToken());
        assertNull(actualAstArrayInitializer.getEnclosingType());
        assertNull(actualAstArrayInitializer.symbolTable);
        assertNull(actualAstArrayInitializer.getTypeMirrorInternal());
        assertEquals(0, actualAstArrayInitializer.getIndexInParent());
        assertEquals(0, actualAstArrayInitializer.getNumChildren());
        assertEquals(0, actualAstArrayInitializer.length());
        assertFalse(actualAstArrayInitializer.isFindBoundary());
        assertFalse(actualAstArrayInitializer.isCompileTimeConstant());
        assertFalse(actualAstArrayInitializer.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstArrayInitializer.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstArrayInitializer.children().toList().isEmpty());
        assertTrue(actualAstArrayInitializer.isExpression());
        assertSame(actualAstArrayInitializer, nextResult.getParent());
        assertSame(ancestorsResult, actualAstArrayInitializer.descendants());
    }
}
