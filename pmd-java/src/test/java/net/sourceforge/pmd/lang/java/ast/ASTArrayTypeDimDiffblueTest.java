package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTArrayTypeDimDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTArrayTypeDim#setVarargs()}
   *   <li>{@link ASTArrayTypeDim#isVarargs()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange
    ASTArrayTypeDim astArrayTypeDim = new ASTArrayTypeDim(1);

    // Act
    astArrayTypeDim.setVarargs();

    // Assert that nothing has changed
    assertTrue(astArrayTypeDim.isVarargs());
  }
}
