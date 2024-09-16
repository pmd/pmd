package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTAnnotationTypeBodyDiffblueTest {
  /**
   * Method under test: {@link ASTAnnotationTypeBody#ASTAnnotationTypeBody(int)}
   */
  @Test
  void testNewASTAnnotationTypeBody() {
    // Arrange, Act and Assert
    assertTrue((new ASTAnnotationTypeBody(1)).toList().isEmpty());
  }
}
