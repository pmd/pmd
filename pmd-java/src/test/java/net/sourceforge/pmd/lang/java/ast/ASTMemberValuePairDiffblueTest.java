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

class ASTMemberValuePairDiffblueTest {
    /**
     * Method under test: {@link ASTMemberValuePair#getName()}
     */
    @Test
    void testGetName() {
        // Arrange, Act and Assert
        assertNull((new ASTMemberValuePair(1)).getName());
    }

    /**
     * Method under test: {@link ASTMemberValuePair#getName()}
     */
    @Test
    void testGetName2() {
        // Arrange
        ASTMemberValuePair astMemberValuePair = new ASTMemberValuePair(1);
        astMemberValuePair.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astMemberValuePair.getName());
    }

    /**
     * Method under test: {@link ASTMemberValuePair#getValue()}
     */
    @Test
    void testGetValue() {
        // Arrange
        ASTMemberValuePair astMemberValuePair = new ASTMemberValuePair(1);
        astMemberValuePair.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astMemberValuePair.getValue());
    }

    /**
     * Method under test: {@link ASTMemberValuePair#getValue()}
     */
    @Test
    void testGetValue2() {
        // Arrange
        ASTMemberValuePair astMemberValuePair = new ASTMemberValuePair(1);
        astMemberValuePair.setSymbolTable(mock(JSymbolTable.class));
        astMemberValuePair.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astMemberValuePair.getValue());
    }

    /**
     * Method under test:
     * {@link ASTMemberValuePair#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTMemberValuePair astMemberValuePair = new ASTMemberValuePair(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTMemberValuePair>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astMemberValuePair.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTMemberValuePair.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTMemberValuePair#setShorthand()}
     *   <li>{@link ASTMemberValuePair#isShorthand()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTMemberValuePair astMemberValuePair = new ASTMemberValuePair(1);

        // Act
        astMemberValuePair.setShorthand();

        // Assert that nothing has changed
        assertTrue(astMemberValuePair.isShorthand());
    }

    /**
     * Method under test: {@link ASTMemberValuePair#ASTMemberValuePair(int)}
     */
    @Test
    void testNewASTMemberValuePair() {
        // Arrange and Act
        ASTMemberValuePair actualAstMemberValuePair = new ASTMemberValuePair(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstMemberValuePair.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstMemberValuePair.getXPathNodeName());
        assertEquals("Name", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstMemberValuePair.getImage());
        assertNull(actualAstMemberValuePair.getName());
        assertNull(actualAstMemberValuePair.getFirstChild());
        assertNull(actualAstMemberValuePair.getLastChild());
        assertNull(actualAstMemberValuePair.getNextSibling());
        assertNull(actualAstMemberValuePair.getPreviousSibling());
        assertNull(actualAstMemberValuePair.getFirstToken());
        assertNull(actualAstMemberValuePair.getLastToken());
        assertNull(actualAstMemberValuePair.getEnclosingType());
        assertNull(actualAstMemberValuePair.symbolTable);
        assertEquals(0, actualAstMemberValuePair.getIndexInParent());
        assertEquals(0, actualAstMemberValuePair.getNumChildren());
        assertFalse(actualAstMemberValuePair.isFindBoundary());
        assertFalse(actualAstMemberValuePair.isShorthand());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstMemberValuePair.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstMemberValuePair.children().toList().isEmpty());
        assertSame(actualAstMemberValuePair, nextResult.getParent());
        assertSame(ancestorsResult, actualAstMemberValuePair.descendants());
    }
}
