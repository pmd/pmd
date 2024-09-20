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
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTCharLiteralDiffblueTest {
    /**
     * Method under test: {@link ASTCharLiteral#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTCharLiteral astCharLiteral = new ASTCharLiteral(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTCharLiteral>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astCharLiteral.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTCharLiteral.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTCharLiteral#getConstValue()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetConstValue() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken.getImageCs()" because "this.literalToken" is null
        //       at net.sourceforge.pmd.lang.java.ast.AbstractLiteral.getLiteralText(AbstractLiteral.java:53)
        //       at net.sourceforge.pmd.lang.java.ast.ASTCharLiteral.getLiteralText(ASTCharLiteral.java:44)
        //       at net.sourceforge.pmd.lang.java.ast.ASTCharLiteral.getConstValue(ASTCharLiteral.java:37)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCharLiteral astCharLiteral = null;

        // Act
        Character actualConstValue = astCharLiteral.getConstValue();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTCharLiteral#getLiteralText()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetLiteralText() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken.getImageCs()" because "this.literalToken" is null
        //       at net.sourceforge.pmd.lang.java.ast.AbstractLiteral.getLiteralText(AbstractLiteral.java:53)
        //       at net.sourceforge.pmd.lang.java.ast.ASTCharLiteral.getLiteralText(ASTCharLiteral.java:44)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCharLiteral astCharLiteral = null;

        // Act
        Chars actualLiteralText = astCharLiteral.getLiteralText();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTCharLiteral#ASTCharLiteral(int)}
     */
    @Test
    void testNewASTCharLiteral() {
        // Arrange and Act
        ASTCharLiteral actualAstCharLiteral = new ASTCharLiteral(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstCharLiteral.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstCharLiteral.getXPathNodeName());
        assertEquals("LiteralText", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstCharLiteral.getImage());
        assertNull(actualAstCharLiteral.getFirstChild());
        assertNull(actualAstCharLiteral.getLastChild());
        assertNull(actualAstCharLiteral.getNextSibling());
        assertNull(actualAstCharLiteral.getPreviousSibling());
        assertNull(actualAstCharLiteral.getFirstToken());
        assertNull(actualAstCharLiteral.getLastToken());
        assertNull(actualAstCharLiteral.getEnclosingType());
        assertNull(actualAstCharLiteral.symbolTable);
        assertNull(actualAstCharLiteral.getTypeMirrorInternal());
        assertEquals(0, actualAstCharLiteral.getIndexInParent());
        assertEquals(0, actualAstCharLiteral.getNumChildren());
        assertFalse(actualAstCharLiteral.isFindBoundary());
        assertFalse(actualAstCharLiteral.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstCharLiteral.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstCharLiteral.children().toList().isEmpty());
        assertTrue(actualAstCharLiteral.isExpression());
        assertSame(actualAstCharLiteral, nextResult.getParent());
        assertSame(ancestorsResult, actualAstCharLiteral.descendants());
    }
}
