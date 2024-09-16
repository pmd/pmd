package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTMemberValuePairDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTMemberValuePair#setShorthand()}
   *   <li>{@link ASTMemberValuePair#isShorthand()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange
    ASTMemberValuePair astMemberValuePair = new ASTMemberValuePair(1);

    // Act
    astMemberValuePair.setShorthand();

    // Assert that nothing has changed
    assertTrue(astMemberValuePair.isShorthand());
  }
}
