package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ASTExpressionDiffblueTest {
    /**
     * Method under test: {@link ASTExpression#isExpression()}
     */
    @Test
    void testIsExpression() {
        // Arrange, Act and Assert
        assertTrue((new ASTAmbiguousName(1)).isExpression());
    }

    /**
     * Method under test: {@link ASTExpression#isExpression()}
     */
    @Test
    void testIsExpression2() {
        // Arrange
        ASTAmbiguousName astAmbiguousName = new ASTAmbiguousName(1);
        astAmbiguousName.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertTrue(astAmbiguousName.isExpression());
    }

    /**
     * Method under test: {@link ASTExpression#isParenthesized()}
     */
    @Test
    void testIsParenthesized() {
        // Arrange, Act and Assert
        assertFalse((new ASTAmbiguousName(1)).isParenthesized());
    }

    /**
     * Method under test: {@link ASTExpression#isParenthesized()}
     */
    @Test
    void testIsParenthesized2() {
        // Arrange
        ASTAmbiguousName astAmbiguousName = new ASTAmbiguousName(1);
        astAmbiguousName.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertFalse(astAmbiguousName.isParenthesized());
    }

    /**
     * Method under test: {@link ASTExpression#getConstValue()}
     */
    @Test
    void testGetConstValue() {
        // Arrange, Act and Assert
        assertNull((new ASTAmbiguousName(1)).getConstValue());
    }

    /**
     * Method under test: {@link ASTExpression#getConstValue()}
     */
    @Test
    void testGetConstValue2() {
        // Arrange
        ASTAmbiguousName astAmbiguousName = new ASTAmbiguousName(1);
        astAmbiguousName.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astAmbiguousName.getConstValue());
    }

    /**
     * Method under test: {@link ASTExpression#isCompileTimeConstant()}
     */
    @Test
    void testIsCompileTimeConstant() {
        // Arrange, Act and Assert
        assertFalse((new ASTAmbiguousName(1)).isCompileTimeConstant());
    }

    /**
     * Method under test: {@link ASTExpression#isCompileTimeConstant()}
     */
    @Test
    void testIsCompileTimeConstant2() {
        // Arrange
        ASTAmbiguousName astAmbiguousName = new ASTAmbiguousName(1);
        astAmbiguousName.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertFalse(astAmbiguousName.isCompileTimeConstant());
    }

    /**
     * Method under test: {@link ASTExpression#getConversionContext()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetConversionContext() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTExpression astExpression = null;

        // Act
        ExprContext actualConversionContext = astExpression.getConversionContext();

        // Assert
        // TODO: Add assertions on result
    }
}
