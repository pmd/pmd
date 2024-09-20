package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTEnumConstantDiffblueTest {
    /**
     * Method under test: {@link ASTEnumConstant#getReportLocation()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetReportLocation() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTVariableId.getFirstToken()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTEnumConstant.getVarId()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTEnumConstant.getReportLocation(ASTEnumConstant.java:37)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTEnumConstant astEnumConstant = null;

        // Act
        FileLocation actualReportLocation = astEnumConstant.getReportLocation();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTEnumConstant#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTEnumConstant>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astEnumConstant.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTEnumConstant.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTEnumConstant#getVarId()}
     */
    @Test
    void testGetVarId() {
        // Arrange, Act and Assert
        assertNull((new ASTEnumConstant(1)).getVarId());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getVarId()}
     */
    @Test
    void testGetVarId2() {
        // Arrange
        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        astEnumConstant.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astEnumConstant.getVarId());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getImage()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetImage() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTVariableId.getName()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTEnumConstant.getVarId()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTEnumConstant.getName(ASTEnumConstant.java:66)
        //       at net.sourceforge.pmd.lang.java.ast.ASTEnumConstant.getImage(ASTEnumConstant.java:53)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTEnumConstant astEnumConstant = null;

        // Act
        String actualImage = astEnumConstant.getImage();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTEnumConstant#getArguments()}
     */
    @Test
    void testGetArguments() {
        // Arrange, Act and Assert
        assertNull((new ASTEnumConstant(1)).getArguments());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getArguments()}
     */
    @Test
    void testGetArguments2() {
        // Arrange
        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        astEnumConstant.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astEnumConstant.getArguments());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getName()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTVariableId.getName()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTEnumConstant.getVarId()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTEnumConstant.getName(ASTEnumConstant.java:66)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTEnumConstant astEnumConstant = null;

        // Act
        String actualName = astEnumConstant.getName();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTEnumConstant#isAnonymousClass()}
     */
    @Test
    void testIsAnonymousClass() {
        // Arrange, Act and Assert
        assertFalse((new ASTEnumConstant(1)).isAnonymousClass());
    }

    /**
     * Method under test: {@link ASTEnumConstant#isAnonymousClass()}
     */
    @Test
    void testIsAnonymousClass2() {
        // Arrange
        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        astEnumConstant.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertFalse(astEnumConstant.isAnonymousClass());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getAnonymousClass()}
     */
    @Test
    void testGetAnonymousClass() {
        // Arrange, Act and Assert
        assertNull((new ASTEnumConstant(1)).getAnonymousClass());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getAnonymousClass()}
     */
    @Test
    void testGetAnonymousClass2() {
        // Arrange
        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        astEnumConstant.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astEnumConstant.getAnonymousClass());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getAnonymousClass()}
     */
    @Test
    void testGetAnonymousClass3() {
        // Arrange
        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        astEnumConstant.setSymbolTable(mock(JSymbolTable.class));
        astEnumConstant.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astEnumConstant.getAnonymousClass());
    }

    /**
     * Method under test: {@link ASTEnumConstant#getAnonymousClass()}
     */
    @Test
    void testGetAnonymousClass4() {
        // Arrange
        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        ASTAnonymousClassDeclaration child = new ASTAnonymousClassDeclaration(1);
        astEnumConstant.addChild(child, 1);

        // Act and Assert
        assertSame(child, astEnumConstant.getAnonymousClass());
    }

    /**
     * Method under test:
     * {@link ASTEnumConstant#setOverload(OverloadSelectionResult)}
     */
    @Test
    void testSetOverload() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken token = new JavaccToken(1, Chars.EMPTY, 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        ASTEnumConstant astEnumConstant = new ASTEnumConstant(1);
        astEnumConstant.setLastToken(token);
        astEnumConstant.addChild(new ASTAmbiguousName(1), 1);

        // Act
        astEnumConstant.setOverload(mock(ExprMirror.InvocationMirror.MethodCtDecl.class));

        // Assert
        verify(textDocument).getLength();
    }

    /**
     * Method under test: {@link ASTEnumConstant#getOverloadSelectionInfo()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetOverloadSelectionInfo() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTEnumConstant astEnumConstant = null;

        // Act
        OverloadSelectionResult actualOverloadSelectionInfo = astEnumConstant.getOverloadSelectionInfo();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTEnumConstant#ASTEnumConstant(int)}
     *   <li>{@link ASTEnumConstant#getExplicitTypeArguments()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        ASTEnumConstant actualAstEnumConstant = new ASTEnumConstant(1);
        ASTTypeArguments actualExplicitTypeArguments = actualAstEnumConstant.getExplicitTypeArguments();

        // Assert
        assertNull(actualAstEnumConstant.getFirstToken());
        assertNull(actualAstEnumConstant.getLastToken());
        assertNull(actualExplicitTypeArguments);
        assertNull(actualAstEnumConstant.getTypeMirrorInternal());
        assertEquals(0, actualAstEnumConstant.getIndexInParent());
    }
}
