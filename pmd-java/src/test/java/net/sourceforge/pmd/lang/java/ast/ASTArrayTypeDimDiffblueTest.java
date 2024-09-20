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
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTArrayTypeDimDiffblueTest {
    /**
     * Method under test: {@link ASTArrayTypeDim#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArrayTypeDim astArrayTypeDim = new ASTArrayTypeDim(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArrayTypeDim>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArrayTypeDim.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArrayTypeDim.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTArrayTypeDim#setVarargs()}
     *   <li>{@link ASTArrayTypeDim#isVarargs()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTArrayTypeDim astArrayTypeDim = new ASTArrayTypeDim(1);

        // Act
        astArrayTypeDim.setVarargs();

        // Assert that nothing has changed
        assertTrue(astArrayTypeDim.isVarargs());
    }

    /**
     * Method under test: {@link ASTArrayTypeDim#ASTArrayTypeDim(int)}
     */
    @Test
    void testNewASTArrayTypeDim() {
        // Arrange and Act
        ASTArrayTypeDim actualAstArrayTypeDim = new ASTArrayTypeDim(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstArrayTypeDim.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassBody", actualAstArrayTypeDim.getXPathNodeName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Varargs", nextResult.getName());
        assertNull(actualAstArrayTypeDim.getImage());
        assertNull(actualAstArrayTypeDim.getFirstChild());
        assertNull(actualAstArrayTypeDim.getLastChild());
        assertNull(actualAstArrayTypeDim.getNextSibling());
        assertNull(actualAstArrayTypeDim.getPreviousSibling());
        assertNull(actualAstArrayTypeDim.getFirstToken());
        assertNull(actualAstArrayTypeDim.getLastToken());
        assertNull(actualAstArrayTypeDim.getEnclosingType());
        assertNull(actualAstArrayTypeDim.symbolTable);
        assertEquals(0, actualAstArrayTypeDim.getIndexInParent());
        assertEquals(0, actualAstArrayTypeDim.getNumChildren());
        assertFalse(actualAstArrayTypeDim.isFindBoundary());
        assertFalse(actualAstArrayTypeDim.isVarargs());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstArrayTypeDim.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstArrayTypeDim.children().toList().isEmpty());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstArrayTypeDim, nextResult.getParent());
        assertSame(ancestorsResult, actualAstArrayTypeDim.descendants());
        assertSame(ancestorsResult, actualAstArrayTypeDim.getDeclaredAnnotations());
    }
}
