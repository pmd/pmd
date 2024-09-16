package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ASTInfixExpressionDiffblueTest {
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
}
