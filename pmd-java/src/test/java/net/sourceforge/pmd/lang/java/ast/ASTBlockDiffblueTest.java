package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.document.TextDocument;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTBlockDiffblueTest {
    /**
     * Method under test: {@link ASTBlock#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTBlock astBlock = new ASTBlock(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTBlock>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astBlock.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTBlock.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTBlock#containsComment()}
     */
    @Test
    void testContainsComment() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken token = new JavaccToken(1, "Image", 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        ASTBlock astBlock = new ASTBlock(1);
        astBlock.setLastToken(token);

        // Act
        boolean actualContainsCommentResult = astBlock.containsComment();

        // Assert
        verify(textDocument).getLength();
        assertFalse(actualContainsCommentResult);
    }

    /**
     * Method under test: {@link ASTBlock#ASTBlock(int)}
     */
    @Test
    void testNewASTBlock() {
        // Arrange, Act and Assert
        assertTrue((new ASTBlock(1)).toList().isEmpty());
    }
}
