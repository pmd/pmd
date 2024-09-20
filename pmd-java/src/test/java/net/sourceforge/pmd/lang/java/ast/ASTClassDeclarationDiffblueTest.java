package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTClassDeclarationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTClassDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTClassDeclaration astClassDeclaration = new ASTClassDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTClassDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astClassDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTClassDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTClassDeclaration#isRegularClass()}
     */
    @Test
    void testIsRegularClass() {
        // Arrange, Act and Assert
        assertTrue((new ASTClassDeclaration(1)).isRegularClass());
    }

    /**
     * Method under test: {@link ASTClassDeclaration#isRegularClass()}
     */
    @Test
    void testIsRegularClass2() {
        // Arrange
        ASTClassDeclaration astClassDeclaration = new ASTClassDeclaration(1);
        astClassDeclaration.setSymbol(mock(JClassSymbol.class));

        // Act and Assert
        assertTrue(astClassDeclaration.isRegularClass());
    }

    /**
     * Method under test: {@link ASTClassDeclaration#getSuperClassTypeNode()}
     */
    @Test
    void testGetSuperClassTypeNode() {
        // Arrange, Act and Assert
        assertNull((new ASTClassDeclaration(1)).getSuperClassTypeNode());
    }

    /**
     * Method under test: {@link ASTClassDeclaration#getSuperClassTypeNode()}
     */
    @Test
    void testGetSuperClassTypeNode2() {
        // Arrange
        ASTClassDeclaration astClassDeclaration = new ASTClassDeclaration(1);
        astClassDeclaration.setSymbol(mock(JClassSymbol.class));

        // Act and Assert
        assertNull(astClassDeclaration.getSuperClassTypeNode());
    }

    /**
     * Method under test: {@link ASTClassDeclaration#getPermittedSubclasses()}
     */
    @Test
    void testGetPermittedSubclasses() {
        // Arrange, Act and Assert
        assertTrue((new ASTClassDeclaration(1)).getPermittedSubclasses().isEmpty());
    }

    /**
     * Method under test: {@link ASTClassDeclaration#getPermittedSubclasses()}
     */
    @Test
    void testGetPermittedSubclasses2() {
        // Arrange
        ASTClassDeclaration astClassDeclaration = new ASTClassDeclaration(1);
        astClassDeclaration.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astClassDeclaration.getPermittedSubclasses().isEmpty());
    }

    /**
     * Method under test: {@link ASTClassDeclaration#getPermittedSubclasses()}
     */
    @Test
    void testGetPermittedSubclasses3() {
        // Arrange
        ASTClassDeclaration astClassDeclaration = new ASTClassDeclaration(1);
        astClassDeclaration.setSymbol(mock(JClassSymbol.class));
        astClassDeclaration.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astClassDeclaration.getPermittedSubclasses().isEmpty());
    }

    /**
     * Method under test: {@link ASTClassDeclaration#getPermittedSubclasses()}
     */
    @Test
    void testGetPermittedSubclasses4() {
        // Arrange
        ASTClassDeclaration astClassDeclaration = new ASTClassDeclaration(1);
        astClassDeclaration.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertTrue(astClassDeclaration.getPermittedSubclasses().isEmpty());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTClassDeclaration#ASTClassDeclaration(int)}
     *   <li>{@link ASTClassDeclaration#setInterface()}
     *   <li>{@link ASTClassDeclaration#isInterface()}
     *   <li>{@link ASTClassDeclaration#isRegularInterface()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        ASTClassDeclaration actualAstClassDeclaration = new ASTClassDeclaration(1);
        actualAstClassDeclaration.setInterface();
        boolean actualIsInterfaceResult = actualAstClassDeclaration.isInterface();
        boolean actualIsRegularInterfaceResult = actualAstClassDeclaration.isRegularInterface();

        // Assert that nothing has changed
        assertEquals(0, actualAstClassDeclaration.getIndexInParent());
        Object value = actualAstClassDeclaration.getXPathAttributesIterator().next().getValue();
        assertSame(value, actualIsInterfaceResult);
        assertSame(value, actualIsRegularInterfaceResult);
    }
}
