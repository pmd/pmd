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
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTCompactConstructorDeclarationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTCompactConstructorDeclaration#ASTCompactConstructorDeclaration(int)}
     */
    @Test
    void testNewASTCompactConstructorDeclaration() {
        // Arrange and Act
        ASTCompactConstructorDeclaration actualAstCompactConstructorDeclaration = new ASTCompactConstructorDeclaration(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstCompactConstructorDeclaration.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstCompactConstructorDeclaration.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstCompactConstructorDeclaration.getImage());
        assertNull(actualAstCompactConstructorDeclaration.getFirstChild());
        assertNull(actualAstCompactConstructorDeclaration.getLastChild());
        assertNull(actualAstCompactConstructorDeclaration.getNextSibling());
        assertNull(actualAstCompactConstructorDeclaration.getPreviousSibling());
        assertNull(actualAstCompactConstructorDeclaration.getFirstToken());
        assertNull(actualAstCompactConstructorDeclaration.getLastToken());
        assertNull(actualAstCompactConstructorDeclaration.getBody());
        assertNull(actualAstCompactConstructorDeclaration.getModifiers());
        assertNull(actualAstCompactConstructorDeclaration.getEnclosingType());
        assertNull(actualAstCompactConstructorDeclaration.symbolTable);
        assertEquals(0, actualAstCompactConstructorDeclaration.getIndexInParent());
        assertEquals(0, actualAstCompactConstructorDeclaration.getNumChildren());
        assertFalse(actualAstCompactConstructorDeclaration.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstCompactConstructorDeclaration.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstCompactConstructorDeclaration.children().toList().isEmpty());
        assertSame(actualAstCompactConstructorDeclaration, nextResult.getParent());
        assertSame(ancestorsResult, actualAstCompactConstructorDeclaration.descendants());
    }

    /**
     * Method under test:
     * {@link ASTCompactConstructorDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTCompactConstructorDeclaration astCompactConstructorDeclaration = new ASTCompactConstructorDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTCompactConstructorDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astCompactConstructorDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTCompactConstructorDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTCompactConstructorDeclaration#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange, Act and Assert
        assertNull((new ASTCompactConstructorDeclaration(1)).getBody());
    }

    /**
     * Method under test: {@link ASTCompactConstructorDeclaration#getBody()}
     */
    @Test
    void testGetBody2() {
        // Arrange
        ASTCompactConstructorDeclaration astCompactConstructorDeclaration = new ASTCompactConstructorDeclaration(1);
        astCompactConstructorDeclaration.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astCompactConstructorDeclaration.getBody());
    }

    /**
     * Method under test:
     * {@link ASTCompactConstructorDeclaration#getDeclarationNode()}
     */
    @Test
    void testGetDeclarationNode() {
        // Arrange
        ASTCompactConstructorDeclaration astCompactConstructorDeclaration = new ASTCompactConstructorDeclaration(1);

        // Act and Assert
        assertSame(astCompactConstructorDeclaration, astCompactConstructorDeclaration.getDeclarationNode());
    }

    /**
     * Method under test:
     * {@link ASTCompactConstructorDeclaration#getDeclarationNode()}
     */
    @Test
    void testGetDeclarationNode2() {
        // Arrange
        ASTCompactConstructorDeclaration astCompactConstructorDeclaration = new ASTCompactConstructorDeclaration(1);
        astCompactConstructorDeclaration.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertSame(astCompactConstructorDeclaration, astCompactConstructorDeclaration.getDeclarationNode());
    }

    /**
     * Method under test:
     * {@link ASTCompactConstructorDeclaration#getEnclosingType()}
     */
    @Test
    void testGetEnclosingType() {
        // Arrange, Act and Assert
        assertNull((new ASTCompactConstructorDeclaration(1)).getEnclosingType());
    }

    /**
     * Method under test:
     * {@link ASTCompactConstructorDeclaration#getEnclosingType()}
     */
    @Test
    void testGetEnclosingType2() {
        // Arrange
        ASTCompactConstructorDeclaration astCompactConstructorDeclaration = new ASTCompactConstructorDeclaration(1);
        astCompactConstructorDeclaration.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astCompactConstructorDeclaration.getEnclosingType());
    }

    /**
     * Method under test: {@link ASTCompactConstructorDeclaration#getSymbol()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetSymbol() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration.getRecordComponents()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTCompactConstructorDeclaration.getEnclosingType()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTCompactConstructorDeclaration.getSymbol(ASTCompactConstructorDeclaration.java:53)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCompactConstructorDeclaration astCompactConstructorDeclaration = null;

        // Act
        JConstructorSymbol actualSymbol = astCompactConstructorDeclaration.getSymbol();

        // Assert
        // TODO: Add assertions on result
    }
}
