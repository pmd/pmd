package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import org.junit.jupiter.api.Test;

class ASTExplicitConstructorInvocationDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>
   * {@link ASTExplicitConstructorInvocation#ASTExplicitConstructorInvocation(int)}
   *   <li>
   * {@link ASTExplicitConstructorInvocation#setOverload(OverloadSelectionResult)}
   *   <li>{@link ASTExplicitConstructorInvocation#setIsSuper()}
   *   <li>{@link ASTExplicitConstructorInvocation#isSuper()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange and Act
    ASTExplicitConstructorInvocation actualAstExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
    actualAstExplicitConstructorInvocation.setOverload(null);
    actualAstExplicitConstructorInvocation.setIsSuper();
    boolean actualIsSuperResult = actualAstExplicitConstructorInvocation.isSuper();

    // Assert that nothing has changed
    assertEquals(0, actualAstExplicitConstructorInvocation.getIndexInParent());
    assertTrue(actualIsSuperResult);
  }
}
