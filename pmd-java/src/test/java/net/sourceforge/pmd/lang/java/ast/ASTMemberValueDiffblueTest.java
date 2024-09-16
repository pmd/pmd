package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import org.junit.jupiter.api.Test;

class ASTMemberValueDiffblueTest {
  /**
   * Method under test: {@link ASTMemberValue#getConstValue()}
   */
  @Test
  void testGetConstValue() {
    // Arrange, Act and Assert
    assertNull((new ASTAnnotation(1)).getConstValue());
  }

  /**
   * Method under test: {@link ASTMemberValue#getConstValue()}
   */
  @Test
  void testGetConstValue2() {
    // Arrange
    ASTAnnotation astAnnotation = new ASTAnnotation(1);
    astAnnotation.setSymbolTable(mock(JSymbolTable.class));

    // Act and Assert
    assertNull(astAnnotation.getConstValue());
  }
}
