package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTArrayTypeDiffblueTest {
    /**
     * Method under test: {@link ASTArrayType#getDeclaredAnnotations()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetDeclaredAnnotations() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTArrayType astArrayType = null;

        // Act
        NodeStream<ASTAnnotation> actualDeclaredAnnotations = astArrayType.getDeclaredAnnotations();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTArrayType#getDimensions()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetDimensions() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTArrayType astArrayType = null;

        // Act
        ASTArrayDimensions actualDimensions = astArrayType.getDimensions();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTArrayType#getElementType()}
     */
    @Test
    void testGetElementType() {
        // Arrange
        ASTArrayType astArrayType = new ASTArrayType(1);
        astArrayType.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayType.getElementType());
    }

    /**
     * Method under test: {@link ASTArrayType#getElementType()}
     */
    @Test
    void testGetElementType2() {
        // Arrange
        ASTArrayType astArrayType = new ASTArrayType(1);
        astArrayType.setSymbolTable(mock(JSymbolTable.class));
        astArrayType.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astArrayType.getElementType());
    }

    /**
     * Method under test: {@link ASTArrayType#getArrayDepth()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetArrayDepth() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTArrayType astArrayType = null;

        // Act
        int actualArrayDepth = astArrayType.getArrayDepth();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTArrayType#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTArrayType astArrayType = new ASTArrayType(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTArrayType>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astArrayType.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTArrayType.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTArrayType#ASTArrayType(int)}
     */
    @Test
    void testNewASTArrayType() {
        // Arrange and Act
        ASTArrayType actualAstArrayType = new ASTArrayType(1);

        // Assert
        assertNull(actualAstArrayType.getImage());
        assertNull(actualAstArrayType.getFirstToken());
        assertNull(actualAstArrayType.getLastToken());
        assertNull(actualAstArrayType.getTypeMirrorInternal());
        assertEquals(0, actualAstArrayType.getIndexInParent());
    }
}
