package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNull;
import net.sourceforge.pmd.lang.ast.AstInfo;
import org.junit.jupiter.api.Test;

class ASTCompilationUnitDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTCompilationUnit#getAstInfo()}
   *   <li>{@link ASTCompilationUnit#getComments()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange
    ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);

    // Act
    AstInfo<ASTCompilationUnit> actualAstInfo = astCompilationUnit.getAstInfo();

    // Assert
    assertNull(astCompilationUnit.getComments());
    assertNull(actualAstInfo);
  }
}
