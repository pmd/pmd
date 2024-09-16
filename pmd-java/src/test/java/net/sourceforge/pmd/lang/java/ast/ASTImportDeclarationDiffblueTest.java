package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ASTImportDeclarationDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTImportDeclaration#setImportOnDemand()}
   *   <li>{@link ASTImportDeclaration#setModuleImport()}
   *   <li>{@link ASTImportDeclaration#setStatic()}
   *   <li>{@link ASTImportDeclaration#getImage()}
   *   <li>{@link ASTImportDeclaration#isImportOnDemand()}
   *   <li>{@link ASTImportDeclaration#isModuleImport()}
   *   <li>{@link ASTImportDeclaration#isStatic()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange
    ASTImportDeclaration astImportDeclaration = new ASTImportDeclaration(1);

    // Act
    astImportDeclaration.setImportOnDemand();
    astImportDeclaration.setModuleImport();
    astImportDeclaration.setStatic();
    astImportDeclaration.getImage();
    boolean actualIsImportOnDemandResult = astImportDeclaration.isImportOnDemand();
    boolean actualIsModuleImportResult = astImportDeclaration.isModuleImport();

    // Assert that nothing has changed
    assertTrue(actualIsImportOnDemandResult);
    assertTrue(actualIsModuleImportResult);
    assertTrue(astImportDeclaration.isStatic());
  }
}
