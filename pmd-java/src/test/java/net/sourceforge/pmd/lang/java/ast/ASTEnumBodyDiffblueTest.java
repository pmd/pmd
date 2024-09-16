package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTEnumBodyDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTEnumBody#setSeparatorSemi()}
   *   <li>{@link ASTEnumBody#setTrailingComma()}
   *   <li>{@link ASTEnumBody#hasSeparatorSemi()}
   *   <li>{@link ASTEnumBody#hasTrailingComma()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange
    ASTEnumBody astEnumBody = new ASTEnumBody(1);

    // Act
    astEnumBody.setSeparatorSemi();
    astEnumBody.setTrailingComma();
    boolean actualHasSeparatorSemiResult = astEnumBody.hasSeparatorSemi();

    // Assert that nothing has changed
    assertTrue(actualHasSeparatorSemiResult);
    assertTrue(astEnumBody.hasTrailingComma());
  }
}
