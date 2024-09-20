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

import java.lang.reflect.Type;
import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTBooleanLiteralDiffblueTest {
    /**
     * Method under test: {@link ASTBooleanLiteral#getConstValue()}
     */
    @Test
    void testGetConstValue() {
        // Arrange
        ASTBooleanLiteral astBooleanLiteral = new ASTBooleanLiteral(1);

        // Act
        Boolean actualConstValue = astBooleanLiteral.getConstValue();

        // Assert
        Iterator<Attribute> xPathAttributesIterator = astBooleanLiteral.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertFalse(actualConstValue);
        Class<Boolean> expectedType = actualConstValue.TYPE;
        assertSame(expectedType, xPathAttributesIterator.next().getType());
    }

    /**
     * Method under test: {@link ASTBooleanLiteral#getConstValue()}
     */
    @Test
    void testGetConstValue2() {
        // Arrange
        ASTBooleanLiteral astBooleanLiteral = new ASTBooleanLiteral(1);
        astBooleanLiteral.setSymbolTable(mock(JSymbolTable.class));

        // Act
        Boolean actualConstValue = astBooleanLiteral.getConstValue();

        // Assert
        Iterator<Attribute> xPathAttributesIterator = astBooleanLiteral.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertFalse(actualConstValue);
        Class<Boolean> expectedType = actualConstValue.TYPE;
        assertSame(expectedType, xPathAttributesIterator.next().getType());
    }

    /**
     * Method under test: {@link ASTBooleanLiteral#getLiteralText()}
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
        //       at net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral.getLiteralText(ASTBooleanLiteral.java:39)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTBooleanLiteral astBooleanLiteral = null;

        // Act
        Chars actualLiteralText = astBooleanLiteral.getLiteralText();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTBooleanLiteral#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTBooleanLiteral astBooleanLiteral = new ASTBooleanLiteral(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTBooleanLiteral>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astBooleanLiteral.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTBooleanLiteral.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTBooleanLiteral#setTrue()}
     *   <li>{@link ASTBooleanLiteral#isTrue()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTBooleanLiteral astBooleanLiteral = new ASTBooleanLiteral(1);

        // Act
        astBooleanLiteral.setTrue();

        // Assert that nothing has changed
        assertTrue(astBooleanLiteral.isTrue());
    }

    /**
     * Method under test: {@link ASTBooleanLiteral#ASTBooleanLiteral(int)}
     */
    @Test
    void testNewASTBooleanLiteral() {
        // Arrange and Act
        ASTBooleanLiteral actualAstBooleanLiteral = new ASTBooleanLiteral(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstBooleanLiteral.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassBody", actualAstBooleanLiteral.getXPathNodeName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("True", nextResult.getName());
        assertNull(actualAstBooleanLiteral.getImage());
        assertNull(actualAstBooleanLiteral.getFirstChild());
        assertNull(actualAstBooleanLiteral.getLastChild());
        assertNull(actualAstBooleanLiteral.getNextSibling());
        assertNull(actualAstBooleanLiteral.getPreviousSibling());
        assertNull(actualAstBooleanLiteral.getFirstToken());
        assertNull(actualAstBooleanLiteral.getLastToken());
        assertNull(actualAstBooleanLiteral.getEnclosingType());
        assertNull(actualAstBooleanLiteral.symbolTable);
        assertNull(actualAstBooleanLiteral.getTypeMirrorInternal());
        assertEquals(0, actualAstBooleanLiteral.getIndexInParent());
        assertEquals(0, actualAstBooleanLiteral.getNumChildren());
        assertFalse(actualAstBooleanLiteral.isFindBoundary());
        assertFalse(actualAstBooleanLiteral.getConstValue());
        assertFalse(actualAstBooleanLiteral.isTrue());
        assertFalse(actualAstBooleanLiteral.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstBooleanLiteral.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstBooleanLiteral.children().toList().isEmpty());
        assertTrue(actualAstBooleanLiteral.isExpression());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstBooleanLiteral, nextResult.getParent());
        assertSame(ancestorsResult, actualAstBooleanLiteral.descendants());
    }
}
