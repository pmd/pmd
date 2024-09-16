package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTArrayDimensionsDiffblueTest {
  /**
   * Method under test: {@link ASTArrayDimensions#ASTArrayDimensions(int)}
   */
  @Test
  void testNewASTArrayDimensions() {
    // Arrange, Act and Assert
    assertTrue((new ASTArrayDimensions(1)).toList().isEmpty());
  }
}
