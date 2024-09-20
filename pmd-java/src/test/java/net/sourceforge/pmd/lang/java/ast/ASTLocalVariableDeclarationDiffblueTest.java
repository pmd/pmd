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

class ASTLocalVariableDeclarationDiffblueTest {
    /**
     * Method under test: {@link ASTLocalVariableDeclaration#getReportLocation()}
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
        //       at net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration.getReportLocation(ASTLocalVariableDeclaration.java:39)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTLocalVariableDeclaration astLocalVariableDeclaration = null;

        // Act
        FileLocation actualReportLocation = astLocalVariableDeclaration.getReportLocation();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTLocalVariableDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTLocalVariableDeclaration astLocalVariableDeclaration = new ASTLocalVariableDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTLocalVariableDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astLocalVariableDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTLocalVariableDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTLocalVariableDeclaration#getVisibility()}
     */
    @Test
    void testGetVisibility() {
        // Arrange, Act and Assert
        assertEquals(ModifierOwner.Visibility.V_LOCAL, (new ASTLocalVariableDeclaration(1)).getVisibility());
    }

    /**
     * Method under test: {@link ASTLocalVariableDeclaration#isTypeInferred()}
     */
    @Test
    void testIsTypeInferred() {
        // Arrange, Act and Assert
        assertTrue((new ASTLocalVariableDeclaration(1)).isTypeInferred());
    }

    /**
     * Method under test: {@link ASTLocalVariableDeclaration#isTypeInferred()}
     */
    @Test
    void testIsTypeInferred2() {
        // Arrange
        ASTLocalVariableDeclaration astLocalVariableDeclaration = new ASTLocalVariableDeclaration(1);
        astLocalVariableDeclaration.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astLocalVariableDeclaration.isTypeInferred());
    }

    /**
     * Method under test: {@link ASTLocalVariableDeclaration#isTypeInferred()}
     */
    @Test
    void testIsTypeInferred3() {
        // Arrange
        ASTLocalVariableDeclaration astLocalVariableDeclaration = new ASTLocalVariableDeclaration(1);
        astLocalVariableDeclaration.setSymbolTable(mock(JSymbolTable.class));
        astLocalVariableDeclaration.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astLocalVariableDeclaration.isTypeInferred());
    }

    /**
     * Method under test: {@link ASTLocalVariableDeclaration#getTypeNode()}
     */
    @Test
    void testGetTypeNode() {
        // Arrange, Act and Assert
        assertNull((new ASTLocalVariableDeclaration(1)).getTypeNode());
    }

    /**
     * Method under test: {@link ASTLocalVariableDeclaration#getTypeNode()}
     */
    @Test
    void testGetTypeNode2() {
        // Arrange
        ASTLocalVariableDeclaration astLocalVariableDeclaration = new ASTLocalVariableDeclaration(1);
        astLocalVariableDeclaration.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astLocalVariableDeclaration.getTypeNode());
    }

    /**
     * Method under test: {@link ASTLocalVariableDeclaration#isFinal()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsFinal() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTModifierList.hasAll(net.sourceforge.pmd.lang.java.ast.JModifier, net.sourceforge.pmd.lang.java.ast.JModifier[])" because the return value of "net.sourceforge.pmd.lang.java.ast.ModifierOwner.getModifiers()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ModifierOwner.hasModifiers(ModifierOwner.java:97)
        //       at net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration.isFinal(ASTLocalVariableDeclaration.java:78)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTLocalVariableDeclaration astLocalVariableDeclaration = null;

        // Act
        boolean actualIsFinalResult = astLocalVariableDeclaration.isFinal();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTLocalVariableDeclaration#ASTLocalVariableDeclaration(int)}
     */
    @Test
    void testNewASTLocalVariableDeclaration() {
        // Arrange and Act
        ASTLocalVariableDeclaration actualAstLocalVariableDeclaration = new ASTLocalVariableDeclaration(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstLocalVariableDeclaration.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstLocalVariableDeclaration.getXPathNodeName());
        assertEquals("Final", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstLocalVariableDeclaration.getImage());
        assertNull(actualAstLocalVariableDeclaration.getFirstChild());
        assertNull(actualAstLocalVariableDeclaration.getLastChild());
        assertNull(actualAstLocalVariableDeclaration.getNextSibling());
        assertNull(actualAstLocalVariableDeclaration.getPreviousSibling());
        assertNull(actualAstLocalVariableDeclaration.getFirstToken());
        assertNull(actualAstLocalVariableDeclaration.getLastToken());
        assertNull(actualAstLocalVariableDeclaration.getModifiers());
        assertNull(actualAstLocalVariableDeclaration.getTypeNode());
        assertNull(actualAstLocalVariableDeclaration.getEnclosingType());
        assertNull(actualAstLocalVariableDeclaration.symbolTable);
        assertEquals(0, actualAstLocalVariableDeclaration.getIndexInParent());
        assertEquals(0, actualAstLocalVariableDeclaration.getNumChildren());
        assertEquals(ModifierOwner.Visibility.V_LOCAL, actualAstLocalVariableDeclaration.getVisibility());
        assertEquals(ModifierOwner.Visibility.V_LOCAL, actualAstLocalVariableDeclaration.getEffectiveVisibility());
        assertFalse(actualAstLocalVariableDeclaration.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstLocalVariableDeclaration.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstLocalVariableDeclaration.children().toList().isEmpty());
        assertTrue(actualAstLocalVariableDeclaration.isTypeInferred());
        assertSame(actualAstLocalVariableDeclaration, nextResult.getParent());
        assertSame(ancestorsResult, actualAstLocalVariableDeclaration.descendants());
        assertSame(ancestorsResult, actualAstLocalVariableDeclaration.getVarIds());
    }
}
