package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTBlockDiffblueTest {
  /**
   * Method under test: {@link ASTBlock#ASTBlock(int)}
   */
  @Test
  void testNewASTBlock() {
    // Arrange, Act and Assert
    assertTrue((new ASTBlock(1)).toList().isEmpty());
  }
}
