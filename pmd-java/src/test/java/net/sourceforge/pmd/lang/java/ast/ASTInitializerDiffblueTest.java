package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTInitializerDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTInitializer#setStatic()}
   *   <li>{@link ASTInitializer#isStatic()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange
    ASTInitializer astInitializer = new ASTInitializer(1);

    // Act
    astInitializer.setStatic();

    // Assert that nothing has changed
    assertTrue(astInitializer.isStatic());
  }
}
