package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTAnnotationMemberListDiffblueTest {
  /**
   * Method under test:
   * {@link ASTAnnotationMemberList#ASTAnnotationMemberList(int)}
   */
  @Test
  void testNewASTAnnotationMemberList() {
    // Arrange, Act and Assert
    assertTrue((new ASTAnnotationMemberList(1)).toList().isEmpty());
  }
}
