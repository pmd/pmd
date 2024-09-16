package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import org.junit.jupiter.api.Test;

class ASTLambdaExpressionDiffblueTest {
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
}
