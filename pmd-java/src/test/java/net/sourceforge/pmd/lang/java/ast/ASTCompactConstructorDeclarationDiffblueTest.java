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

class ASTCompactConstructorDeclarationDiffblueTest {
  /**
   * Method under test:
   * {@link ASTCompactConstructorDeclaration#ASTCompactConstructorDeclaration(int)}
   */
  @Test
  void testNewASTCompactConstructorDeclaration() {
    // Arrange and Act
    ASTCompactConstructorDeclaration actualAstCompactConstructorDeclaration = new ASTCompactConstructorDeclaration(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstCompactConstructorDeclaration.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstCompactConstructorDeclaration.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstCompactConstructorDeclaration.getImage());
    assertNull(actualAstCompactConstructorDeclaration.getFirstChild());
    assertNull(actualAstCompactConstructorDeclaration.getLastChild());
    assertNull(actualAstCompactConstructorDeclaration.getNextSibling());
    assertNull(actualAstCompactConstructorDeclaration.getPreviousSibling());
    assertNull(actualAstCompactConstructorDeclaration.getFirstToken());
    assertNull(actualAstCompactConstructorDeclaration.getLastToken());
    assertNull(actualAstCompactConstructorDeclaration.getBody());
    assertNull(actualAstCompactConstructorDeclaration.getModifiers());
    assertNull(actualAstCompactConstructorDeclaration.getEnclosingType());
    assertNull(actualAstCompactConstructorDeclaration.symbolTable);
    assertEquals(0, actualAstCompactConstructorDeclaration.getIndexInParent());
    assertEquals(0, actualAstCompactConstructorDeclaration.getNumChildren());
    assertFalse(actualAstCompactConstructorDeclaration.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstCompactConstructorDeclaration.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstCompactConstructorDeclaration.children().toList().isEmpty());
    assertSame(actualAstCompactConstructorDeclaration, nextResult.getParent());
    assertSame(ancestorsResult, actualAstCompactConstructorDeclaration.descendants());
  }
}
