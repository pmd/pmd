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

class ASTConstructorDeclarationDiffblueTest {
  /**
   * Method under test:
   * {@link ASTConstructorDeclaration#ASTConstructorDeclaration(int)}
   */
  @Test
  void testNewASTConstructorDeclaration() {
    // Arrange and Act
    ASTConstructorDeclaration actualAstConstructorDeclaration = new ASTConstructorDeclaration(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstConstructorDeclaration.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstConstructorDeclaration.getXPathNodeName());
    assertEquals("Name", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstConstructorDeclaration.getImage());
    assertNull(actualAstConstructorDeclaration.getName());
    assertNull(actualAstConstructorDeclaration.getFirstChild());
    assertNull(actualAstConstructorDeclaration.getLastChild());
    assertNull(actualAstConstructorDeclaration.getNextSibling());
    assertNull(actualAstConstructorDeclaration.getPreviousSibling());
    assertNull(actualAstConstructorDeclaration.getFirstToken());
    assertNull(actualAstConstructorDeclaration.getLastToken());
    assertNull(actualAstConstructorDeclaration.getBody());
    assertNull(actualAstConstructorDeclaration.getFormalParameters());
    assertNull(actualAstConstructorDeclaration.getModifiers());
    assertNull(actualAstConstructorDeclaration.getThrowsList());
    assertNull(actualAstConstructorDeclaration.getEnclosingType());
    assertNull(actualAstConstructorDeclaration.getTypeParameters());
    assertNull(actualAstConstructorDeclaration.getJavadocComment());
    assertNull(actualAstConstructorDeclaration.symbolTable);
    assertEquals(0, actualAstConstructorDeclaration.getIndexInParent());
    assertEquals(0, actualAstConstructorDeclaration.getNumChildren());
    assertFalse(actualAstConstructorDeclaration.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstConstructorDeclaration.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstConstructorDeclaration.children().toList().isEmpty());
    assertSame(actualAstConstructorDeclaration, nextResult.getParent());
    assertSame(ancestorsResult, actualAstConstructorDeclaration.descendants());
  }
}
