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

class ASTClassLiteralDiffblueTest {
  /**
   * Method under test: {@link ASTClassLiteral#ASTClassLiteral(int)}
   */
  @Test
  void testNewASTClassLiteral() {
    // Arrange and Act
    ASTClassLiteral actualAstClassLiteral = new ASTClassLiteral(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstClassLiteral.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("0", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstClassLiteral.getXPathNodeName());
    assertEquals("ParenthesisDepth", nextResult.getName());
    assertNull(actualAstClassLiteral.getImage());
    assertNull(actualAstClassLiteral.getFirstChild());
    assertNull(actualAstClassLiteral.getLastChild());
    assertNull(actualAstClassLiteral.getNextSibling());
    assertNull(actualAstClassLiteral.getPreviousSibling());
    assertNull(actualAstClassLiteral.getFirstToken());
    assertNull(actualAstClassLiteral.getLastToken());
    assertNull(actualAstClassLiteral.getEnclosingType());
    assertNull(actualAstClassLiteral.symbolTable);
    assertNull(actualAstClassLiteral.getTypeMirrorInternal());
    assertEquals(0, actualAstClassLiteral.getIndexInParent());
    assertEquals(0, actualAstClassLiteral.getNumChildren());
    assertFalse(actualAstClassLiteral.isFindBoundary());
    assertFalse(actualAstClassLiteral.isCompileTimeConstant());
    assertFalse(actualAstClassLiteral.isParenthesized());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstClassLiteral.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstClassLiteral.children().toList().isEmpty());
    assertTrue(actualAstClassLiteral.isExpression());
    assertSame(actualAstClassLiteral, nextResult.getParent());
    assertSame(ancestorsResult, actualAstClassLiteral.descendants());
  }
}
