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

class ASTCatchClauseDiffblueTest {
    /**
     * Method under test: {@link ASTCatchClause#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTCatchClause astCatchClause = new ASTCatchClause(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTCatchClause>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astCatchClause.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTCatchClause.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTCatchClause#getParameter()}
     */
    @Test
    void testGetParameter() {
        // Arrange, Act and Assert
        assertNull((new ASTCatchClause(1)).getParameter());
    }

    /**
     * Method under test: {@link ASTCatchClause#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange, Act and Assert
        assertNull((new ASTCatchClause(1)).getBody());
    }

    /**
     * Method under test: {@link ASTCatchClause#getBody()}
     */
    @Test
    void testGetBody2() {
        // Arrange
        ASTCatchClause astCatchClause = new ASTCatchClause(1);
        astCatchClause.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astCatchClause.getBody());
    }

    /**
     * Method under test: {@link ASTCatchClause#ASTCatchClause(int)}
     */
    @Test
    void testNewASTCatchClause() {
        // Arrange and Act
        ASTCatchClause actualAstCatchClause = new ASTCatchClause(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstCatchClause.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstCatchClause.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstCatchClause.getImage());
        assertNull(actualAstCatchClause.getFirstChild());
        assertNull(actualAstCatchClause.getLastChild());
        assertNull(actualAstCatchClause.getNextSibling());
        assertNull(actualAstCatchClause.getPreviousSibling());
        assertNull(actualAstCatchClause.getFirstToken());
        assertNull(actualAstCatchClause.getLastToken());
        assertNull(actualAstCatchClause.getBody());
        assertNull(actualAstCatchClause.getParameter());
        assertNull(actualAstCatchClause.getEnclosingType());
        assertNull(actualAstCatchClause.symbolTable);
        assertEquals(0, actualAstCatchClause.getIndexInParent());
        assertEquals(0, actualAstCatchClause.getNumChildren());
        assertFalse(actualAstCatchClause.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstCatchClause.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstCatchClause.children().toList().isEmpty());
        assertSame(actualAstCatchClause, nextResult.getParent());
        assertSame(ancestorsResult, actualAstCatchClause.descendants());
    }
}
