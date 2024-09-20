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

class ASTCastExpressionDiffblueTest {
    /**
     * Method under test: {@link ASTCastExpression#getCastType()}
     */
    @Test
    void testGetCastType() {
        // Arrange, Act and Assert
        assertNull((new ASTCastExpression(1)).getCastType());
    }

    /**
     * Method under test: {@link ASTCastExpression#getCastType()}
     */
    @Test
    void testGetCastType2() {
        // Arrange
        ASTCastExpression astCastExpression = new ASTCastExpression(1);
        astCastExpression.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astCastExpression.getCastType());
    }

    /**
     * Method under test: {@link ASTCastExpression#getOperand()}
     */
    @Test
    void testGetOperand() {
        // Arrange
        ASTCastExpression astCastExpression = new ASTCastExpression(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astCastExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astCastExpression.getOperand());
    }

    /**
     * Method under test: {@link ASTCastExpression#getOperand()}
     */
    @Test
    void testGetOperand2() {
        // Arrange
        ASTCastExpression astCastExpression = new ASTCastExpression(1);
        astCastExpression.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astCastExpression.addChild(child, 1);

        // Act and Assert
        assertSame(child, astCastExpression.getOperand());
    }

    /**
     * Method under test:
     * {@link ASTCastExpression#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTCastExpression astCastExpression = new ASTCastExpression(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTCastExpression>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astCastExpression.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTCastExpression.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTCastExpression#ASTCastExpression(int)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewASTCastExpression() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        int id = 0;

        // Act
        ASTCastExpression actualAstCastExpression = new ASTCastExpression(id);

        // Assert
        // TODO: Add assertions on result
    }
}
