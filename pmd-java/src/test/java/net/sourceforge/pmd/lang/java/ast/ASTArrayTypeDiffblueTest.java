package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ASTArrayTypeDiffblueTest {
  /**
   * Method under test: {@link ASTArrayType#ASTArrayType(int)}
   */
  @Test
  void testNewASTArrayType() {
    // Arrange and Act
    ASTArrayType actualAstArrayType = new ASTArrayType(1);

    // Assert
    assertNull(actualAstArrayType.getImage());
    assertNull(actualAstArrayType.getFirstToken());
    assertNull(actualAstArrayType.getLastToken());
    assertNull(actualAstArrayType.getTypeMirrorInternal());
    assertEquals(0, actualAstArrayType.getIndexInParent());
  }
}
