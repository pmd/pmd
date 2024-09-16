package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ASTAnnotationDiffblueTest {
  /**
   * Method under test: {@link ASTAnnotation#ASTAnnotation(int)}
   */
  @Test
  void testNewASTAnnotation() {
    // Arrange and Act
    ASTAnnotation actualAstAnnotation = new ASTAnnotation(1);

    // Assert
    assertNull(actualAstAnnotation.getImage());
    assertNull(actualAstAnnotation.getFirstToken());
    assertNull(actualAstAnnotation.getLastToken());
    assertNull(actualAstAnnotation.getTypeMirrorInternal());
    assertEquals(0, actualAstAnnotation.getIndexInParent());
  }
}
