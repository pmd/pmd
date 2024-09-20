package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import org.junit.jupiter.api.Test;

class ASTLoopStatementDiffblueTest {
    /**
     * Method under test: {@link ASTLoopStatement#getBody()}
     */
    @Test
    void testGetBody() {
        // Arrange, Act and Assert
        assertNull((new ASTForStatement(1)).getBody());
    }

    /**
     * Method under test: {@link ASTLoopStatement#getCondition()}
     */
    @Test
    void testGetCondition() {
        // Arrange, Act and Assert
        assertNull((new ASTForeachStatement(1)).getCondition());
    }

    /**
     * Method under test: {@link ASTLoopStatement#getCondition()}
     */
    @Test
    void testGetCondition2() {
        // Arrange
        ASTForeachStatement astForeachStatement = new ASTForeachStatement(1);
        astForeachStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astForeachStatement.getCondition());
    }
}
