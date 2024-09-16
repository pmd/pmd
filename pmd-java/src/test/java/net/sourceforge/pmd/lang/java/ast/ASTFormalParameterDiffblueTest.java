package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ASTFormalParameterDiffblueTest {
  /**
   * Method under test: {@link ASTFormalParameter#getVisibility()}
   */
  @Test
  void testGetVisibility() {
    // Arrange, Act and Assert
    assertEquals(ModifierOwner.Visibility.V_LOCAL, (new ASTFormalParameter(1)).getVisibility());
  }
}
