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

class ASTArrayAccessDiffblueTest {
    /**
     * Method under test: {@link ASTArrayAccess#getQualifier()}
     */
    @Test
    void testGetQualifier() {
        // Arrange
        ASTArrayAccess astArrayAccess = new ASTArrayAccess(1);
        astArrayAccess.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayAccess.getQualifier());
    }

    /**
     * Method under test: {@link ASTArrayAccess#getQualifier()}
     */
    @Test
    void testGetQualifier2() {
        // Arrange
        ASTArrayAccess astArrayAccess = new ASTArrayAccess(1);
        astArrayAccess.setSymbolTable(mock(JSymbolTable.class));
        astArrayAccess.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayAccess.getQualifier());
    }

    /**
     * Method under test: {@link ASTArrayAccess#getIndexExpression()}
     */
    @Test
    void testGetIndexExpression() {
        // Arrange
        ASTArrayAccess astArrayAccess = new ASTArrayAccess(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astArrayAccess.addChild(child, 1);

        // Act and Assert
        assertSame(child, astArrayAccess.getIndexExpression());
    }

    /**
     * Method under test: {@link ASTArrayAccess#getIndexExpression()}
     */
    @Test
    void testGetIndexExpression2() {
        // Arrange
        ASTArrayAccess astArrayAccess = new ASTArrayAccess(1);
        astArrayAccess.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astArrayAccess.addChild(child, 1);

        // Act and Assert
        assertSame(child, astArrayAccess.getIndexExpression());
    }

    /**
     * Method under test: {@link ASTArrayAccess#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArrayAccess astArrayAccess = new ASTArrayAccess(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArrayAccess>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArrayAccess.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArrayAccess.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTArrayAccess#ASTArrayAccess(int)}
     */
    @Test
    void testNewASTArrayAccess() {
        // Arrange and Act
        ASTArrayAccess actualAstArrayAccess = new ASTArrayAccess(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstArrayAccess.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("0", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstArrayAccess.getXPathNodeName());
        assertEquals("ParenthesisDepth", nextResult.getName());
        assertNull(actualAstArrayAccess.getImage());
        assertNull(actualAstArrayAccess.getFirstChild());
        assertNull(actualAstArrayAccess.getLastChild());
        assertNull(actualAstArrayAccess.getNextSibling());
        assertNull(actualAstArrayAccess.getPreviousSibling());
        assertNull(actualAstArrayAccess.getFirstToken());
        assertNull(actualAstArrayAccess.getLastToken());
        assertNull(actualAstArrayAccess.getEnclosingType());
        assertNull(actualAstArrayAccess.symbolTable);
        assertNull(actualAstArrayAccess.getTypeMirrorInternal());
        assertEquals(0, actualAstArrayAccess.getIndexInParent());
        assertEquals(0, actualAstArrayAccess.getNumChildren());
        assertEquals(ASTAssignableExpr.AccessType.READ, actualAstArrayAccess.getAccessType());
        assertFalse(actualAstArrayAccess.isFindBoundary());
        assertFalse(actualAstArrayAccess.isCompileTimeConstant());
        assertFalse(actualAstArrayAccess.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstArrayAccess.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstArrayAccess.children().toList().isEmpty());
        assertTrue(actualAstArrayAccess.isExpression());
        assertSame(actualAstArrayAccess, nextResult.getParent());
        assertSame(ancestorsResult, actualAstArrayAccess.descendants());
    }
}
