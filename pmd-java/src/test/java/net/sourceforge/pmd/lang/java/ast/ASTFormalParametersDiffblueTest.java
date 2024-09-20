package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTFormalParametersDiffblueTest {
    /**
     * Method under test: {@link ASTFormalParameters#size()}
     */
    @Test
    void testSize() {
        // Arrange, Act and Assert
        assertEquals(0, (new ASTFormalParameters(1)).size());
    }

    /**
     * Method under test: {@link ASTFormalParameters#size()}
     */
    @Test
    void testSize2() {
        // Arrange
        ASTFormalParameters astFormalParameters = new ASTFormalParameters(1);
        astFormalParameters.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertEquals(0, astFormalParameters.size());
    }

    /**
     * Method under test:
     * {@link ASTFormalParameters#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTFormalParameters astFormalParameters = new ASTFormalParameters(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTFormalParameters>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astFormalParameters.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTFormalParameters.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTFormalParameters#getReceiverParameter()}
     */
    @Test
    void testGetReceiverParameter() {
        // Arrange, Act and Assert
        assertNull((new ASTFormalParameters(1)).getReceiverParameter());
    }

    /**
     * Method under test: {@link ASTFormalParameters#getReceiverParameter()}
     */
    @Test
    void testGetReceiverParameter2() {
        // Arrange
        ASTFormalParameters astFormalParameters = new ASTFormalParameters(1);
        astFormalParameters.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astFormalParameters.getReceiverParameter());
    }

    /**
     * Method under test: {@link ASTFormalParameters#getReceiverParameter()}
     */
    @Test
    void testGetReceiverParameter3() {
        // Arrange
        ASTFormalParameters astFormalParameters = new ASTFormalParameters(1);
        astFormalParameters.setSymbolTable(mock(JSymbolTable.class));
        astFormalParameters.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astFormalParameters.getReceiverParameter());
    }

    /**
     * Method under test: {@link ASTFormalParameters#ASTFormalParameters(int)}
     */
    @Test
    void testNewASTFormalParameters() {
        // Arrange, Act and Assert
        assertTrue((new ASTFormalParameters(1)).toList().isEmpty());
    }
}
