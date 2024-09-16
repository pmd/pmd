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

class ASTForeachStatementDiffblueTest {
  /**
   * Method under test: {@link ASTForeachStatement#ASTForeachStatement(int)}
   */
  @Test
  void testNewASTForeachStatement() {
    // Arrange and Act
    ASTForeachStatement actualAstForeachStatement = new ASTForeachStatement(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstForeachStatement.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstForeachStatement.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstForeachStatement.getImage());
    assertNull(actualAstForeachStatement.getFirstChild());
    assertNull(actualAstForeachStatement.getLastChild());
    assertNull(actualAstForeachStatement.getNextSibling());
    assertNull(actualAstForeachStatement.getPreviousSibling());
    assertNull(actualAstForeachStatement.getFirstToken());
    assertNull(actualAstForeachStatement.getLastToken());
    assertNull(actualAstForeachStatement.getIterableExpr());
    assertNull(actualAstForeachStatement.getCondition());
    assertNull(actualAstForeachStatement.getBody());
    assertNull(actualAstForeachStatement.getEnclosingType());
    assertNull(actualAstForeachStatement.symbolTable);
    assertEquals(0, actualAstForeachStatement.getIndexInParent());
    assertEquals(0, actualAstForeachStatement.getNumChildren());
    assertFalse(actualAstForeachStatement.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstForeachStatement.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstForeachStatement.children().toList().isEmpty());
    assertSame(actualAstForeachStatement, nextResult.getParent());
    assertSame(ancestorsResult, actualAstForeachStatement.descendants());
  }
}
