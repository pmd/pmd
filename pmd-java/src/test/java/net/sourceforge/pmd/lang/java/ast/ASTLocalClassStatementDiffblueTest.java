package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTLocalClassStatementDiffblueTest {
    /**
     * Method under test:
     * {@link ASTLocalClassStatement#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTLocalClassStatement astLocalClassStatement = new ASTLocalClassStatement(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTLocalClassStatement>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astLocalClassStatement.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTLocalClassStatement.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTLocalClassStatement#getDeclaration()}
     */
    @Test
    void testGetDeclaration() {
        // Arrange
        ASTAnnotationTypeDeclaration tdecl = new ASTAnnotationTypeDeclaration(1);

        // Act and Assert
        assertSame(tdecl, (new ASTLocalClassStatement(tdecl)).getDeclaration());
    }

    /**
     * Method under test: {@link ASTLocalClassStatement#getDeclaration()}
     */
    @Test
    void testGetDeclaration2() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken token = new JavaccToken(1, Chars.EMPTY, 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        ASTLocalClassStatement astLocalClassStatement = new ASTLocalClassStatement(1);
        astLocalClassStatement.setLastToken(token);
        astLocalClassStatement.addChild(new ASTAmbiguousName(1), 2);
        ASTImplicitClassDeclaration child = new ASTImplicitClassDeclaration(1);
        astLocalClassStatement.addChild(child, 0);

        // Act
        ASTTypeDeclaration actualDeclaration = astLocalClassStatement.getDeclaration();

        // Assert
        verify(textDocument).getLength();
        assertSame(child, actualDeclaration);
    }

    /**
     * Method under test: {@link ASTLocalClassStatement#isFindBoundary()}
     */
    @Test
    void testIsFindBoundary() {
        // Arrange, Act and Assert
        assertTrue((new ASTLocalClassStatement(1)).isFindBoundary());
    }

    /**
     * Method under test: {@link ASTLocalClassStatement#ASTLocalClassStatement(int)}
     */
    @Test
    void testNewASTLocalClassStatement() {
        // Arrange and Act
        ASTLocalClassStatement actualAstLocalClassStatement = new ASTLocalClassStatement(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstLocalClassStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstLocalClassStatement.getXPathNodeName());
        assertEquals("Image", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstLocalClassStatement.getImage());
        assertNull(actualAstLocalClassStatement.getFirstChild());
        assertNull(actualAstLocalClassStatement.getLastChild());
        assertNull(actualAstLocalClassStatement.getNextSibling());
        assertNull(actualAstLocalClassStatement.getPreviousSibling());
        assertNull(actualAstLocalClassStatement.getFirstToken());
        assertNull(actualAstLocalClassStatement.getLastToken());
        assertNull(actualAstLocalClassStatement.getEnclosingType());
        assertNull(actualAstLocalClassStatement.symbolTable);
        assertEquals(0, actualAstLocalClassStatement.getIndexInParent());
        assertEquals(0, actualAstLocalClassStatement.getNumChildren());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstLocalClassStatement.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstLocalClassStatement.children().toList().isEmpty());
        assertTrue(actualAstLocalClassStatement.isFindBoundary());
        assertSame(actualAstLocalClassStatement, nextResult.getParent());
        assertSame(ancestorsResult, actualAstLocalClassStatement.descendants());
    }

    /**
     * Method under test:
     * {@link ASTLocalClassStatement#ASTLocalClassStatement(ASTTypeDeclaration)}
     */
    @Test
    void testNewASTLocalClassStatement2() {
        // Arrange
        ASTAnnotationTypeDeclaration tdecl = new ASTAnnotationTypeDeclaration(1);

        // Act
        ASTLocalClassStatement actualAstLocalClassStatement = new ASTLocalClassStatement(tdecl);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstLocalClassStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("Image", nextResult.getName());
        assertEquals("LocalClassStatement", actualAstLocalClassStatement.getXPathNodeName());
        assertNull(nextResult.getValue());
        assertNull(actualAstLocalClassStatement.getImage());
        assertNull(actualAstLocalClassStatement.getNextSibling());
        assertNull(actualAstLocalClassStatement.getPreviousSibling());
        assertNull(actualAstLocalClassStatement.getFirstToken());
        assertNull(actualAstLocalClassStatement.getLastToken());
        assertNull(actualAstLocalClassStatement.getEnclosingType());
        assertNull(actualAstLocalClassStatement.symbolTable);
        assertEquals(0, actualAstLocalClassStatement.getIndexInParent());
        assertEquals(1, tdecl.ancestors().toList().size());
        List<JavaNode> toListResult = actualAstLocalClassStatement.children().toList();
        assertEquals(1, toListResult.size());
        assertEquals(1, actualAstLocalClassStatement.getNumChildren());
        assertEquals(ModifierOwner.Visibility.V_LOCAL, tdecl.getEffectiveVisibility());
        assertTrue(xPathAttributesIterator.hasNext());
        assertTrue(actualAstLocalClassStatement.ancestors().toList().isEmpty());
        assertTrue(actualAstLocalClassStatement.isFindBoundary());
        assertEquals(toListResult, actualAstLocalClassStatement.descendants().toList());
        assertSame(tdecl, toListResult.get(0));
        assertSame(tdecl, actualAstLocalClassStatement.getFirstChild());
        assertSame(tdecl, actualAstLocalClassStatement.getLastChild());
        assertSame(tdecl, actualAstLocalClassStatement.getDeclaration());
        assertSame(actualAstLocalClassStatement, nextResult.getParent());
    }

    /**
     * Method under test:
     * {@link ASTLocalClassStatement#ASTLocalClassStatement(ASTTypeDeclaration)}
     */
    @Test
    void testNewASTLocalClassStatement3() {
        // Arrange
        ASTAnnotationTypeDeclaration tdecl = new ASTAnnotationTypeDeclaration(1);
        tdecl.setSymbol(mock(JClassSymbol.class));

        // Act
        ASTLocalClassStatement actualAstLocalClassStatement = new ASTLocalClassStatement(tdecl);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstLocalClassStatement.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("Image", nextResult.getName());
        assertEquals("LocalClassStatement", actualAstLocalClassStatement.getXPathNodeName());
        assertNull(nextResult.getValue());
        assertNull(actualAstLocalClassStatement.getImage());
        assertNull(actualAstLocalClassStatement.getNextSibling());
        assertNull(actualAstLocalClassStatement.getPreviousSibling());
        assertNull(actualAstLocalClassStatement.getFirstToken());
        assertNull(actualAstLocalClassStatement.getLastToken());
        assertNull(actualAstLocalClassStatement.getEnclosingType());
        assertNull(actualAstLocalClassStatement.symbolTable);
        assertEquals(0, actualAstLocalClassStatement.getIndexInParent());
        assertEquals(1, tdecl.ancestors().toList().size());
        List<JavaNode> toListResult = actualAstLocalClassStatement.children().toList();
        assertEquals(1, toListResult.size());
        assertEquals(1, actualAstLocalClassStatement.getNumChildren());
        assertEquals(ModifierOwner.Visibility.V_LOCAL, tdecl.getEffectiveVisibility());
        assertTrue(xPathAttributesIterator.hasNext());
        assertTrue(actualAstLocalClassStatement.ancestors().toList().isEmpty());
        assertTrue(actualAstLocalClassStatement.isFindBoundary());
        assertEquals(toListResult, actualAstLocalClassStatement.descendants().toList());
        assertSame(tdecl, toListResult.get(0));
        assertSame(tdecl, actualAstLocalClassStatement.getFirstChild());
        assertSame(tdecl, actualAstLocalClassStatement.getLastChild());
        assertSame(tdecl, actualAstLocalClassStatement.getDeclaration());
        assertSame(actualAstLocalClassStatement, nextResult.getParent());
    }
}
