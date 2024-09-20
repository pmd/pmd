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

class ASTEmptyStatementDiffblueTest {
    /**
     * Method under test:
     * {@link ASTEmptyStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTEmptyStatement astEmptyStatement = new ASTEmptyStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTEmptyStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astEmptyStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTEmptyStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTEmptyStatement#ASTEmptyStatement(int)}
     */
    @Test
    void testNewASTEmptyStatement() {
        // Arrange and Act
        ASTEmptyStatement actualAstEmptyStatement = new ASTEmptyStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstEmptyStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstEmptyStatement.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstEmptyStatement.getImage());
        assertNull(actualAstEmptyStatement.getFirstChild());
        assertNull(actualAstEmptyStatement.getLastChild());
        assertNull(actualAstEmptyStatement.getNextSibling());
        assertNull(actualAstEmptyStatement.getPreviousSibling());
        assertNull(actualAstEmptyStatement.getFirstToken());
        assertNull(actualAstEmptyStatement.getLastToken());
        assertNull(actualAstEmptyStatement.getEnclosingType());
        assertNull(actualAstEmptyStatement.symbolTable);
        assertEquals(0, actualAstEmptyStatement.getIndexInParent());
        assertEquals(0, actualAstEmptyStatement.getNumChildren());
        assertFalse(actualAstEmptyStatement.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstEmptyStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstEmptyStatement.children().toList().isEmpty());
        assertSame(actualAstEmptyStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstEmptyStatement.descendants());
    }
}
