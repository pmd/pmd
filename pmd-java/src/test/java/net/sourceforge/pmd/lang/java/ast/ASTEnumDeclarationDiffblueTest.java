package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTEnumDeclarationDiffblueTest {
    /**
     * Method under test:
     * {@link ASTEnumDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTEnumDeclaration astEnumDeclaration = new ASTEnumDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTEnumDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astEnumDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTEnumDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTEnumDeclaration#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange, Act and Assert
        assertNull((new ASTEnumDeclaration(1)).getBody());
    }

    /**
     * Method under test: {@link ASTEnumDeclaration#ASTEnumDeclaration(int)}
     */
    @Test
    void testNewASTEnumDeclaration() {
        // Arrange and Act
        ASTEnumDeclaration actualAstEnumDeclaration = new ASTEnumDeclaration(1);

        // Assert
        assertNull(actualAstEnumDeclaration.getImage());
        assertNull(actualAstEnumDeclaration.getFirstToken());
        assertNull(actualAstEnumDeclaration.getLastToken());
        assertNull(actualAstEnumDeclaration.getTypeMirrorInternal());
        assertEquals(0, actualAstEnumDeclaration.getIndexInParent());
    }
}
