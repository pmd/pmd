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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTMemberValueArrayInitializerDiffblueTest {
    /**
     * Method under test:
     * {@link ASTMemberValueArrayInitializer#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTMemberValueArrayInitializer astMemberValueArrayInitializer = new ASTMemberValueArrayInitializer(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTMemberValueArrayInitializer>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astMemberValueArrayInitializer.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTMemberValueArrayInitializer.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTMemberValueArrayInitializer#iterator()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIterator() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        ASTMemberValueArrayInitializer astMemberValueArrayInitializer = null;

        // Act
        Iterator<ASTMemberValue> actualIteratorResult = astMemberValueArrayInitializer.iterator();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTMemberValueArrayInitializer#ASTMemberValueArrayInitializer(int)}
     */
    @Test
    void testNewASTMemberValueArrayInitializer() {
        // Arrange and Act
        ASTMemberValueArrayInitializer actualAstMemberValueArrayInitializer = new ASTMemberValueArrayInitializer(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstMemberValueArrayInitializer.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstMemberValueArrayInitializer.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(actualAstMemberValueArrayInitializer.getConstValue());
        assertNull(nextResult.getValue());
        assertNull(actualAstMemberValueArrayInitializer.getImage());
        assertNull(actualAstMemberValueArrayInitializer.getFirstChild());
        assertNull(actualAstMemberValueArrayInitializer.getLastChild());
        assertNull(actualAstMemberValueArrayInitializer.getNextSibling());
        assertNull(actualAstMemberValueArrayInitializer.getPreviousSibling());
        assertNull(actualAstMemberValueArrayInitializer.getFirstToken());
        assertNull(actualAstMemberValueArrayInitializer.getLastToken());
        assertNull(actualAstMemberValueArrayInitializer.getEnclosingType());
        assertNull(actualAstMemberValueArrayInitializer.symbolTable);
        assertEquals(0, actualAstMemberValueArrayInitializer.getIndexInParent());
        assertEquals(0, actualAstMemberValueArrayInitializer.getNumChildren());
        assertFalse(actualAstMemberValueArrayInitializer.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstMemberValueArrayInitializer.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstMemberValueArrayInitializer.children().toList().isEmpty());
        assertSame(actualAstMemberValueArrayInitializer, nextResult.getParent());
        assertSame(ancestorsResult, actualAstMemberValueArrayInitializer.descendants());
    }
}
