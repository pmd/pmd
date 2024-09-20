package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTArrayDimensionsDiffblueTest {
    /**
     * Method under test:
     * {@link ASTArrayDimensions#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArrayDimensions astArrayDimensions = new ASTArrayDimensions(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArrayDimensions>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArrayDimensions.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArrayDimensions.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTArrayDimensions#ASTArrayDimensions(int)}
     */
    @Test
    void testNewASTArrayDimensions() {
        // Arrange, Act and Assert
        assertTrue((new ASTArrayDimensions(1)).toList().isEmpty());
    }
}
