package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

class ASTListDiffblueTest {
  /**
   * Method under test: {@link ASTList#get(int)}
   */
  @Test
  void testGet() {
    // Arrange
    ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
    ASTAmbiguousName child = new ASTAmbiguousName(1);
    astAnnotationMemberList.addChild(child, 1);

    // Act and Assert
    assertSame(child, astAnnotationMemberList.get(1));
  }
}
