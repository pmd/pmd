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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.ast.internal.LazyTypeResolver;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTCompilationUnitDiffblueTest {
    /**
     * Method under test: {@link ASTCompilationUnit#setComments(List)}
     */
    @Test
    void testSetComments() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);

        // Act
        astCompilationUnit.setComments(new ArrayList<>());

        // Assert
        assertTrue(astCompilationUnit.getComments().isEmpty());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#setComments(List)}
     */
    @Test
    void testSetComments2() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaComment javaComment = new JavaComment(
                new JavaccToken(1, "Image", 1, 3, new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc())));

        ArrayList<JavaComment> comments = new ArrayList<>();
        comments.add(javaComment);

        // Act
        astCompilationUnit.setComments(comments);

        // Assert
        verify(textDocument).getLength();
        assertEquals(comments, astCompilationUnit.getComments());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#setComments(List)}
     */
    @Test
    void testSetComments3() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getLength()).thenReturn(3);
        JavaComment javaComment = new JavaComment(
                new JavaccToken(1, "Image", 1, 3, new JavaccTokenDocument(textDocument, InternalApiBridge.javaTokenDoc())));
        TextDocument textDocument2 = mock(TextDocument.class);
        when(textDocument2.getLength()).thenReturn(3);
        JavaComment javaComment2 = new JavaComment(
                new JavaccToken(1, "Image", 1, 3, new JavaccTokenDocument(textDocument2, InternalApiBridge.javaTokenDoc())));

        ArrayList<JavaComment> comments = new ArrayList<>();
        comments.add(javaComment2);
        comments.add(javaComment);

        // Act
        astCompilationUnit.setComments(comments);

        // Assert
        verify(textDocument2).getLength();
        verify(textDocument).getLength();
        assertEquals(comments, astCompilationUnit.getComments());
    }

    /**
     * Method under test:
     * {@link ASTCompilationUnit#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTCompilationUnit>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astCompilationUnit.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTCompilationUnit.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getPackageDeclaration()}
     */
    @Test
    void testGetPackageDeclaration() {
        // Arrange, Act and Assert
        assertNull((new ASTCompilationUnit(1)).getPackageDeclaration());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getPackageDeclaration()}
     */
    @Test
    void testGetPackageDeclaration2() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astCompilationUnit.getPackageDeclaration());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getPackageDeclaration()}
     */
    @Test
    void testGetPackageDeclaration3() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.setSymbolTable(mock(JSymbolTable.class));
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astCompilationUnit.getPackageDeclaration());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getPackageName()}
     */
    @Test
    void testGetPackageName() {
        // Arrange, Act and Assert
        assertEquals("", (new ASTCompilationUnit(1)).getPackageName());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getPackageName()}
     */
    @Test
    void testGetPackageName2() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertEquals("", astCompilationUnit.getPackageName());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getPackageName()}
     */
    @Test
    void testGetPackageName3() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.setSymbolTable(mock(JSymbolTable.class));
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertEquals("", astCompilationUnit.getPackageName());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getTypeDeclarations()}
     */
    @Test
    void testGetTypeDeclarations() {
        // Arrange, Act and Assert
        assertTrue((new ASTCompilationUnit(1)).getTypeDeclarations().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getTypeDeclarations()}
     */
    @Test
    void testGetTypeDeclarations2() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astCompilationUnit.getTypeDeclarations().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getTypeDeclarations()}
     */
    @Test
    void testGetTypeDeclarations3() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.setSymbolTable(mock(JSymbolTable.class));
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astCompilationUnit.getTypeDeclarations().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getTypeDeclarations()}
     */
    @Test
    void testGetTypeDeclarations4() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertTrue(astCompilationUnit.getTypeDeclarations().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getModuleDeclaration()}
     */
    @Test
    void testGetModuleDeclaration() {
        // Arrange, Act and Assert
        assertNull((new ASTCompilationUnit(1)).getModuleDeclaration());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getSymbolTable()}
     */
    @Test
    void testGetSymbolTable() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertSame(astCompilationUnit.symbolTable, astCompilationUnit.getSymbolTable());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getTypeSystem()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetTypeSystem() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.AssertionError: Type resolution not initialized
        //       at net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit.getTypeSystem(ASTCompilationUnit.java:154)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCompilationUnit astCompilationUnit = null;

        // Act
        TypeSystem actualTypeSystem = astCompilationUnit.getTypeSystem();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTCompilationUnit#getLazyTypeResolver()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetLazyTypeResolver() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.AssertionError: Type resolution not initialized
        //       at net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit.getLazyTypeResolver(ASTCompilationUnit.java:163)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTCompilationUnit astCompilationUnit = null;

        // Act
        LazyTypeResolver actualLazyTypeResolver = astCompilationUnit.getLazyTypeResolver();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTCompilationUnit#isSimpleCompilationUnit()}
     */
    @Test
    void testIsSimpleCompilationUnit() {
        // Arrange, Act and Assert
        assertFalse((new ASTCompilationUnit(1)).isSimpleCompilationUnit());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#isSimpleCompilationUnit()}
     */
    @Test
    void testIsSimpleCompilationUnit2() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astCompilationUnit.isSimpleCompilationUnit());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#isSimpleCompilationUnit()}
     */
    @Test
    void testIsSimpleCompilationUnit3() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.setSymbolTable(mock(JSymbolTable.class));
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astCompilationUnit.isSimpleCompilationUnit());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#isSimpleCompilationUnit()}
     */
    @Test
    void testIsSimpleCompilationUnit4() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertFalse(astCompilationUnit.isSimpleCompilationUnit());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#isSimpleCompilationUnit()}
     */
    @Test
    void testIsSimpleCompilationUnit5() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTImplicitClassDeclaration(1), 1);

        // Act and Assert
        assertTrue(astCompilationUnit.isSimpleCompilationUnit());
    }

    /**
     * Method under test: {@link ASTCompilationUnit#isSimpleCompilationUnit()}
     */
    @Test
    void testIsSimpleCompilationUnit6() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);
        astCompilationUnit.addChild(new ASTImplicitClassDeclaration(1), 0);

        // Act and Assert
        assertTrue(astCompilationUnit.isSimpleCompilationUnit());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTCompilationUnit#getAstInfo()}
     *   <li>{@link ASTCompilationUnit#getComments()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTCompilationUnit astCompilationUnit = new ASTCompilationUnit(1);

        // Act
        AstInfo<ASTCompilationUnit> actualAstInfo = astCompilationUnit.getAstInfo();

        // Assert
        assertNull(astCompilationUnit.getComments());
        assertNull(actualAstInfo);
    }

    /**
     * Method under test: {@link ASTCompilationUnit#ASTCompilationUnit(int)}
     */
    @Test
    void testNewASTCompilationUnit() {
        // Arrange and Act
        ASTCompilationUnit actualAstCompilationUnit = new ASTCompilationUnit(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstCompilationUnit.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        assertEquals("", actualAstCompilationUnit.getPackageName());
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("", nextResult.getValue());
        assertEquals("ClassBody", actualAstCompilationUnit.getXPathNodeName());
        assertEquals("PackageName", nextResult.getName());
        assertNull(actualAstCompilationUnit.getImage());
        assertNull(actualAstCompilationUnit.getComments());
        assertNull(actualAstCompilationUnit.getAstInfo());
        assertNull(actualAstCompilationUnit.getFirstChild());
        assertNull(actualAstCompilationUnit.getLastChild());
        assertNull(actualAstCompilationUnit.getNextSibling());
        assertNull(actualAstCompilationUnit.getPreviousSibling());
        assertNull(actualAstCompilationUnit.getFirstToken());
        assertNull(actualAstCompilationUnit.getLastToken());
        assertNull(actualAstCompilationUnit.getModuleDeclaration());
        assertNull(actualAstCompilationUnit.getPackageDeclaration());
        assertNull(actualAstCompilationUnit.getEnclosingType());
        assertEquals(0, actualAstCompilationUnit.getIndexInParent());
        assertEquals(0, actualAstCompilationUnit.getNumChildren());
        assertFalse(actualAstCompilationUnit.isFindBoundary());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstCompilationUnit.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstCompilationUnit.children().toList().isEmpty());
        assertSame(actualAstCompilationUnit, actualAstCompilationUnit.getRoot());
        assertSame(actualAstCompilationUnit, nextResult.getParent());
        assertSame(ancestorsResult, actualAstCompilationUnit.descendants());
        assertSame(ancestorsResult, actualAstCompilationUnit.getTypeDeclarations());
    }
}
