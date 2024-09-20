package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTImplementsListDiffblueTest {
    /**
     * Method under test:
     * {@link ASTImplementsList#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTImplementsList astImplementsList = new ASTImplementsList(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTImplementsList>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astImplementsList.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTImplementsList.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTImplementsList#ASTImplementsList(int)}
     */
    @Test
    void testNewASTImplementsList() {
        // Arrange, Act and Assert
        assertTrue((new ASTImplementsList(1)).toList().isEmpty());
    }
}
