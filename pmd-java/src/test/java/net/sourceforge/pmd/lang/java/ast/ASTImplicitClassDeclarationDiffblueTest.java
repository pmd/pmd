package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTImplicitClassDeclarationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTImplicitClassDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTImplicitClassDeclaration astImplicitClassDeclaration = new ASTImplicitClassDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTImplicitClassDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astImplicitClassDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTImplicitClassDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTImplicitClassDeclaration#ASTImplicitClassDeclaration(int)}
     *   <li>{@link ASTImplicitClassDeclaration#getSimpleName()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        ASTImplicitClassDeclaration actualAstImplicitClassDeclaration = new ASTImplicitClassDeclaration(1);
        String actualSimpleName = actualAstImplicitClassDeclaration.getSimpleName();

        // Assert
        assertNull(actualAstImplicitClassDeclaration.getImage());
        assertNull(actualAstImplicitClassDeclaration.getFirstToken());
        assertNull(actualAstImplicitClassDeclaration.getLastToken());
        assertNull(actualAstImplicitClassDeclaration.getTypeMirrorInternal());
        assertEquals(0, actualAstImplicitClassDeclaration.getIndexInParent());
        assertSame(actualAstImplicitClassDeclaration.getXPathAttributesIterator().next().getValue(), actualSimpleName);
    }
}
