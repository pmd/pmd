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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTImportDeclarationDiffblueTest {
    /**
     * Method under test: {@link ASTImportDeclaration#getImportedName()}
     */
    @Test
    void testGetImportedName() {
        // Arrange, Act and Assert
        assertNull((new ASTImportDeclaration(1)).getImportedName());
    }

    /**
     * Method under test: {@link ASTImportDeclaration#getImportedName()}
     */
    @Test
    void testGetImportedName2() {
        // Arrange
        ASTImportDeclaration astImportDeclaration = new ASTImportDeclaration(1);
        astImportDeclaration.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astImportDeclaration.getImportedName());
    }

    /**
     * Method under test: {@link ASTImportDeclaration#getImportedSimpleName()}
     */
    @Test
    void testGetImportedSimpleName() {
        // Arrange
        ASTImportDeclaration astImportDeclaration = new ASTImportDeclaration(1);
        astImportDeclaration.setImage("Image");

        // Act and Assert
        assertEquals("Image", astImportDeclaration.getImportedSimpleName());
    }

    /**
     * Method under test: {@link ASTImportDeclaration#getPackageName()}
     */
    @Test
    void testGetPackageName() {
        // Arrange
        ASTImportDeclaration astImportDeclaration = new ASTImportDeclaration(1);
        astImportDeclaration.setImage("Image");

        // Act and Assert
        assertEquals("", astImportDeclaration.getPackageName());
    }

    /**
     * Method under test: {@link ASTImportDeclaration#getPackageName()}
     */
    @Test
    void testGetPackageName2() {
        // Arrange
        ASTImportDeclaration astImportDeclaration = new ASTImportDeclaration(1);
        astImportDeclaration.setImage("net.sourceforge.pmd.lang.java.ast.AbstractJavaNode");

        // Act and Assert
        assertEquals("net.sourceforge.pmd.lang.java.ast", astImportDeclaration.getPackageName());
    }

    /**
     * Method under test:
     * {@link ASTImportDeclaration#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTImportDeclaration astImportDeclaration = new ASTImportDeclaration(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTImportDeclaration>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astImportDeclaration.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTImportDeclaration.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link ASTImportDeclaration#setImportOnDemand()}
     *   <li>{@link ASTImportDeclaration#setModuleImport()}
     *   <li>{@link ASTImportDeclaration#setStatic()}
     *   <li>{@link ASTImportDeclaration#getImage()}
     *   <li>{@link ASTImportDeclaration#isImportOnDemand()}
     *   <li>{@link ASTImportDeclaration#isModuleImport()}
     *   <li>{@link ASTImportDeclaration#isStatic()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        ASTImportDeclaration astImportDeclaration = new ASTImportDeclaration(1);

        // Act
        astImportDeclaration.setImportOnDemand();
        astImportDeclaration.setModuleImport();
        astImportDeclaration.setStatic();
        astImportDeclaration.getImage();
        boolean actualIsImportOnDemandResult = astImportDeclaration.isImportOnDemand();
        boolean actualIsModuleImportResult = astImportDeclaration.isModuleImport();

        // Assert that nothing has changed
        assertTrue(actualIsImportOnDemandResult);
        assertTrue(actualIsModuleImportResult);
        assertTrue(astImportDeclaration.isStatic());
    }

    /**
     * Method under test: {@link ASTImportDeclaration#ASTImportDeclaration(int)}
     */
    @Test
    void testNewASTImportDeclaration() {
        // Arrange and Act
        ASTImportDeclaration actualAstImportDeclaration = new ASTImportDeclaration(1);

        // Assert
        Iterator<Attribute> xPathAttributesIterator = actualAstImportDeclaration.getXPathAttributesIterator();
        assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
        Attribute nextResult = xPathAttributesIterator.next();
        assertEquals("", nextResult.getStringValue());
        assertEquals("ClassBody", actualAstImportDeclaration.getXPathNodeName());
        assertEquals("PackageName", nextResult.getName());
        assertNull(nextResult.getValue());
        assertNull(actualAstImportDeclaration.getImage());
        assertNull(actualAstImportDeclaration.getImportedName());
        assertNull(actualAstImportDeclaration.getFirstChild());
        assertNull(actualAstImportDeclaration.getLastChild());
        assertNull(actualAstImportDeclaration.getNextSibling());
        assertNull(actualAstImportDeclaration.getPreviousSibling());
        assertNull(actualAstImportDeclaration.getFirstToken());
        assertNull(actualAstImportDeclaration.getLastToken());
        assertNull(actualAstImportDeclaration.getEnclosingType());
        assertNull(actualAstImportDeclaration.symbolTable);
        assertEquals(0, actualAstImportDeclaration.getIndexInParent());
        assertEquals(0, actualAstImportDeclaration.getNumChildren());
        assertFalse(actualAstImportDeclaration.isFindBoundary());
        assertFalse(actualAstImportDeclaration.isImportOnDemand());
        assertFalse(actualAstImportDeclaration.isModuleImport());
        assertFalse(actualAstImportDeclaration.isStatic());
        assertTrue(xPathAttributesIterator.hasNext());
        NodeStream<? extends Node> ancestorsResult = actualAstImportDeclaration.ancestors();
        assertTrue(ancestorsResult.toList().isEmpty());
        assertTrue(actualAstImportDeclaration.children().toList().isEmpty());
        assertSame(actualAstImportDeclaration, nextResult.getParent());
        assertSame(ancestorsResult, actualAstImportDeclaration.descendants());
    }
}
