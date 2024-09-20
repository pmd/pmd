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
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTConstructorDeclarationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTConstructorDeclaration#ASTConstructorDeclaration(int)}
     */
    @Test
    void testNewASTConstructorDeclaration() {
        // Arrange and Act
        ASTConstructorDeclaration actualAstConstructorDeclaration = new ASTConstructorDeclaration(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstConstructorDeclaration.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstConstructorDeclaration.getXPathNodeName());
        assertEquals("Name", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstConstructorDeclaration.getImage());
        assertNull(actualAstConstructorDeclaration.getName());
        assertNull(actualAstConstructorDeclaration.getFirstChild());
        assertNull(actualAstConstructorDeclaration.getLastChild());
        assertNull(actualAstConstructorDeclaration.getNextSibling());
        assertNull(actualAstConstructorDeclaration.getPreviousSibling());
        assertNull(actualAstConstructorDeclaration.getFirstToken());
        assertNull(actualAstConstructorDeclaration.getLastToken());
        assertNull(actualAstConstructorDeclaration.getBody());
        assertNull(actualAstConstructorDeclaration.getFormalParameters());
        assertNull(actualAstConstructorDeclaration.getModifiers());
        assertNull(actualAstConstructorDeclaration.getThrowsList());
        assertNull(actualAstConstructorDeclaration.getEnclosingType());
        assertNull(actualAstConstructorDeclaration.getTypeParameters());
        assertNull(actualAstConstructorDeclaration.getJavadocComment());
        assertNull(actualAstConstructorDeclaration.symbolTable);
        assertEquals(0, actualAstConstructorDeclaration.getIndexInParent());
        assertEquals(0, actualAstConstructorDeclaration.getNumChildren());
        assertFalse(actualAstConstructorDeclaration.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstConstructorDeclaration.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstConstructorDeclaration.children().toList().isEmpty());
        assertSame(actualAstConstructorDeclaration, nextResult.getParent());
        assertSame(ancestorsResult, actualAstConstructorDeclaration.descendants());
    }

    /**
     * Method under test: {@link ASTConstructorDeclaration#getName()}
     */
    @Test
    void testGetName() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorDeclaration(1)).getName());
    }

    /**
     * Method under test: {@link ASTConstructorDeclaration#getName()}
     */
    @Test
    void testGetName2() {
        // Arrange
        ASTConstructorDeclaration astConstructorDeclaration = new ASTConstructorDeclaration(1);
        astConstructorDeclaration.setSymbol(mock(JConstructorSymbol.class));

        // Act and Assert
        assertNull(astConstructorDeclaration.getName());
    }

    /**
     * Method under test: {@link ASTConstructorDeclaration#getReportLocation()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetReportLocation() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTModifierList.getLastToken()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration.getModifiers()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration.getReportLocation(ASTConstructorDeclaration.java:40)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTConstructorDeclaration astConstructorDeclaration = null;

        // Act
        FileLocation actualReportLocation = astConstructorDeclaration.getReportLocation();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTConstructorDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTConstructorDeclaration astConstructorDeclaration = new ASTConstructorDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTConstructorDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astConstructorDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTConstructorDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTConstructorDeclaration#containsComment()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testContainsComment() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTBlock.containsComment()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration.getBody()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration.containsComment(ASTConstructorDeclaration.java:50)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTConstructorDeclaration astConstructorDeclaration = null;

        // Act
        boolean actualContainsCommentResult = astConstructorDeclaration.containsComment();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTConstructorDeclaration#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorDeclaration(1)).getBody());
    }
}
