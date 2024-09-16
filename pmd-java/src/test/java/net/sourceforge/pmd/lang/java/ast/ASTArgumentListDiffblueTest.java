package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTArgumentListDiffblueTest {
  /**
   * Method under test: {@link ASTArgumentList#ASTArgumentList(int)}
   */
  @Test
  void testNewASTArgumentList() {
    // Arrange, Act and Assert
    assertTrue((new ASTArgumentList(1)).toList().isEmpty());
  }
}
