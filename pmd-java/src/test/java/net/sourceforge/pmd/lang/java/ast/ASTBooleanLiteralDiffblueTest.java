package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTBooleanLiteralDiffblueTest {
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
}
