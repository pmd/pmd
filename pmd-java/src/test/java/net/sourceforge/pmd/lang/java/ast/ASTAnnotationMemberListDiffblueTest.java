package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTAnnotationMemberListDiffblueTest {
    /**
     * Method under test: {@link ASTAnnotationMemberList#getAttribute(String)}
     */
    @Test
    void testGetAttribute() {
        // Arrange, Act and Assert
        assertNull((new ASTAnnotationMemberList(1)).getAttribute("Attr Name"));
    }

    /**
     * Method under test:
     * {@link ASTAnnotationMemberList#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTAnnotationMemberList>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astAnnotationMemberList.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTAnnotationMemberList.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test:
     * {@link ASTAnnotationMemberList#ASTAnnotationMemberList(int)}
     */
    @Test
    void testNewASTAnnotationMemberList() {
        // Arrange, Act and Assert
        assertTrue((new ASTAnnotationMemberList(1)).toList().isEmpty());
    }
}
