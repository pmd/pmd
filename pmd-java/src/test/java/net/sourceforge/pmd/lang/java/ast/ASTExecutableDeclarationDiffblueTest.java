package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
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

    /**
     * Method under test: {@link ASTExecutableDeclaration#isAbstract()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsAbstract() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTModifierList.hasAll(net.sourceforge.pmd.lang.java.ast.JModifier, net.sourceforge.pmd.lang.java.ast.JModifier[])" because the return value of "net.sourceforge.pmd.lang.java.ast.ModifierOwner.getModifiers()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ModifierOwner.hasModifiers(ModifierOwner.java:97)
        //       at net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration.isAbstract(ASTExecutableDeclaration.java:67)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTExecutableDeclaration astExecutableDeclaration = null;

        // Act
        boolean actualIsAbstractResult = astExecutableDeclaration.isAbstract();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#getFormalParameters()}
     */
    @Test
    void testGetFormalParameters() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorDeclaration(1)).getFormalParameters());
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#getFormalParameters()}
     */
    @Test
    void testGetFormalParameters2() {
        // Arrange
        ASTConstructorDeclaration astConstructorDeclaration = new ASTConstructorDeclaration(1);
        astConstructorDeclaration.setSymbol(mock(JConstructorSymbol.class));

        // Act and Assert
        assertNull(astConstructorDeclaration.getFormalParameters());
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#getArity()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetArity() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTFormalParameters.size()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration.getFormalParameters()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration.getArity(ASTExecutableDeclaration.java:83)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTExecutableDeclaration astExecutableDeclaration = null;

        // Act
        int actualArity = astExecutableDeclaration.getArity();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange, Act and Assert
        assertNull((new ASTMethodDeclaration(1)).getBody());
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#getBody()}
     */
    @Test
    void testGetBody2() {
        // Arrange
        ASTMethodDeclaration astMethodDeclaration = new ASTMethodDeclaration(1);
        astMethodDeclaration.setOverriddenMethod(mock(JMethodSig.class));

        // Act and Assert
        assertNull(astMethodDeclaration.getBody());
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#getThrowsList()}
     */
    @Test
    void testGetThrowsList() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorDeclaration(1)).getThrowsList());
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#getThrowsList()}
     */
    @Test
    void testGetThrowsList2() {
        // Arrange
        ASTConstructorDeclaration astConstructorDeclaration = new ASTConstructorDeclaration(1);
        astConstructorDeclaration.setSymbol(mock(JConstructorSymbol.class));

        // Act and Assert
        assertNull(astConstructorDeclaration.getThrowsList());
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#isVarargs()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsVarargs() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTFormalParameters.getLastChild()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration.getFormalParameters()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration.isVarargs(ASTExecutableDeclaration.java:110)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTExecutableDeclaration astExecutableDeclaration = null;

        // Act
        boolean actualIsVarargsResult = astExecutableDeclaration.isVarargs();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#isStatic()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsStatic() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTModifierList.hasAll(net.sourceforge.pmd.lang.java.ast.JModifier, net.sourceforge.pmd.lang.java.ast.JModifier[])" because the return value of "net.sourceforge.pmd.lang.java.ast.ModifierOwner.getModifiers()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ModifierOwner.hasModifiers(ModifierOwner.java:97)
        //       at net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration.isStatic(ASTExecutableDeclaration.java:122)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTExecutableDeclaration astExecutableDeclaration = null;

        // Act
        boolean actualIsStaticResult = astExecutableDeclaration.isStatic();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTExecutableDeclaration#isFinal()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIsFinal() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTModifierList.hasAll(net.sourceforge.pmd.lang.java.ast.JModifier, net.sourceforge.pmd.lang.java.ast.JModifier[])" because the return value of "net.sourceforge.pmd.lang.java.ast.ModifierOwner.getModifiers()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ModifierOwner.hasModifiers(ModifierOwner.java:97)
        //       at net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration.isFinal(ASTExecutableDeclaration.java:133)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTExecutableDeclaration astExecutableDeclaration = null;

        // Act
        boolean actualIsFinalResult = astExecutableDeclaration.isFinal();

        // Assert
        // TODO: Add assertions on result
    }
}
