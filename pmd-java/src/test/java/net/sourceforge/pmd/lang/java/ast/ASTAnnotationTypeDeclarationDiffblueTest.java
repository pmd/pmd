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

class ASTAnnotationTypeDeclarationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTAnnotationTypeDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTAnnotationTypeDeclaration astAnnotationTypeDeclaration = new ASTAnnotationTypeDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTAnnotationTypeDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astAnnotationTypeDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTAnnotationTypeDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTAnnotationTypeDeclaration#ASTAnnotationTypeDeclaration(int)}
     *   <li>{@link ASTAnnotationTypeDeclaration#isInterface()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        ASTAnnotationTypeDeclaration actualAstAnnotationTypeDeclaration = new ASTAnnotationTypeDeclaration(1);
        boolean actualIsInterfaceResult = actualAstAnnotationTypeDeclaration.isInterface();

        // Assert
        assertNull(actualAstAnnotationTypeDeclaration.getImage());
        assertNull(actualAstAnnotationTypeDeclaration.getFirstToken());
        assertNull(actualAstAnnotationTypeDeclaration.getLastToken());
        assertNull(actualAstAnnotationTypeDeclaration.getTypeMirrorInternal());
        assertEquals(0, actualAstAnnotationTypeDeclaration.getIndexInParent());
        assertSame(actualAstAnnotationTypeDeclaration.getXPathAttributesIterator().next().getValue(),
                actualIsInterfaceResult);
    }
}
