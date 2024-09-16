package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ASTExpressionDiffblueTest {
  /**
   * Method under test: {@link ASTExpression#getConstValue()}
   */
  @Test
  void testGetConstValue() {
    // Arrange, Act and Assert
    assertNull((new ASTAmbiguousName(1)).getConstValue());
  }
}
