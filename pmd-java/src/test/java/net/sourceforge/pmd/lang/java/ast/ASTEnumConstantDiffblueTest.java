package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ASTEnumConstantDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTEnumConstant#ASTEnumConstant(int)}
   *   <li>{@link ASTEnumConstant#getExplicitTypeArguments()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange and Act
    ASTEnumConstant actualAstEnumConstant = new ASTEnumConstant(1);
    ASTTypeArguments actualExplicitTypeArguments = actualAstEnumConstant.getExplicitTypeArguments();

    // Assert
    assertNull(actualAstEnumConstant.getFirstToken());
    assertNull(actualAstEnumConstant.getLastToken());
    assertNull(actualExplicitTypeArguments);
    assertNull(actualAstEnumConstant.getTypeMirrorInternal());
    assertEquals(0, actualAstEnumConstant.getIndexInParent());
  }
}
