package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTExplicitConstructorInvocationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTExplicitConstructorInvocation#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTExplicitConstructorInvocation>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astExplicitConstructorInvocation.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTExplicitConstructorInvocation.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#getArguments()}
     */
    @Test
    void testGetArguments() {
        // Arrange, Act and Assert
        assertNull((new ASTExplicitConstructorInvocation(1)).getArguments());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#getArguments()}
     */
    @Test
    void testGetArguments2() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken token = new JavaccToken(2, "Image", 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.setLastToken(token);
        ASTArgumentList child = new ASTArgumentList(1);
        astExplicitConstructorInvocation.addChild(child, 1);

        // Act
        ASTArgumentList actualArguments = astExplicitConstructorInvocation.getArguments();

        // Assert
        verify(textDocument).getLength();
        assertTrue(actualArguments.toList().isEmpty());
        assertSame(child, actualArguments);
    }

    /**
     * Method under test:
     * {@link ASTExplicitConstructorInvocation#getArgumentCount()}
     */
    @Test
    void testGetArgumentCount() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken token = new JavaccToken(2, "Image", 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.setLastToken(token);
        astExplicitConstructorInvocation.addChild(new ASTArgumentList(1), 1);

        // Act
        int actualArgumentCount = astExplicitConstructorInvocation.getArgumentCount();

        // Assert
        verify(textDocument).getLength();
        assertEquals(0, actualArgumentCount);
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#isThis()}
     */
    @Test
    void testIsThis() {
        // Arrange, Act and Assert
        assertTrue((new ASTExplicitConstructorInvocation(1)).isThis());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#isThis()}
     */
    @Test
    void testIsThis2() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertTrue(astExplicitConstructorInvocation.isThis());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#isQualified()}
     */
    @Test
    void testIsQualified() {
        // Arrange, Act and Assert
        assertFalse((new ASTExplicitConstructorInvocation(1)).isQualified());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#isQualified()}
     */
    @Test
    void testIsQualified2() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertTrue(astExplicitConstructorInvocation.isQualified());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#isQualified()}
     */
    @Test
    void testIsQualified3() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertFalse(astExplicitConstructorInvocation.isQualified());
    }

    /**
     * Method under test:
     * {@link ASTExplicitConstructorInvocation#getExplicitTypeArguments()}
     */
    @Test
    void testGetExplicitTypeArguments() {
        // Arrange, Act and Assert
        assertNull((new ASTExplicitConstructorInvocation(1)).getExplicitTypeArguments());
    }

    /**
     * Method under test:
     * {@link ASTExplicitConstructorInvocation#getExplicitTypeArguments()}
     */
    @Test
    void testGetExplicitTypeArguments2() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astExplicitConstructorInvocation.getExplicitTypeArguments());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#getQualifier()}
     */
    @Test
    void testGetQualifier() {
        // Arrange, Act and Assert
        assertNull((new ASTExplicitConstructorInvocation(1)).getQualifier());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#getQualifier()}
     */
    @Test
    void testGetQualifier2() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astExplicitConstructorInvocation.getQualifier());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#getQualifier()}
     */
    @Test
    void testGetQualifier3() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        astExplicitConstructorInvocation.setSymbolTable(mock(JSymbolTable.class));
        astExplicitConstructorInvocation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astExplicitConstructorInvocation.getQualifier());
    }

    /**
     * Method under test: {@link ASTExplicitConstructorInvocation#getQualifier()}
     */
    @Test
    void testGetQualifier4() {
        // Arrange
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astExplicitConstructorInvocation.addChild(child, 0);

        // Act and Assert
        assertSame(child, astExplicitConstructorInvocation.getQualifier());
    }

    /**
     * Method under test:
     * {@link ASTExplicitConstructorInvocation#getOverloadSelectionInfo()}
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
        ASTExplicitConstructorInvocation astExplicitConstructorInvocation = null;

        // Act
        OverloadSelectionResult actualOverloadSelectionInfo = astExplicitConstructorInvocation.getOverloadSelectionInfo();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>
     * {@link ASTExplicitConstructorInvocation#ASTExplicitConstructorInvocation(int)}
     *   <li>
     * {@link ASTExplicitConstructorInvocation#setOverload(OverloadSelectionResult)}
     *   <li>{@link ASTExplicitConstructorInvocation#setIsSuper()}
     *   <li>{@link ASTExplicitConstructorInvocation#isSuper()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        ASTExplicitConstructorInvocation actualAstExplicitConstructorInvocation = new ASTExplicitConstructorInvocation(1);
        actualAstExplicitConstructorInvocation.setOverload(null);
        actualAstExplicitConstructorInvocation.setIsSuper();
        boolean actualIsSuperResult = actualAstExplicitConstructorInvocation.isSuper();

        // Assert that nothing has changed
        assertEquals(0, actualAstExplicitConstructorInvocation.getIndexInParent());
        assertTrue(actualIsSuperResult);
    }
}
