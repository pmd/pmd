package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTImplementsListDiffblueTest {
  /**
   * Method under test: {@link ASTImplementsList#ASTImplementsList(int)}
   */
  @Test
  void testNewASTImplementsList() {
    // Arrange, Act and Assert
    assertTrue((new ASTImplementsList(1)).toList().isEmpty());
  }
}
