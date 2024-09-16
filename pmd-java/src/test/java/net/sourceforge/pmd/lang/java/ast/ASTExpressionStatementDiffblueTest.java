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

class ASTExpressionStatementDiffblueTest {
  /**
   * Method under test: {@link ASTExpressionStatement#ASTExpressionStatement(int)}
   */
  @Test
  void testNewASTExpressionStatement() {
    // Arrange and Act
    ASTExpressionStatement actualAstExpressionStatement = new ASTExpressionStatement(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstExpressionStatement.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstExpressionStatement.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstExpressionStatement.getImage());
    assertNull(actualAstExpressionStatement.getFirstChild());
    assertNull(actualAstExpressionStatement.getLastChild());
    assertNull(actualAstExpressionStatement.getNextSibling());
    assertNull(actualAstExpressionStatement.getPreviousSibling());
    assertNull(actualAstExpressionStatement.getFirstToken());
    assertNull(actualAstExpressionStatement.getLastToken());
    assertNull(actualAstExpressionStatement.getEnclosingType());
    assertNull(actualAstExpressionStatement.symbolTable);
    assertEquals(0, actualAstExpressionStatement.getIndexInParent());
    assertEquals(0, actualAstExpressionStatement.getNumChildren());
    assertFalse(actualAstExpressionStatement.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstExpressionStatement.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstExpressionStatement.children().toList().isEmpty());
    assertSame(actualAstExpressionStatement, nextResult.getParent());
    assertSame(ancestorsResult, actualAstExpressionStatement.descendants());
  }
}
