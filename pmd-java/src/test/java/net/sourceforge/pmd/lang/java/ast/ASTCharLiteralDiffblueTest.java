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

class ASTCharLiteralDiffblueTest {
  /**
   * Method under test: {@link ASTCharLiteral#ASTCharLiteral(int)}
   */
  @Test
  void testNewASTCharLiteral() {
    // Arrange and Act
    ASTCharLiteral actualAstCharLiteral = new ASTCharLiteral(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstCharLiteral.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstCharLiteral.getXPathNodeName());
    assertEquals("LiteralText", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstCharLiteral.getImage());
    assertNull(actualAstCharLiteral.getFirstChild());
    assertNull(actualAstCharLiteral.getLastChild());
    assertNull(actualAstCharLiteral.getNextSibling());
    assertNull(actualAstCharLiteral.getPreviousSibling());
    assertNull(actualAstCharLiteral.getFirstToken());
    assertNull(actualAstCharLiteral.getLastToken());
    assertNull(actualAstCharLiteral.getEnclosingType());
    assertNull(actualAstCharLiteral.symbolTable);
    assertNull(actualAstCharLiteral.getTypeMirrorInternal());
    assertEquals(0, actualAstCharLiteral.getIndexInParent());
    assertEquals(0, actualAstCharLiteral.getNumChildren());
    assertFalse(actualAstCharLiteral.isFindBoundary());
    assertFalse(actualAstCharLiteral.isParenthesized());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstCharLiteral.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstCharLiteral.children().toList().isEmpty());
    assertTrue(actualAstCharLiteral.isExpression());
    assertSame(actualAstCharLiteral, nextResult.getParent());
    assertSame(ancestorsResult, actualAstCharLiteral.descendants());
  }
}
