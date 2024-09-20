package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTEnumBodyDiffblueTest {
    /**
     * Method under test: {@link ASTEnumBody#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTEnumBody astEnumBody = new ASTEnumBody(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTEnumBody>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astEnumBody.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTEnumBody.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTEnumBody#setSeparatorSemi()}
     *   <li>{@link ASTEnumBody#setTrailingComma()}
     *   <li>{@link ASTEnumBody#hasSeparatorSemi()}
     *   <li>{@link ASTEnumBody#hasTrailingComma()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTEnumBody astEnumBody = new ASTEnumBody(1);

        // Act
        astEnumBody.setSeparatorSemi();
        astEnumBody.setTrailingComma();
        boolean actualHasSeparatorSemiResult = astEnumBody.hasSeparatorSemi();

        // Assert that nothing has changed
        assertTrue(actualHasSeparatorSemiResult);
        assertTrue(astEnumBody.hasTrailingComma());
    }

    /**
     * Method under test: {@link ASTEnumBody#ASTEnumBody(int)}
     */
    @Test
    void testNewASTEnumBody() {
        // Arrange, Act and Assert
        assertTrue((new ASTEnumBody(1)).toList().isEmpty());
    }
}
