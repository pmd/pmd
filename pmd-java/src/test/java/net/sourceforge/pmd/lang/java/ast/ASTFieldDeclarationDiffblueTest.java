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
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTFieldDeclarationDiffblueTest {
    /**
     * Method under test: {@link ASTFieldDeclaration#getReportLocation()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetReportLocation() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.util.NoSuchElementException: Empty node stream
        //       at net.sourceforge.pmd.lang.ast.NodeStream.firstOrThrow(NodeStream.java:783)
        //       at net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration.getReportLocation(ASTFieldDeclaration.java:39)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTFieldDeclaration astFieldDeclaration = null;

        // Act
        FileLocation actualReportLocation = astFieldDeclaration.getReportLocation();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTFieldDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTFieldDeclaration astFieldDeclaration = new ASTFieldDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTFieldDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astFieldDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTFieldDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTFieldDeclaration#getVariableName()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetVariableName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.util.NoSuchElementException: Empty node stream
        //       at net.sourceforge.pmd.lang.ast.NodeStream.firstOrThrow(NodeStream.java:783)
        //       at net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration.getVariableName(ASTFieldDeclaration.java:60)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTFieldDeclaration astFieldDeclaration = null;

        // Act
        String actualVariableName = astFieldDeclaration.getVariableName();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFieldDeclaration#getTypeNode()}
     */
    @Test
    void testGetTypeNode() {
        // Arrange, Act and Assert
        assertNull((new ASTFieldDeclaration(1)).getTypeNode());
    }

    /**
     * Method under test: {@link ASTFieldDeclaration#getTypeNode()}
     */
    @Test
    void testGetTypeNode2() {
        // Arrange
        ASTFieldDeclaration astFieldDeclaration = new ASTFieldDeclaration(1);
        astFieldDeclaration.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astFieldDeclaration.getTypeNode());
    }

    /**
     * Method under test: {@link ASTFieldDeclaration#isStatic()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsStatic() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTModifierList.hasAll(net.sourceforge.pmd.lang.java.ast.JModifier, net.sourceforge.pmd.lang.java.ast.JModifier[])" because the return value of "net.sourceforge.pmd.lang.java.ast.ModifierOwner.getModifiers()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ModifierOwner.hasModifiers(ModifierOwner.java:97)
        //       at net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration.isStatic(ASTFieldDeclaration.java:78)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTFieldDeclaration astFieldDeclaration = null;

        // Act
        boolean actualIsStaticResult = astFieldDeclaration.isStatic();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFieldDeclaration#ASTFieldDeclaration(int)}
     */
    @Test
    void testNewASTFieldDeclaration() {
        // Arrange and Act
        ASTFieldDeclaration actualAstFieldDeclaration = new ASTFieldDeclaration(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstFieldDeclaration.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstFieldDeclaration.getXPathNodeName());
        assertEquals("Static", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstFieldDeclaration.getImage());
        assertNull(actualAstFieldDeclaration.getFirstChild());
        assertNull(actualAstFieldDeclaration.getLastChild());
        assertNull(actualAstFieldDeclaration.getNextSibling());
        assertNull(actualAstFieldDeclaration.getPreviousSibling());
        assertNull(actualAstFieldDeclaration.getFirstToken());
        assertNull(actualAstFieldDeclaration.getLastToken());
        assertNull(actualAstFieldDeclaration.getModifiers());
        assertNull(actualAstFieldDeclaration.getTypeNode());
        assertNull(actualAstFieldDeclaration.getEnclosingType());
        assertNull(actualAstFieldDeclaration.getJavadocComment());
        assertNull(actualAstFieldDeclaration.symbolTable);
        assertEquals(0, actualAstFieldDeclaration.getIndexInParent());
        assertEquals(0, actualAstFieldDeclaration.getNumChildren());
        assertFalse(actualAstFieldDeclaration.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstFieldDeclaration.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstFieldDeclaration.children().toList().isEmpty());
        assertSame(actualAstFieldDeclaration, nextResult.getParent());
        assertSame(ancestorsResult, actualAstFieldDeclaration.descendants());
        assertSame(ancestorsResult, actualAstFieldDeclaration.getVarIds());
    }
}
