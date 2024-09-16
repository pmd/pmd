package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ASTEnumDeclarationDiffblueTest {
  /**
   * Method under test: {@link ASTEnumDeclaration#ASTEnumDeclaration(int)}
   */
  @Test
  void testNewASTEnumDeclaration() {
    // Arrange and Act
    ASTEnumDeclaration actualAstEnumDeclaration = new ASTEnumDeclaration(1);

    // Assert
    assertNull(actualAstEnumDeclaration.getImage());
    assertNull(actualAstEnumDeclaration.getFirstToken());
    assertNull(actualAstEnumDeclaration.getLastToken());
    assertNull(actualAstEnumDeclaration.getTypeMirrorInternal());
    assertEquals(0, actualAstEnumDeclaration.getIndexInParent());
  }
}
