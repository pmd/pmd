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
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.WildcardDsl;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTLambdaExpressionDiffblueTest {
    /**
     * Method under test: {@link ASTLambdaExpression#getTypeMirror()}
     */
    @Test
    void testGetTypeMirror() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        WildcardDsl mirror = new WildcardDsl(JavaParsingHelper.TEST_TYPE_SYSTEM);
        astLambdaExpression.setTypeMirror(mirror);

        // Act and Assert
        assertSame(mirror, astLambdaExpression.getTypeMirror());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getFunctionalMethod()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetFunctionalMethod() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTLambdaExpression astLambdaExpression = null;

        // Act
        JMethodSig actualFunctionalMethod = astLambdaExpression.getFunctionalMethod();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getParameters()}
     */
    @Test
    void testGetParameters() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLambdaExpression.getParameters());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getParameters()}
     */
    @Test
    void testGetParameters2() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLambdaExpression.getParameters());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#isExplicitlyTyped()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsExplicitlyTyped() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTLambdaExpression astLambdaExpression = null;

        // Act
        boolean actualIsExplicitlyTypedResult = astLambdaExpression.isExplicitlyTyped();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTLambdaExpression#isBlockBody()}
     */
    @Test
    void testIsBlockBody() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astLambdaExpression.isBlockBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#isBlockBody()}
     */
    @Test
    void testIsBlockBody2() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astLambdaExpression.isBlockBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#isExpressionBody()}
     */
    @Test
    void testIsExpressionBody() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astLambdaExpression.isExpressionBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#isExpressionBody()}
     */
    @Test
    void testIsExpressionBody2() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astLambdaExpression.isExpressionBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getBlock()}
     */
    @Test
    void testGetBlock() {
        // Arrange, Act and Assert
        assertNull((new ASTLambdaExpression(1)).getBlock());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getBlock()}
     */
    @Test
    void testGetBlock2() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLambdaExpression.getBlock());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getBlock()}
     */
    @Test
    void testGetBlock3() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLambdaExpression.getBlock());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpression()}
     */
    @Test
    void testGetExpression() {
        // Arrange, Act and Assert
        assertNull((new ASTLambdaExpression(1)).getExpression());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpression()}
     */
    @Test
    void testGetExpression2() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astLambdaExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astLambdaExpression.getExpression());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpression()}
     */
    @Test
    void testGetExpression3() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astLambdaExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astLambdaExpression.getExpression());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpression()}
     */
    @Test
    void testGetExpression4() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 2);

        // Act and Assert
        assertNull(astLambdaExpression.getExpression());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getBlockBody()}
     */
    @Test
    void testGetBlockBody() {
        // Arrange, Act and Assert
        assertNull((new ASTLambdaExpression(1)).getBlockBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getBlockBody()}
     */
    @Test
    void testGetBlockBody2() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLambdaExpression.getBlockBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getBlockBody()}
     */
    @Test
    void testGetBlockBody3() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astLambdaExpression.getBlockBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpressionBody()}
     */
    @Test
    void testGetExpressionBody() {
        // Arrange, Act and Assert
        assertNull((new ASTLambdaExpression(1)).getExpressionBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpressionBody()}
     */
    @Test
    void testGetExpressionBody2() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astLambdaExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astLambdaExpression.getExpressionBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpressionBody()}
     */
    @Test
    void testGetExpressionBody3() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astLambdaExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astLambdaExpression.getExpressionBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getExpressionBody()}
     */
    @Test
    void testGetExpressionBody4() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        astLambdaExpression.addChild(new ASTAmbiguousName(1), 2);

        // Act and Assert
        assertNull(astLambdaExpression.getExpressionBody());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#getArity()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetArity() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
        //       at net.sourceforge.pmd.lang.ast.impl.AbstractNode.getChild(AbstractNode.java:59)
        //       at net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression.getParameters(ASTLambdaExpression.java:63)
        //       at net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression.getArity(ASTLambdaExpression.java:131)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTLambdaExpression astLambdaExpression = null;

        // Act
        int actualArity = astLambdaExpression.getArity();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTLambdaExpression#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTLambdaExpression>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astLambdaExpression.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTLambdaExpression.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTLambdaExpression#setFunctionalMethod(JMethodSig)}
     *   <li>{@link ASTLambdaExpression#isFindBoundary()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTLambdaExpression astLambdaExpression = new ASTLambdaExpression(1);

        // Act
        astLambdaExpression.setFunctionalMethod(mock(JMethodSig.class));

        // Assert that nothing has changed
        assertTrue(astLambdaExpression.isFindBoundary());
    }

    /**
     * Method under test: {@link ASTLambdaExpression#ASTLambdaExpression(int)}
     */
    @Test
    void testNewASTLambdaExpression() {
        // Arrange and Act
        ASTLambdaExpression actualAstLambdaExpression = new ASTLambdaExpression(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstLambdaExpression.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("Arity", nextResult.getName());
        assertEquals("ClassBody", actualAstLambdaExpression.getXPathNodeName());
        assertNull(nextResult.getValue());
        assertNull(actualAstLambdaExpression.getImage());
        assertNull(actualAstLambdaExpression.getFirstChild());
        assertNull(actualAstLambdaExpression.getLastChild());
        assertNull(actualAstLambdaExpression.getNextSibling());
        assertNull(actualAstLambdaExpression.getPreviousSibling());
        assertNull(actualAstLambdaExpression.getFirstToken());
        assertNull(actualAstLambdaExpression.getLastToken());
        assertNull(actualAstLambdaExpression.getBlock());
        assertNull(actualAstLambdaExpression.getBlockBody());
        assertNull(actualAstLambdaExpression.getExpression());
        assertNull(actualAstLambdaExpression.getExpressionBody());
        assertNull(actualAstLambdaExpression.getEnclosingType());
        assertNull(actualAstLambdaExpression.symbolTable);
        assertNull(actualAstLambdaExpression.getTypeMirrorInternal());
        assertEquals(0, actualAstLambdaExpression.getIndexInParent());
        assertEquals(0, actualAstLambdaExpression.getNumChildren());
        assertFalse(actualAstLambdaExpression.isCompileTimeConstant());
        assertFalse(actualAstLambdaExpression.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstLambdaExpression.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstLambdaExpression.children().toList().isEmpty());
        assertTrue(actualAstLambdaExpression.isExpression());
        assertTrue(actualAstLambdaExpression.isFindBoundary());
        assertSame(actualAstLambdaExpression, nextResult.getParent());
        assertSame(ancestorsResult, actualAstLambdaExpression.descendants());
    }
}
