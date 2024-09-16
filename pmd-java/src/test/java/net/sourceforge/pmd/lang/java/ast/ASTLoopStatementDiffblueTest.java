package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ASTLoopStatementDiffblueTest {
  /**
   * Method under test: {@link ASTLoopStatement#getBody()}
   */
  @Test
  void testGetBody() {
    // Arrange, Act and Assert
    assertNull((new ASTForStatement(1)).getBody());
  }
}
