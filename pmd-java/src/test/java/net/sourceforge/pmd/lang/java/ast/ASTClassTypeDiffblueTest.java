package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import org.junit.jupiter.api.Test;

class ASTClassTypeDiffblueTest {
  /**
   * Methods under test:
   * <ul>
   *   <li>{@link ASTClassType#ASTClassType(int)}
   *   <li>{@link ASTClassType#setImplicitEnclosing(JClassType)}
   *   <li>{@link ASTClassType#setSymbol(JTypeDeclSymbol)}
   *   <li>{@link ASTClassType#setFullyQualified()}
   *   <li>{@link ASTClassType#getImplicitEnclosing()}
   *   <li>{@link ASTClassType#getReferencedSym()}
   *   <li>{@link ASTClassType#getSimpleName()}
   *   <li>{@link ASTClassType#isFullyQualified()}
   * </ul>
   */
  @Test
  void testGettersAndSetters() {
    // Arrange and Act
    ASTClassType actualAstClassType = new ASTClassType(1);
    JClassType enclosing = mock(JClassType.class);
    actualAstClassType.setImplicitEnclosing(enclosing);
    JTypeDeclSymbol symbol = mock(JTypeDeclSymbol.class);
    actualAstClassType.setSymbol(symbol);
    actualAstClassType.setFullyQualified();
    JClassType actualImplicitEnclosing = actualAstClassType.getImplicitEnclosing();
    JTypeDeclSymbol actualReferencedSym = actualAstClassType.getReferencedSym();
    actualAstClassType.getSimpleName();
    boolean actualIsFullyQualifiedResult = actualAstClassType.isFullyQualified();

    // Assert that nothing has changed
    assertEquals(0, actualAstClassType.getIndexInParent());
    assertTrue(actualIsFullyQualifiedResult);
    assertSame(symbol, actualReferencedSym);
    assertSame(enclosing, actualImplicitEnclosing);
  }
}
