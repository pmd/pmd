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

class ASTAssignmentExpressionDiffblueTest {
    /**
     * Method under test:
     * {@link ASTAssignmentExpression#ASTAssignmentExpression(int)}
     */
    @Test
    void testNewASTAssignmentExpression() {
        // Arrange and Act
        ASTAssignmentExpression actualAstAssignmentExpression = new ASTAssignmentExpression(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstAssignmentExpression.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstAssignmentExpression.getXPathNodeName());
        assertEquals("Operator", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstAssignmentExpression.getImage());
        assertNull(actualAstAssignmentExpression.getFirstChild());
        assertNull(actualAstAssignmentExpression.getLastChild());
        assertNull(actualAstAssignmentExpression.getNextSibling());
        assertNull(actualAstAssignmentExpression.getPreviousSibling());
        assertNull(actualAstAssignmentExpression.getFirstToken());
        assertNull(actualAstAssignmentExpression.getLastToken());
        assertNull(actualAstAssignmentExpression.getEnclosingType());
        assertNull(actualAstAssignmentExpression.getOperator());
        assertNull(actualAstAssignmentExpression.symbolTable);
        assertNull(actualAstAssignmentExpression.getTypeMirrorInternal());
        assertEquals(0, actualAstAssignmentExpression.getIndexInParent());
        assertEquals(0, actualAstAssignmentExpression.getNumChildren());
        assertFalse(actualAstAssignmentExpression.isFindBoundary());
        assertFalse(actualAstAssignmentExpression.isCompileTimeConstant());
        assertFalse(actualAstAssignmentExpression.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstAssignmentExpression.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstAssignmentExpression.children().toList().isEmpty());
        assertTrue(actualAstAssignmentExpression.isExpression());
        assertSame(actualAstAssignmentExpression, nextResult.getParent());
        assertSame(ancestorsResult, actualAstAssignmentExpression.descendants());
    }

    /**
     * Method under test: {@link ASTAssignmentExpression#setOp(AssignmentOp)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSetOp() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ASTAssignmentExpression.operator
        //     AbstractJavaExpr.constValue
        //     AbstractJavaExpr.parenDepth
        //     AbstractJavaTypeNode.typeMirror
        //     AbstractJavaNode.root
        //     AbstractJavaNode.symbolTable
        //     AbstractJjtreeNode.firstToken
        //     AbstractJjtreeNode.id
        //     AbstractJjtreeNode.image
        //     AbstractJjtreeNode.lastToken
        //     AbstractNode.childIndex
        //     AbstractNode.children
        //     AbstractNode.parent
        //     AbstractNode.userData

        // Arrange
        // TODO: Populate arranged inputs
        ASTAssignmentExpression astAssignmentExpression = null;
        AssignmentOp op = AssignmentOp.ASSIGN;

        // Act
        astAssignmentExpression.setOp(op);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTAssignmentExpression#getLeftOperand()}
     */
    @Test
    void testGetLeftOperand() {
        // Arrange
        ASTAssignmentExpression astAssignmentExpression = new ASTAssignmentExpression(1);
        astAssignmentExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAssignmentExpression.getLeftOperand());
    }

    /**
     * Method under test: {@link ASTAssignmentExpression#getLeftOperand()}
     */
    @Test
    void testGetLeftOperand2() {
        // Arrange
        ASTAssignmentExpression astAssignmentExpression = new ASTAssignmentExpression(1);
        astAssignmentExpression.setSymbolTable(mock(JSymbolTable.class));
        astAssignmentExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAssignmentExpression.getLeftOperand());
    }

    /**
     * Method under test: {@link ASTAssignmentExpression#isCompound()}
     */
    @Test
    void testIsCompound() {
        // Arrange
        ASTAssignmentExpression astAssignmentExpression = new ASTAssignmentExpression(1);
        astAssignmentExpression.setOp(AssignmentOp.ASSIGN);

        // Act and Assert
        assertFalse(astAssignmentExpression.isCompound());
    }

    /**
     * Method under test: {@link ASTAssignmentExpression#isCompound()}
     */
    @Test
    void testIsCompound2() {
        // Arrange
        ASTAssignmentExpression astAssignmentExpression = new ASTAssignmentExpression(1);
        astAssignmentExpression.setOp(AssignmentOp.AND_ASSIGN);

        // Act and Assert
        assertTrue(astAssignmentExpression.isCompound());
    }

    /**
     * Method under test: {@link ASTAssignmentExpression#getOperator()}
     */
    @Test
    void testGetOperator() {
        // Arrange, Act and Assert
        assertNull((new ASTAssignmentExpression(1)).getOperator());
    }

    /**
     * Method under test: {@link ASTAssignmentExpression#getOperator()}
     */
    @Test
    void testGetOperator2() {
        // Arrange
        ASTAssignmentExpression astAssignmentExpression = new ASTAssignmentExpression(1);
        astAssignmentExpression.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astAssignmentExpression.getOperator());
    }

    /**
     * Method under test:
     * {@link ASTAssignmentExpression#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTAssignmentExpression astAssignmentExpression = new ASTAssignmentExpression(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTAssignmentExpression>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astAssignmentExpression.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTAssignmentExpression.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }
}
