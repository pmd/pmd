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

class ASTArrayAllocationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTArrayAllocation#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArrayAllocation astArrayAllocation = new ASTArrayAllocation(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArrayAllocation>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArrayAllocation.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArrayAllocation.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTArrayAllocation#getTypeNode()}
     */
    @Test
    void testGetTypeNode() {
        // Arrange
        ASTArrayAllocation astArrayAllocation = new ASTArrayAllocation(1);
        astArrayAllocation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayAllocation.getTypeNode());
    }

    /**
     * Method under test: {@link ASTArrayAllocation#getTypeNode()}
     */
    @Test
    void testGetTypeNode2() {
        // Arrange
        ASTArrayAllocation astArrayAllocation = new ASTArrayAllocation(1);
        astArrayAllocation.setSymbolTable(mock(JSymbolTable.class));
        astArrayAllocation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayAllocation.getTypeNode());
    }

    /**
     * Method under test: {@link ASTArrayAllocation#getArrayInitializer()}
     */
    @Test
    void testGetArrayInitializer() {
        // Arrange, Act and Assert
        assertNull((new ASTArrayAllocation(1)).getArrayInitializer());
    }

    /**
     * Method under test: {@link ASTArrayAllocation#getArrayInitializer()}
     */
    @Test
    void testGetArrayInitializer2() {
        // Arrange
        ASTArrayAllocation astArrayAllocation = new ASTArrayAllocation(1);
        astArrayAllocation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayAllocation.getArrayInitializer());
    }

    /**
     * Method under test: {@link ASTArrayAllocation#getArrayInitializer()}
     */
    @Test
    void testGetArrayInitializer3() {
        // Arrange
        ASTArrayAllocation astArrayAllocation = new ASTArrayAllocation(1);
        astArrayAllocation.setSymbolTable(mock(JSymbolTable.class));
        astArrayAllocation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayAllocation.getArrayInitializer());
    }

    /**
     * Method under test: {@link ASTArrayAllocation#getArrayDepth()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetArrayDepth() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTArrayAllocation astArrayAllocation = null;

        // Act
        int actualArrayDepth = astArrayAllocation.getArrayDepth();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTArrayAllocation#ASTArrayAllocation(int)}
     */
    @Test
    void testNewASTArrayAllocation() {
        // Arrange and Act
        ASTArrayAllocation actualAstArrayAllocation = new ASTArrayAllocation(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstArrayAllocation.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ArrayDepth", nextResult.getName());
        assertEquals("ClassBody", actualAstArrayAllocation.getXPathNodeName());
        assertNull(nextResult.getValue());
        assertNull(actualAstArrayAllocation.getImage());
        assertNull(actualAstArrayAllocation.getFirstChild());
        assertNull(actualAstArrayAllocation.getLastChild());
        assertNull(actualAstArrayAllocation.getNextSibling());
        assertNull(actualAstArrayAllocation.getPreviousSibling());
        assertNull(actualAstArrayAllocation.getFirstToken());
        assertNull(actualAstArrayAllocation.getLastToken());
        assertNull(actualAstArrayAllocation.getArrayInitializer());
        assertNull(actualAstArrayAllocation.getEnclosingType());
        assertNull(actualAstArrayAllocation.symbolTable);
        assertNull(actualAstArrayAllocation.getTypeMirrorInternal());
        assertEquals(0, actualAstArrayAllocation.getIndexInParent());
        assertEquals(0, actualAstArrayAllocation.getNumChildren());
        assertFalse(actualAstArrayAllocation.isFindBoundary());
        assertFalse(actualAstArrayAllocation.isCompileTimeConstant());
        assertFalse(actualAstArrayAllocation.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstArrayAllocation.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstArrayAllocation.children().toList().isEmpty());
        assertTrue(actualAstArrayAllocation.isExpression());
        assertSame(actualAstArrayAllocation, nextResult.getParent());
        assertSame(ancestorsResult, actualAstArrayAllocation.descendants());
    }
}
