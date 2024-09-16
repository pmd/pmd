package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ASTExecutableDeclarationDiffblueTest {
  /**
   * Method under test: {@link ASTExecutableDeclaration#getSymbol()}
   */
  @Test
  @Disabled("TODO: Complete this test")
  void testGetSymbol() {
    // TODO: Diffblue Cover was only able to create a partial test for this method:
    //   Reason: No inputs found that don't throw a trivial exception.
    //   Diffblue Cover tried to run the arrange/act section, but the method under
    //   test threw
    //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTModifierList.getLastToken()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration.getModifiers()" is null
    //       at net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration.getReportLocation(ASTConstructorDeclaration.java:40)
    //       at net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode.toString(AbstractJjtreeNode.java:143)
    //       at java.base/java.lang.String.valueOf(String.java:4507)
    //       at java.base/java.lang.StringBuilder.append(StringBuilder.java:173)
    //       at net.sourceforge.pmd.lang.java.ast.AbstractTypedSymbolDeclarator.assertSymbolNotNull(AbstractTypedSymbolDeclarator.java:33)
    //       at net.sourceforge.pmd.lang.java.ast.AbstractExecutableDeclaration.getSymbol(AbstractExecutableDeclaration.java:31)
    //       at net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration.getSymbol(ASTConstructorDeclaration.java:27)
    //   See https://diff.blue/R013 to resolve this issue.

    // Arrange
    // TODO: Populate arranged inputs
    ASTExecutableDeclaration astExecutableDeclaration = null;

    // Act
    JExecutableSymbol actualSymbol = astExecutableDeclaration.getSymbol();

    // Assert
    // TODO: Add assertions on result
  }
}
