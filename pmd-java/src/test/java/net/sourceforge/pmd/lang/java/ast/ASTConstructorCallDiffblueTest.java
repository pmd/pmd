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
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTConstructorCallDiffblueTest {
    /**
     * Method under test: {@link ASTConstructorCall#ASTConstructorCall(int)}
     */
    @Test
    void testNewASTConstructorCall() {
        // Arrange and Act
        ASTConstructorCall actualAstConstructorCall = new ASTConstructorCall(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstConstructorCall.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("AnonymousClass", nextResult.getName());
        assertEquals("ClassBody", actualAstConstructorCall.getXPathNodeName());
        assertEquals("new", actualAstConstructorCall.getMethodName());
        assertNull(actualAstConstructorCall.getImage());
        assertNull(actualAstConstructorCall.getFirstChild());
        assertNull(actualAstConstructorCall.getLastChild());
        assertNull(actualAstConstructorCall.getNextSibling());
        assertNull(actualAstConstructorCall.getPreviousSibling());
        assertNull(actualAstConstructorCall.getFirstToken());
        assertNull(actualAstConstructorCall.getLastToken());
        assertNull(actualAstConstructorCall.getAnonymousClassDeclaration());
        assertNull(actualAstConstructorCall.getArguments());
        assertNull(actualAstConstructorCall.getTypeNode());
        assertNull(actualAstConstructorCall.getQualifier());
        assertNull(actualAstConstructorCall.getExplicitTypeArguments());
        assertNull(actualAstConstructorCall.getEnclosingType());
        assertNull(actualAstConstructorCall.symbolTable);
        assertNull(actualAstConstructorCall.getTypeMirrorInternal());
        assertEquals(0, actualAstConstructorCall.getIndexInParent());
        assertEquals(0, actualAstConstructorCall.getNumChildren());
        assertFalse(actualAstConstructorCall.isFindBoundary());
        assertFalse(actualAstConstructorCall.isAnonymousClass());
        assertFalse(actualAstConstructorCall.isCompileTimeConstant());
        assertFalse(actualAstConstructorCall.isParenthesized());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstConstructorCall.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstConstructorCall.children().toList().isEmpty());
        assertTrue(actualAstConstructorCall.isExpression());
        String expectedStringValue = Boolean.FALSE.toString();
        assertEquals(expectedStringValue, nextResult.getStringValue());
        assertSame(actualAstConstructorCall, nextResult.getParent());
        assertSame(ancestorsResult, actualAstConstructorCall.descendants());
    }

    /**
     * Method under test:
     * {@link ASTConstructorCall#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTConstructorCall>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astConstructorCall.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTConstructorCall.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTConstructorCall#isQualifiedInstanceCreation()}
     */
    @Test
    void testIsQualifiedInstanceCreation() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astConstructorCall.isQualifiedInstanceCreation());
    }

    /**
     * Method under test: {@link ASTConstructorCall#isQualifiedInstanceCreation()}
     */
    @Test
    void testIsQualifiedInstanceCreation2() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.setSymbolTable(mock(JSymbolTable.class));
        astConstructorCall.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astConstructorCall.isQualifiedInstanceCreation());
    }

    /**
     * Method under test: {@link ASTConstructorCall#isQualifiedInstanceCreation()}
     */
    @Test
    void testIsQualifiedInstanceCreation3() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertTrue(astConstructorCall.isQualifiedInstanceCreation());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getQualifier()}
     */
    @Test
    void testGetQualifier() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorCall(1)).getQualifier());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getQualifier()}
     */
    @Test
    void testGetQualifier2() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astConstructorCall.getQualifier());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getExplicitTypeArguments()}
     */
    @Test
    void testGetExplicitTypeArguments() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorCall(1)).getExplicitTypeArguments());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getExplicitTypeArguments()}
     */
    @Test
    void testGetExplicitTypeArguments2() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astConstructorCall.getExplicitTypeArguments());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getArguments()}
     */
    @Test
    void testGetArguments() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorCall(1)).getArguments());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getArguments()}
     */
    @Test
    void testGetArguments2() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken token = new JavaccToken(2, "Image", 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.setLastToken(token);
        astConstructorCall.addChild(new ASTAnonymousClassDeclaration(1), 1);

        // Act
        ASTArgumentList actualArguments = astConstructorCall.getArguments();

        // Assert
        verify(textDocument).getLength();
        assertNull(actualArguments);
    }

    /**
     * Method under test: {@link ASTConstructorCall#usesDiamondTypeArgs()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testUsesDiamondTypeArgs() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "net.sourceforge.pmd.lang.java.ast.ASTClassType.getTypeArguments()" because the return value of "net.sourceforge.pmd.lang.java.ast.ASTConstructorCall.getTypeNode()" is null
        //       at net.sourceforge.pmd.lang.java.ast.ASTConstructorCall.usesDiamondTypeArgs(ASTConstructorCall.java:77)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTConstructorCall astConstructorCall = null;

        // Act
        boolean actualUsesDiamondTypeArgsResult = astConstructorCall.usesDiamondTypeArgs();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTConstructorCall#getTypeNode()}
     */
    @Test
    void testGetTypeNode() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorCall(1)).getTypeNode());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getTypeNode()}
     */
    @Test
    void testGetTypeNode2() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astConstructorCall.getTypeNode());
    }

    /**
     * Method under test: {@link ASTConstructorCall#isAnonymousClass()}
     */
    @Test
    void testIsAnonymousClass() {
        // Arrange, Act and Assert
        assertFalse((new ASTConstructorCall(1)).isAnonymousClass());
    }

    /**
     * Method under test: {@link ASTConstructorCall#isAnonymousClass()}
     */
    @Test
    void testIsAnonymousClass2() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertFalse(astConstructorCall.isAnonymousClass());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getAnonymousClassDeclaration()}
     */
    @Test
    void testGetAnonymousClassDeclaration() {
        // Arrange, Act and Assert
        assertNull((new ASTConstructorCall(1)).getAnonymousClassDeclaration());
    }

    /**
     * Method under test: {@link ASTConstructorCall#getAnonymousClassDeclaration()}
     */
    @Test
    void testGetAnonymousClassDeclaration2() {
        // Arrange
        ASTConstructorCall astConstructorCall = new ASTConstructorCall(1);
        astConstructorCall.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astConstructorCall.getAnonymousClassDeclaration());
    }
}
