package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTExtendsListDiffblueTest {
    /**
     * Method under test: {@link ASTExtendsList#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTExtendsList astExtendsList = new ASTExtendsList(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTExtendsList>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astExtendsList.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTExtendsList.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTExtendsList#ASTExtendsList(int)}
     */
    @Test
    void testNewASTExtendsList() {
        // Arrange, Act and Assert
        assertTrue((new ASTExtendsList(1)).toList().isEmpty());
    }
}
