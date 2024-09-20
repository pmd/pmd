package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTIfStatementDiffblueTest {
    /**
     * Method under test: {@link ASTIfStatement#getCondition()}
     */
    @Test
    void testGetCondition() {
        // Arrange
        ASTIfStatement astIfStatement = new ASTIfStatement(1);
        astIfStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astIfStatement.getCondition());
    }

    /**
     * Method under test: {@link ASTIfStatement#getCondition()}
     */
    @Test
    void testGetCondition2() {
        // Arrange
        ASTIfStatement astIfStatement = new ASTIfStatement(1);
        astIfStatement.setSymbolTable(mock(JSymbolTable.class));
        astIfStatement.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astIfStatement.getCondition());
    }

    /**
     * Method under test: {@link ASTIfStatement#getThenBranch()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetThenBranch() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 0
        //       at net.sourceforge.pmd.lang.ast.impl.AbstractNode.getChild(AbstractNode.java:59)
        //       at net.sourceforge.pmd.lang.java.ast.ASTIfStatement.getThenBranch(ASTIfStatement.java:56)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTIfStatement astIfStatement = null;

        // Act
        ASTStatement actualThenBranch = astIfStatement.getThenBranch();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTIfStatement#getElseBranch()}
     */
    @Test
    void testGetElseBranch() {
        // Arrange, Act and Assert
        assertNull((new ASTIfStatement(1)).getElseBranch());
    }

    /**
     * Method under test: {@link ASTIfStatement#getElseBranch()}
     */
    @Test
    void testGetElseBranch2() {
        // Arrange
        ASTIfStatement astIfStatement = new ASTIfStatement(1);
        astIfStatement.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astIfStatement.getElseBranch());
    }

    /**
     * Method under test: {@link ASTIfStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTIfStatement astIfStatement = new ASTIfStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTIfStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astIfStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTIfStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTIfStatement#setHasElse()}
     *   <li>{@link ASTIfStatement#hasElse()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTIfStatement astIfStatement = new ASTIfStatement(1);

        // Act
        astIfStatement.setHasElse();

        // Assert that nothing has changed
        assertTrue(astIfStatement.hasElse());
    }

    /**
     * Method under test: {@link ASTIfStatement#ASTIfStatement(int)}
     */
    @Test
    void testNewASTIfStatement() {
        // Arrange and Act
        ASTIfStatement actualAstIfStatement = new ASTIfStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstIfStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassBody", actualAstIfStatement.getXPathNodeName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Else", nextResult.getName());
        assertNull(actualAstIfStatement.getImage());
        assertNull(actualAstIfStatement.getFirstChild());
        assertNull(actualAstIfStatement.getLastChild());
        assertNull(actualAstIfStatement.getNextSibling());
        assertNull(actualAstIfStatement.getPreviousSibling());
        assertNull(actualAstIfStatement.getFirstToken());
        assertNull(actualAstIfStatement.getLastToken());
        assertNull(actualAstIfStatement.getElseBranch());
        assertNull(actualAstIfStatement.getEnclosingType());
        assertNull(actualAstIfStatement.symbolTable);
        assertEquals(0, actualAstIfStatement.getIndexInParent());
        assertEquals(0, actualAstIfStatement.getNumChildren());
        assertFalse(actualAstIfStatement.isFindBoundary());
        assertFalse(actualAstIfStatement.hasElse());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstIfStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstIfStatement.children().toList().isEmpty());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstIfStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstIfStatement.descendants());
    }
}
