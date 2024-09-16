package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ASTLocalVariableDeclarationDiffblueTest {
  /**
   * Method under test: {@link ASTLocalVariableDeclaration#getVisibility()}
   */
  @Test
  void testGetVisibility() {
    // Arrange, Act and Assert
    assertEquals(ModifierOwner.Visibility.V_LOCAL, (new ASTLocalVariableDeclaration(1)).getVisibility());
  }
}
