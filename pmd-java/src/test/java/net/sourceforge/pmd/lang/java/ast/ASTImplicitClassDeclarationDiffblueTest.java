package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class ASTImplicitClassDeclarationDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTImplicitClassDeclaration#ASTImplicitClassDeclaration(int)}
   *   <li>{@link ASTImplicitClassDeclaration#getSimpleName()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange and Act
    ASTImplicitClassDeclaration actualAstImplicitClassDeclaration = new ASTImplicitClassDeclaration(1);
    String actualSimpleName = actualAstImplicitClassDeclaration.getSimpleName();

    // Assert
    assertNull(actualAstImplicitClassDeclaration.getImage());
    assertNull(actualAstImplicitClassDeclaration.getFirstToken());
    assertNull(actualAstImplicitClassDeclaration.getLastToken());
    assertNull(actualAstImplicitClassDeclaration.getTypeMirrorInternal());
    assertEquals(0, actualAstImplicitClassDeclaration.getIndexInParent());
    assertSame(actualAstImplicitClassDeclaration.getXPathAttributesIterator().next().getValue(), actualSimpleName);
  }
}
