package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTArgumentListDiffblueTest {
    /**
     * Method under test: {@link ASTArgumentList#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArgumentList astArgumentList = new ASTArgumentList(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArgumentList>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArgumentList.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArgumentList.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTArgumentList#ASTArgumentList(int)}
     */
    @Test
    void testNewASTArgumentList() {
        // Arrange, Act and Assert
        assertTrue((new ASTArgumentList(1)).toList().isEmpty());
    }
}
