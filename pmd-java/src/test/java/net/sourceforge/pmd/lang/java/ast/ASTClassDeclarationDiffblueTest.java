package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class ASTClassDeclarationDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTClassDeclaration#ASTClassDeclaration(int)}
   *   <li>{@link ASTClassDeclaration#setInterface()}
   *   <li>{@link ASTClassDeclaration#isInterface()}
   *   <li>{@link ASTClassDeclaration#isRegularInterface()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange and Act
    ASTClassDeclaration actualAstClassDeclaration = new ASTClassDeclaration(1);
    actualAstClassDeclaration.setInterface();
    boolean actualIsInterfaceResult = actualAstClassDeclaration.isInterface();
    boolean actualIsRegularInterfaceResult = actualAstClassDeclaration.isRegularInterface();

    // Assert that nothing has changed
    assertEquals(0, actualAstClassDeclaration.getIndexInParent());
    Object value = actualAstClassDeclaration.getXPathAttributesIterator().next().getValue();
    assertSame(value, actualIsInterfaceResult);
    assertSame(value, actualIsRegularInterfaceResult);
  }
}
