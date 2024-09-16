package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTLambdaParameterListDiffblueTest {
  /**
   * Method under test: {@link ASTLambdaParameterList#ASTLambdaParameterList(int)}
   */
  @Test
  void testNewASTLambdaParameterList() {
    // Arrange, Act and Assert
    assertTrue((new ASTLambdaParameterList(1)).toList().isEmpty());
  }
}
