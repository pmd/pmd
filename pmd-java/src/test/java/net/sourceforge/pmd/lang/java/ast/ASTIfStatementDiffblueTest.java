package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTIfStatementDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTIfStatement#setHasElse()}
   *   <li>{@link ASTIfStatement#hasElse()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange
    ASTIfStatement astIfStatement = new ASTIfStatement(1);

    // Act
    astIfStatement.setHasElse();

    // Assert that nothing has changed
    assertTrue(astIfStatement.hasElse());
  }
}
