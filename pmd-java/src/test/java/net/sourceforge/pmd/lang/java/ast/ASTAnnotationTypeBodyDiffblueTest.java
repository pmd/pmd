package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTAnnotationTypeBodyDiffblueTest {
    /**
     * Method under test:
     * {@link ASTAnnotationTypeBody#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTAnnotationTypeBody astAnnotationTypeBody = new ASTAnnotationTypeBody(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTAnnotationTypeBody>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astAnnotationTypeBody.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTAnnotationTypeBody.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTAnnotationTypeBody#ASTAnnotationTypeBody(int)}
     */
    @Test
    void testNewASTAnnotationTypeBody() {
        // Arrange, Act and Assert
        assertTrue((new ASTAnnotationTypeBody(1)).toList().isEmpty());
    }
}
