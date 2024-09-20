package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTInfixExpressionDiffblueTest {
    /**
     * Method under test:
     * {@link ASTInfixExpression#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTInfixExpression astInfixExpression = new ASTInfixExpression(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTInfixExpression>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astInfixExpression.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTInfixExpression.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test:
     * {@link ASTInfixExpression#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor2() {
        // Arrange
        ASTInfixExpression astInfixExpression = new ASTInfixExpression(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTInfixExpression>any(), Mockito.<Object>any()))
                .thenThrow(new UnsupportedOperationException("foo"));

        // Act and Assert
        assertThrows(UnsupportedOperationException.class, () -> astInfixExpression.acceptVisitor(visitor, "Data"));
        verify(visitor).visit(isA(ASTInfixExpression.class), isA(Object.class));
    }

    /**
     * Method under test: {@link ASTInfixExpression#getRightOperand()}
     */
    @Test
    void testGetRightOperand() {
        // Arrange
        ASTInfixExpression astInfixExpression = new ASTInfixExpression(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astInfixExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astInfixExpression.getRightOperand());
    }

    /**
     * Method under test: {@link ASTInfixExpression#getRightOperand()}
     */
    @Test
    void testGetRightOperand2() {
        // Arrange
        ASTInfixExpression astInfixExpression = new ASTInfixExpression(1);
        astInfixExpression.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astInfixExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astInfixExpression.getRightOperand());
    }

    /**
     * Method under test: {@link ASTInfixExpression#getOperator()}
     */
    @Test
    void testGetOperator() {
        // Arrange, Act and Assert
        assertNull((new ASTInfixExpression(1)).getOperator());
    }

    /**
     * Method under test: {@link ASTInfixExpression#getOperator()}
     */
    @Test
    void testGetOperator2() {
        // Arrange
        ASTInfixExpression astInfixExpression = new ASTInfixExpression(1);
        astInfixExpression.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astInfixExpression.getOperator());
    }

    /**
     * Method under test: {@link ASTInfixExpression#setImage(String)}
     */
    @Test
    void testSetImage() {
        // Arrange, Act and Assert
        assertThrows(UnsupportedOperationException.class, () -> (new ASTInfixExpression(1)).setImage("Image"));
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTInfixExpression#setOp(BinaryOp)}
     *   <li>{@link ASTInfixExpression#getImage()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTInfixExpression astInfixExpression = new ASTInfixExpression(1);

        // Act
        astInfixExpression.setOp(BinaryOp.CONDITIONAL_OR);

        // Assert that nothing has changed
        assertNull(astInfixExpression.getImage());
    }

    /**
     * Method under test: {@link ASTInfixExpression#ASTInfixExpression(int)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewASTInfixExpression() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        int i = 0;

        // Act
        ASTInfixExpression actualAstInfixExpression = new ASTInfixExpression(i);

        // Assert
        // TODO: Add assertions on result
    }
}
