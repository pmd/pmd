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
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTFormalParameterDiffblueTest {
    /**
     * Method under test: {@link ASTFormalParameter#getVisibility()}
     */
    @Test
    void testGetVisibility() {
        // Arrange, Act and Assert
        assertEquals(ModifierOwner.Visibility.V_LOCAL, (new ASTFormalParameter(1)).getVisibility());
    }

    /**
     * Method under test: {@link ASTFormalParameter#getOwnerList()}
     */
    @Test
    void testGetOwnerList() {
        // Arrange, Act and Assert
        assertNull((new ASTFormalParameter(1)).getOwnerList());
    }

    /**
     * Method under test: {@link ASTFormalParameter#getOwnerList()}
     */
    @Test
    void testGetOwnerList2() {
        // Arrange
        ASTFormalParameter astFormalParameter = new ASTFormalParameter(1);
        astFormalParameter.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astFormalParameter.getOwnerList());
    }

    /**
     * Method under test: {@link ASTFormalParameter#isVarargs()}
     */
    @Test
    void testIsVarargs() {
        // Arrange, Act and Assert
        assertFalse((new ASTFormalParameter(1)).isVarargs());
    }

    /**
     * Method under test: {@link ASTFormalParameter#isVarargs()}
     */
    @Test
    void testIsVarargs2() {
        // Arrange
        ASTFormalParameter astFormalParameter = new ASTFormalParameter(1);
        astFormalParameter.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertFalse(astFormalParameter.isVarargs());
    }

    /**
     * Method under test:
     * {@link ASTFormalParameter#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTFormalParameter astFormalParameter = new ASTFormalParameter(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTFormalParameter>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astFormalParameter.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTFormalParameter.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTFormalParameter#getVarId()}
     */
    @Test
    void testGetVarId() {
        // Arrange, Act and Assert
        assertNull((new ASTFormalParameter(1)).getVarId());
    }

    /**
     * Method under test: {@link ASTFormalParameter#getVarId()}
     */
    @Test
    void testGetVarId2() {
        // Arrange
        ASTFormalParameter astFormalParameter = new ASTFormalParameter(1);
        astFormalParameter.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astFormalParameter.getVarId());
    }

    /**
     * Method under test: {@link ASTFormalParameter#getTypeNode()}
     */
    @Test
    void testGetTypeNode() {
        // Arrange, Act and Assert
        assertNull((new ASTFormalParameter(1)).getTypeNode());
    }

    /**
     * Method under test: {@link ASTFormalParameter#getTypeNode()}
     */
    @Test
    void testGetTypeNode2() {
        // Arrange
        ASTFormalParameter astFormalParameter = new ASTFormalParameter(1);
        astFormalParameter.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astFormalParameter.getTypeNode());
    }

    /**
     * Method under test: {@link ASTFormalParameter#getTypeMirror(TypingContext)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetTypeMirror() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTVariableId.getTypeMirror(net.sourceforge.pmd.lang.java.types.TypingContext)" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTFormalParameter.getVarId()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTFormalParameter.getTypeMirror(ASTFormalParameter.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTFormalParameter astFormalParameter = null;
        TypingContext ctx = null;

        // Act
        JTypeMirror actualTypeMirror = astFormalParameter.getTypeMirror(ctx);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFormalParameter#isFinal()}
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
        //       at net.sourceforge.pmd.lang.java.ast.ASTFormalParameter.isFinal(ASTFormalParameter.java:92)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTFormalParameter astFormalParameter = null;

        // Act
        boolean actualIsFinalResult = astFormalParameter.isFinal();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFormalParameter#ASTFormalParameter(int)}
     */
    @Test
    void testNewASTFormalParameter() {
        // Arrange and Act
        ASTFormalParameter actualAstFormalParameter = new ASTFormalParameter(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstFormalParameter.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassBody", actualAstFormalParameter.getXPathNodeName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Varargs", nextResult.getName());
        assertNull(actualAstFormalParameter.getImage());
        assertNull(actualAstFormalParameter.getFirstChild());
        assertNull(actualAstFormalParameter.getLastChild());
        assertNull(actualAstFormalParameter.getNextSibling());
        assertNull(actualAstFormalParameter.getPreviousSibling());
        assertNull(actualAstFormalParameter.getFirstToken());
        assertNull(actualAstFormalParameter.getLastToken());
        assertNull(actualAstFormalParameter.getOwnerList());
        assertNull(actualAstFormalParameter.getModifiers());
        assertNull(actualAstFormalParameter.getTypeNode());
        assertNull(actualAstFormalParameter.getEnclosingType());
        assertNull(actualAstFormalParameter.getVarId());
        assertNull(actualAstFormalParameter.symbolTable);
        assertEquals(0, actualAstFormalParameter.getIndexInParent());
        assertEquals(0, actualAstFormalParameter.getNumChildren());
        assertEquals(ModifierOwner.Visibility.V_LOCAL, actualAstFormalParameter.getVisibility());
        assertEquals(ModifierOwner.Visibility.V_LOCAL, actualAstFormalParameter.getEffectiveVisibility());
        assertFalse(actualAstFormalParameter.isFindBoundary());
        assertFalse(actualAstFormalParameter.isVarargs());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstFormalParameter.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstFormalParameter.children().toList().isEmpty());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstFormalParameter, nextResult.getParent());
        assertSame(ancestorsResult, actualAstFormalParameter.descendants());
    }
}
