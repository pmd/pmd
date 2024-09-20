package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTConditionalExpressionDiffblueTest {
    /**
     * Method under test: {@link ASTConditionalExpression#getCondition()}
     */
    @Test
    void testGetCondition() {
        // Arrange
        ASTConditionalExpression astConditionalExpression = new ASTConditionalExpression(1);
        astConditionalExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astConditionalExpression.getCondition());
    }

    /**
     * Method under test: {@link ASTConditionalExpression#getCondition()}
     */
    @Test
    void testGetCondition2() {
        // Arrange
        ASTConditionalExpression astConditionalExpression = new ASTConditionalExpression(1);
        astConditionalExpression.setSymbolTable(mock(JSymbolTable.class));
        astConditionalExpression.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astConditionalExpression.getCondition());
    }

    /**
     * Method under test: {@link ASTConditionalExpression#getThenBranch()}
     */
    @Test
    void testGetThenBranch() {
        // Arrange
        ASTConditionalExpression astConditionalExpression = new ASTConditionalExpression(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astConditionalExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astConditionalExpression.getThenBranch());
    }

    /**
     * Method under test: {@link ASTConditionalExpression#getThenBranch()}
     */
    @Test
    void testGetThenBranch2() {
        // Arrange
        ASTConditionalExpression astConditionalExpression = new ASTConditionalExpression(1);
        astConditionalExpression.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astConditionalExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astConditionalExpression.getThenBranch());
    }

    /**
     * Method under test: {@link ASTConditionalExpression#getElseBranch()}
     */
    @Test
    void testGetElseBranch() {
        // Arrange
        ASTConditionalExpression astConditionalExpression = new ASTConditionalExpression(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astConditionalExpression.addChild(child, 2);

        // Act and Assert
        assertSame(child, astConditionalExpression.getElseBranch());
    }

    /**
     * Method under test:
     * {@link ASTConditionalExpression#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTConditionalExpression astConditionalExpression = new ASTConditionalExpression(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTConditionalExpression>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astConditionalExpression.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTConditionalExpression.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTConditionalExpression#isStandalone()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsStandalone() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTConditionalExpression astConditionalExpression = null;

        // Act
        boolean actualIsStandaloneResult = astConditionalExpression.isStandalone();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTConditionalExpression#ASTConditionalExpression(int)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewASTConditionalExpression() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        int id = 0;

        // Act
        ASTConditionalExpression actualAstConditionalExpression = new ASTConditionalExpression(id);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTConditionalExpression#setStandaloneTernary()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSetStandaloneTernary() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ASTConditionalExpression.isStandalone
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
        ASTConditionalExpression astConditionalExpression = null;

        // Act
        astConditionalExpression.setStandaloneTernary();

        // Assert
        // TODO: Add assertions on result
    }
}
