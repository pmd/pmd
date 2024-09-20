package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTLambdaParameterListDiffblueTest {
    /**
     * Method under test:
     * {@link ASTLambdaParameterList#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTLambdaParameterList astLambdaParameterList = new ASTLambdaParameterList(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTLambdaParameterList>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astLambdaParameterList.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTLambdaParameterList.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTLambdaParameterList#ASTLambdaParameterList(int)}
     */
    @Test
    void testNewASTLambdaParameterList() {
        // Arrange, Act and Assert
        assertTrue((new ASTLambdaParameterList(1)).toList().isEmpty());
    }
}
