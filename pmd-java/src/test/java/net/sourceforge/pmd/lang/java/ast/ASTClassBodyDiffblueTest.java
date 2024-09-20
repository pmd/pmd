package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTClassBodyDiffblueTest {
    /**
     * Method under test: {@link ASTClassBody#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTClassBody astClassBody = new ASTClassBody(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTClassBody>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astClassBody.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTClassBody.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTClassBody#ASTClassBody(int)}
     */
    @Test
    void testNewASTClassBody() {
        // Arrange, Act and Assert
        assertTrue((new ASTClassBody(1)).toList().isEmpty());
    }
}
