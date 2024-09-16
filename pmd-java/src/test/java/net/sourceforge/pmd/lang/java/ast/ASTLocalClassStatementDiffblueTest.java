package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTLocalClassStatementDiffblueTest {
  /**
   * Method under test: {@link ASTLocalClassStatement#isFindBoundary()}
   */
  @Test
  void testIsFindBoundary() {
    // Arrange, Act and Assert
    assertTrue((new ASTLocalClassStatement(1)).isFindBoundary());
  }
}
