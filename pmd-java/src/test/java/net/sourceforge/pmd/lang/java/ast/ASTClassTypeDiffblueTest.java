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
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTClassTypeDiffblueTest {
    /**
     * Method under test: {@link ASTClassType#getQualifier()}
     */
    @Test
    void testGetQualifier() {
        // Arrange, Act and Assert
        assertNull((new ASTClassType(1)).getQualifier());
    }

    /**
     * Method under test: {@link ASTClassType#getQualifier()}
     */
    @Test
    void testGetQualifier2() {
        // Arrange
        ASTClassType astClassType = new ASTClassType(1);
        astClassType.setSymbol(mock(JTypeDeclSymbol.class));

        // Act and Assert
        assertNull(astClassType.getQualifier());
    }

    /**
     * Method under test: {@link ASTClassType#getTypeArguments()}
     */
    @Test
    void testGetTypeArguments() {
        // Arrange, Act and Assert
        assertNull((new ASTClassType(1)).getTypeArguments());
    }

    /**
     * Method under test: {@link ASTClassType#getTypeArguments()}
     */
    @Test
    void testGetTypeArguments2() {
        // Arrange
        ASTClassType astClassType = new ASTClassType(1);
        astClassType.setSymbol(mock(JTypeDeclSymbol.class));

        // Act and Assert
        assertNull(astClassType.getTypeArguments());
    }

    /**
     * Method under test: {@link ASTClassType#getPackageQualifier()}
     */
    @Test
    void testGetPackageQualifier() {
        // Arrange, Act and Assert
        assertNull((new ASTClassType(1)).getPackageQualifier());
    }

    /**
     * Method under test: {@link ASTClassType#getPackageQualifier()}
     */
    @Test
    void testGetPackageQualifier2() {
        // Arrange
        ASTClassType astClassType = new ASTClassType(1);
        astClassType.setSymbol(mock(JTypeDeclSymbol.class));

        // Act and Assert
        assertNull(astClassType.getPackageQualifier());
    }

    /**
     * Method under test: {@link ASTClassType#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTClassType astClassType = new ASTClassType(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTClassType>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astClassType.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTClassType.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTClassType#setSimpleName(String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSetSimpleName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.AssertionError: Invalid simple name 'Simple Name'
        //       at net.sourceforge.pmd.lang.java.ast.ASTClassType.assertSimpleNameOk(ASTClassType.java:167)
        //       at net.sourceforge.pmd.lang.java.ast.ASTClassType.setSimpleName(ASTClassType.java:163)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTClassType astClassType = null;
        String simpleName = "";

        // Act
        astClassType.setSimpleName(simpleName);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTClassType#ASTClassType(int)}
     *   <li>{@link ASTClassType#setImplicitEnclosing(JClassType)}
     *   <li>{@link ASTClassType#setSymbol(JTypeDeclSymbol)}
     *   <li>{@link ASTClassType#setFullyQualified()}
     *   <li>{@link ASTClassType#getImplicitEnclosing()}
     *   <li>{@link ASTClassType#getReferencedSym()}
     *   <li>{@link ASTClassType#getSimpleName()}
     *   <li>{@link ASTClassType#isFullyQualified()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        ASTClassType actualAstClassType = new ASTClassType(1);
        JClassType enclosing = mock(JClassType.class);
        actualAstClassType.setImplicitEnclosing(enclosing);
        JTypeDeclSymbol symbol = mock(JTypeDeclSymbol.class);
        actualAstClassType.setSymbol(symbol);
        actualAstClassType.setFullyQualified();
        JClassType actualImplicitEnclosing = actualAstClassType.getImplicitEnclosing();
        JTypeDeclSymbol actualReferencedSym = actualAstClassType.getReferencedSym();
        actualAstClassType.getSimpleName();
        boolean actualIsFullyQualifiedResult = actualAstClassType.isFullyQualified();

        // Assert that nothing has changed
        assertEquals(0, actualAstClassType.getIndexInParent());
        assertTrue(actualIsFullyQualifiedResult);
        assertSame(symbol, actualReferencedSym);
        assertSame(enclosing, actualImplicitEnclosing);
    }

    /**
     * Method under test: {@link ASTClassType#ASTClassType(ASTAmbiguousName)}
     */
    @Test
    void testNewASTClassType() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken token = new JavaccToken(JavaTokenKinds.NATIVE, "Image", 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        ASTAmbiguousName simpleName = new ASTAmbiguousName(1);
        simpleName.setFirstToken(token);

        // Act
        ASTClassType actualAstClassType = new ASTClassType(simpleName);

        // Assert
        verify(textDocument).getLength();
        Iterator<Attribute> xPathAttributesIterator = actualAstClassType.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassType", actualAstClassType.getXPathNodeName());
        assertEquals("Image", actualAstClassType.getSimpleName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Image", nextResult.getStringValue());
        assertEquals("Image", nextResult.getValue());
        assertEquals("SimpleName", nextResult.getName());
        assertNull(actualAstClassType.getImage());
        assertNull(actualAstClassType.getPackageQualifier());
        assertNull(actualAstClassType.getFirstChild());
        assertNull(actualAstClassType.getLastChild());
        assertNull(actualAstClassType.getNextSibling());
        assertNull(actualAstClassType.getPreviousSibling());
        assertNull(actualAstClassType.getFirstToken());
        assertNull(actualAstClassType.getLastToken());
        assertNull(actualAstClassType.getQualifier());
        assertNull(actualAstClassType.getTypeArguments());
        assertNull(actualAstClassType.getEnclosingType());
        assertNull(actualAstClassType.getReferencedSym());
        assertNull(actualAstClassType.symbolTable);
        assertNull(actualAstClassType.getImplicitEnclosing());
        assertNull(actualAstClassType.getTypeMirrorInternal());
        assertEquals(0, actualAstClassType.getIndexInParent());
        assertEquals(0, actualAstClassType.getNumChildren());
        assertFalse(actualAstClassType.isFindBoundary());
        assertFalse(actualAstClassType.isFullyQualified());
        assertFalse(actualAstClassType.isVoid());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstClassType.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstClassType.children().toList().isEmpty());
        assertSame(actualAstClassType, nextResult.getParent());
        assertSame(ancestorsResult, actualAstClassType.descendants());
        assertSame(ancestorsResult, actualAstClassType.getDeclaredAnnotations());
    }

    /**
     * Method under test:
     * {@link ASTClassType#ASTClassType(ASTAmbiguousName, String)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewASTClassType2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.AssertionError: Invalid simple name 'Simple Name'
        //       at net.sourceforge.pmd.lang.java.ast.ASTClassType.assertSimpleNameOk(ASTClassType.java:167)
        //       at net.sourceforge.pmd.lang.java.ast.ASTClassType.setSimpleName(ASTClassType.java:163)
        //       at net.sourceforge.pmd.lang.java.ast.ASTClassType.<init>(ASTClassType.java:52)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTAmbiguousName lhs = null;
        String simpleName = "";

        // Act
        ASTClassType actualAstClassType = new ASTClassType(lhs, simpleName);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link ASTClassType#ASTClassType(ASTClassType, boolean, JavaccToken, JavaccToken)}
     */
    @Test
    void testNewASTClassType3() {
        // Arrange
        ASTClassType lhs = new ASTClassType(1);
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken firstToken = new JavaccToken(1, "Image", 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        TextDocument textDocument2 = mock(TextDocument.class);
        when(textDocument2.getLength()).thenReturn(3);
        JavaccToken identifier = new JavaccToken(1, "Image", 1, 3,
                new JavaccTokenDocument(textDocument2, InternalApiBridge.javaTokenDoc()));

        // Act
        ASTClassType actualAstClassType = new ASTClassType(lhs, true, firstToken, identifier);

        // Assert
        verify(textDocument).getLength();
        verify(textDocument2).getLength();
        Iterator<Attribute> xPathAttributesIterator = actualAstClassType.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassType", actualAstClassType.getXPathNodeName());
        assertEquals("Image", actualAstClassType.getSimpleName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Image", nextResult.getStringValue());
        assertEquals("Image", nextResult.getValue());
        assertEquals("SimpleName", nextResult.getName());
        assertNull(actualAstClassType.getImage());
        assertNull(actualAstClassType.getNextSibling());
        assertNull(actualAstClassType.getPreviousSibling());
        assertNull(actualAstClassType.getTypeArguments());
        assertNull(actualAstClassType.getEnclosingType());
        assertNull(actualAstClassType.getReferencedSym());
        assertNull(actualAstClassType.symbolTable);
        assertNull(actualAstClassType.getImplicitEnclosing());
        assertNull(actualAstClassType.getTypeMirrorInternal());
        assertEquals(0, actualAstClassType.getIndexInParent());
        assertEquals(1, lhs.ancestors().toList().size());
        List<JavaNode> toListResult = actualAstClassType.children().toList();
        assertEquals(1, toListResult.size());
        assertEquals(1, actualAstClassType.getNumChildren());
        TextRegion textRegion = actualAstClassType.getTextRegion();
        assertEquals(1, textRegion.getStartOffset());
        assertEquals(2, textRegion.getLength());
        assertEquals(3, textRegion.getEndOffset());
        assertFalse(actualAstClassType.isFindBoundary());
        assertFalse(textRegion.isEmpty());
        assertFalse(actualAstClassType.isVoid());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstClassType.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstClassType.isFullyQualified());
        assertEquals(toListResult, actualAstClassType.descendants().toList());
        assertSame(firstToken, actualAstClassType.getFirstToken());
        assertSame(identifier, actualAstClassType.getLastToken());
        assertSame(lhs, toListResult.get(0));
        assertSame(lhs, actualAstClassType.getFirstChild());
        assertSame(lhs, actualAstClassType.getLastChild());
        assertSame(lhs, actualAstClassType.getQualifier());
        assertSame(actualAstClassType, nextResult.getParent());
        assertSame(ancestorsResult, actualAstClassType.getDeclaredAnnotations());
    }

    /**
     * Method under test:
     * {@link ASTClassType#ASTClassType(ASTClassType, boolean, JavaccToken, JavaccToken)}
     */
    @Test
    void testNewASTClassType4() {
        // Arrange
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaccToken firstToken = new JavaccToken(1, "Image", 1, 3,
                new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc()));

        TextDocument textDocument2 = mock(TextDocument.class);
        when(textDocument2.getLength()).thenReturn(3);
        JavaccToken identifier = new JavaccToken(1, "Image", 1, 3,
                new JavaccTokenDocument(textDocument2, InternalApiBridge.javaTokenDoc()));

        // Act
        ASTClassType actualAstClassType = new ASTClassType(null, true, firstToken, identifier);

        // Assert
        verify(textDocument).getLength();
        verify(textDocument2).getLength();
        Iterator<Attribute> xPathAttributesIterator = actualAstClassType.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("ClassType", actualAstClassType.getXPathNodeName());
        assertEquals("Image", actualAstClassType.getSimpleName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("Image", nextResult.getStringValue());
        assertEquals("Image", nextResult.getValue());
        assertEquals("SimpleName", nextResult.getName());
        assertNull(actualAstClassType.getImage());
        assertNull(actualAstClassType.getFirstChild());
        assertNull(actualAstClassType.getLastChild());
        assertNull(actualAstClassType.getNextSibling());
        assertNull(actualAstClassType.getPreviousSibling());
        assertNull(actualAstClassType.getQualifier());
        assertNull(actualAstClassType.getTypeArguments());
        assertNull(actualAstClassType.getEnclosingType());
        assertNull(actualAstClassType.getReferencedSym());
        assertNull(actualAstClassType.symbolTable);
        assertNull(actualAstClassType.getImplicitEnclosing());
        assertNull(actualAstClassType.getTypeMirrorInternal());
        assertEquals(0, actualAstClassType.getIndexInParent());
        assertEquals(0, actualAstClassType.getNumChildren());
        TextRegion textRegion = actualAstClassType.getTextRegion();
        assertEquals(1, textRegion.getStartOffset());
        assertEquals(2, textRegion.getLength());
        assertEquals(3, textRegion.getEndOffset());
        assertFalse(actualAstClassType.isFindBoundary());
        assertFalse(textRegion.isEmpty());
        assertFalse(actualAstClassType.isVoid());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstClassType.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstClassType.children().toList().isEmpty());
        assertTrue(actualAstClassType.isFullyQualified());
        assertSame(firstToken, actualAstClassType.getFirstToken());
        assertSame(identifier, actualAstClassType.getLastToken());
        assertSame(actualAstClassType, nextResult.getParent());
        assertSame(ancestorsResult, actualAstClassType.descendants());
        assertSame(ancestorsResult, actualAstClassType.getDeclaredAnnotations());
    }
}
