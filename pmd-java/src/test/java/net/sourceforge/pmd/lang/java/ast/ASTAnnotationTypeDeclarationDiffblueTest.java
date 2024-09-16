package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class ASTAnnotationTypeDeclarationDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTAnnotationTypeDeclaration#ASTAnnotationTypeDeclaration(int)}
   *   <li>{@link ASTAnnotationTypeDeclaration#isInterface()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange and Act
    ASTAnnotationTypeDeclaration actualAstAnnotationTypeDeclaration = new ASTAnnotationTypeDeclaration(1);
    boolean actualIsInterfaceResult = actualAstAnnotationTypeDeclaration.isInterface();

    // Assert
    assertNull(actualAstAnnotationTypeDeclaration.getImage());
    assertNull(actualAstAnnotationTypeDeclaration.getFirstToken());
    assertNull(actualAstAnnotationTypeDeclaration.getLastToken());
    assertNull(actualAstAnnotationTypeDeclaration.getTypeMirrorInternal());
    assertEquals(0, actualAstAnnotationTypeDeclaration.getIndexInParent());
    assertSame(actualAstAnnotationTypeDeclaration.getXPathAttributesIterator().next().getValue(),
        actualIsInterfaceResult);
  }
}
