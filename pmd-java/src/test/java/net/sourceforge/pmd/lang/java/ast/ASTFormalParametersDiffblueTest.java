package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTFormalParametersDiffblueTest {
  /**
   * Method under test: {@link ASTFormalParameters#ASTFormalParameters(int)}
   */
  @Test
  void testNewASTFormalParameters() {
    // Arrange, Act and Assert
    assertTrue((new ASTFormalParameters(1)).toList().isEmpty());
  }
}
