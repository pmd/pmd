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

class ASTCatchParameterDiffblueTest {
    /**
     * Method under test:
     * {@link ASTCatchParameter#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTCatchParameter astCatchParameter = new ASTCatchParameter(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTCatchParameter>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astCatchParameter.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTCatchParameter.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTCatchParameter#isMulticatch()}
     */
    @Test
    void testIsMulticatch() {
        // Arrange
        ASTCatchParameter astCatchParameter = new ASTCatchParameter(1);
        astCatchParameter.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astCatchParameter.isMulticatch());
    }

    /**
     * Method under test: {@link ASTCatchParameter#isMulticatch()}
     */
    @Test
    void testIsMulticatch2() {
        // Arrange
        ASTCatchParameter astCatchParameter = new ASTCatchParameter(1);
        astCatchParameter.setSymbolTable(mock(JSymbolTable.class));
        astCatchParameter.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astCatchParameter.isMulticatch());
    }

    /**
     * Method under test: {@link ASTCatchParameter#getVarId()}
     */
    @Test
    void testGetVarId() {
        // Arrange, Act and Assert
        assertNull((new ASTCatchParameter(1)).getVarId());
    }

    /**
     * Method under test: {@link ASTCatchParameter#getName()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTVariableId.getName()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTCatchParameter.getVarId()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTCatchParameter.getName(ASTCatchParameter.java:59)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCatchParameter astCatchParameter = null;

        // Act
        String actualName = astCatchParameter.getName();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTCatchParameter#getTypeNode()}
     */
    @Test
    void testGetTypeNode() {
        // Arrange
        ASTCatchParameter astCatchParameter = new ASTCatchParameter(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astCatchParameter.addChild(child, 1);

        // Act and Assert
        assertSame(child, astCatchParameter.getTypeNode());
    }

    /**
     * Method under test: {@link ASTCatchParameter#getTypeNode()}
     */
    @Test
    void testGetTypeNode2() {
        // Arrange
        ASTCatchParameter astCatchParameter = new ASTCatchParameter(1);
        astCatchParameter.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astCatchParameter.addChild(child, 1);

        // Act and Assert
        assertSame(child, astCatchParameter.getTypeNode());
    }

    /**
     * Method under test: {@link ASTCatchParameter#getAllExceptionTypes()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetAllExceptionTypes() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCatchParameter astCatchParameter = null;

        // Act
        NodeStream<ASTClassType> actualAllExceptionTypes = astCatchParameter.getAllExceptionTypes();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTCatchParameter#isFinal()}
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
        //       at net.sourceforge.pmd.lang.java.ast.ASTCatchParameter.isFinal(ASTCatchParameter.java:92)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCatchParameter astCatchParameter = null;

        // Act
        boolean actualIsFinalResult = astCatchParameter.isFinal();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTCatchParameter#ASTCatchParameter(int)}
     */
    @Test
    void testNewASTCatchParameter() {
        // Arrange and Act
        ASTCatchParameter actualAstCatchParameter = new ASTCatchParameter(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstCatchParameter.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstCatchParameter.getXPathNodeName());
        assertEquals("Name", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstCatchParameter.getImage());
        assertNull(actualAstCatchParameter.getFirstChild());
        assertNull(actualAstCatchParameter.getLastChild());
        assertNull(actualAstCatchParameter.getNextSibling());
        assertNull(actualAstCatchParameter.getPreviousSibling());
        assertNull(actualAstCatchParameter.getFirstToken());
        assertNull(actualAstCatchParameter.getLastToken());
        assertNull(actualAstCatchParameter.getModifiers());
        assertNull(actualAstCatchParameter.getEnclosingType());
        assertNull(actualAstCatchParameter.getVarId());
        assertNull(actualAstCatchParameter.symbolTable);
        assertEquals(0, actualAstCatchParameter.getIndexInParent());
        assertEquals(0, actualAstCatchParameter.getNumChildren());
        assertFalse(actualAstCatchParameter.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstCatchParameter.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstCatchParameter.children().toList().isEmpty());
        assertSame(actualAstCatchParameter, nextResult.getParent());
        assertSame(ancestorsResult, actualAstCatchParameter.descendants());
    }
}
