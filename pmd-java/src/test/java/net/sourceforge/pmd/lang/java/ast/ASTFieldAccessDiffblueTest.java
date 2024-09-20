package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTFieldAccessDiffblueTest {
    /**
     * Method under test: {@link ASTFieldAccess#getQualifier()}
     */
    @Test
    void testGetQualifier() {
        // Arrange
        ASTAmbiguousName lhs = new ASTAmbiguousName(1);

        // Act and Assert
        assertSame(lhs, (new ASTFieldAccess(lhs, "Field Name")).getQualifier());
    }

    /**
     * Method under test: {@link ASTFieldAccess#getName()}
     */
    @Test
    void testGetName() {
        // Arrange, Act and Assert
        assertNull((new ASTFieldAccess(1)).getName());
    }

    /**
     * Method under test: {@link ASTFieldAccess#getSignature()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetSignature() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTFieldAccess astFieldAccess = null;

        // Act
        JVariableSig.FieldSig actualSignature = astFieldAccess.getSignature();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFieldAccess#getReferencedSym()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetReferencedSym() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTFieldAccess astFieldAccess = null;

        // Act
        JFieldSymbol actualReferencedSym = astFieldAccess.getReferencedSym();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFieldAccess#ASTFieldAccess(int)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewASTFieldAccess() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        int id = 0;

        // Act
        ASTFieldAccess actualAstFieldAccess = new ASTFieldAccess(id);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTFieldAccess#ASTFieldAccess(ASTAmbiguousName, String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewASTFieldAccess2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTAmbiguousName lhs = null;
        String fieldName = "";

        // Act
        ASTFieldAccess actualAstFieldAccess = new ASTFieldAccess(lhs, fieldName);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTFieldAccess#ASTFieldAccess(ASTExpression, JavaccToken)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewASTFieldAccess3() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument$TokenDocumentBehavior.describeKind(int)" because "this.behavior" is null
        //       at net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.describeKind(JavaccTokenDocument.java:170)
        //       at net.sourceforge.pmd.lang.java.ast.TokenUtils.expectKind(TokenUtils.java:89)
        //       at net.sourceforge.pmd.lang.java.ast.ASTFieldAccess.<init>(ASTFieldAccess.java:45)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTExpression lhs = null;
        JavaccToken identifier = null;

        // Act
        ASTFieldAccess actualAstFieldAccess = new ASTFieldAccess(lhs, identifier);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFieldAccess#setTypedSym(JVariableSig.FieldSig)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSetTypedSym() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ASTFieldAccess.typedSym
        //     AbstractJavaExpr.constValue
        //     AbstractJavaExpr.parenDepth
        //     AbstractJavaTypeNode.typeMirror
        //     AbstractJavaNode.root
        //     AbstractJavaNode.symbolTable
        //     AbstractJjtreeNode.firstToken
        //     AbstractJjtreeNode.id
        //     AbstractJjtreeNode.image
        //     AbstractJjtreeNode.lastToken
        //     AbstractNode.childIndex
        //     AbstractNode.children
        //     AbstractNode.parent
        //     AbstractNode.userData

        // Arrange
        // TODO: Populate arranged inputs
        ASTFieldAccess astFieldAccess = null;
        JVariableSig.FieldSig sig = null;

        // Act
        astFieldAccess.setTypedSym(sig);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTFieldAccess#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTFieldAccess astFieldAccess = new ASTFieldAccess(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTFieldAccess>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astFieldAccess.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTFieldAccess.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }
}
