package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTExtendsListDiffblueTest {
  /**
   * Method under test: {@link ASTExtendsList#ASTExtendsList(int)}
   */
  @Test
  void testNewASTExtendsList() {
    // Arrange, Act and Assert
    assertTrue((new ASTExtendsList(1)).toList().isEmpty());
  }
}
