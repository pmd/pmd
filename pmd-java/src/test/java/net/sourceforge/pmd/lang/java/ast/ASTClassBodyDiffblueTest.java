package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTClassBodyDiffblueTest {
  /**
   * Method under test: {@link ASTClassBody#ASTClassBody(int)}
   */
  @Test
  void testNewASTClassBody() {
    // Arrange, Act and Assert
    assertTrue((new ASTClassBody(1)).toList().isEmpty());
  }
}
