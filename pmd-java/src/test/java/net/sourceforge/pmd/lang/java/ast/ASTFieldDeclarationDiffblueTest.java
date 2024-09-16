package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Iterator;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;
import org.junit.jupiter.api.Test;

class ASTFieldDeclarationDiffblueTest {
  /**
   * Method under test: {@link ASTFieldDeclaration#ASTFieldDeclaration(int)}
   */
  @Test
  void testNewASTFieldDeclaration() {
    // Arrange and Act
    ASTFieldDeclaration actualAstFieldDeclaration = new ASTFieldDeclaration(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstFieldDeclaration.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstFieldDeclaration.getXPathNodeName());
    assertEquals("Static", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstFieldDeclaration.getImage());
    assertNull(actualAstFieldDeclaration.getFirstChild());
    assertNull(actualAstFieldDeclaration.getLastChild());
    assertNull(actualAstFieldDeclaration.getNextSibling());
    assertNull(actualAstFieldDeclaration.getPreviousSibling());
    assertNull(actualAstFieldDeclaration.getFirstToken());
    assertNull(actualAstFieldDeclaration.getLastToken());
    assertNull(actualAstFieldDeclaration.getModifiers());
    assertNull(actualAstFieldDeclaration.getTypeNode());
    assertNull(actualAstFieldDeclaration.getEnclosingType());
    assertNull(actualAstFieldDeclaration.getJavadocComment());
    assertNull(actualAstFieldDeclaration.symbolTable);
    assertEquals(0, actualAstFieldDeclaration.getIndexInParent());
    assertEquals(0, actualAstFieldDeclaration.getNumChildren());
    assertFalse(actualAstFieldDeclaration.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstFieldDeclaration.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstFieldDeclaration.children().toList().isEmpty());
    assertSame(actualAstFieldDeclaration, nextResult.getParent());
    assertSame(ancestorsResult, actualAstFieldDeclaration.descendants());
    assertSame(ancestorsResult, actualAstFieldDeclaration.getVarIds());
  }
}
