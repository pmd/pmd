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

class ASTFinallyClauseDiffblueTest {
  /**
   * Method under test: {@link ASTFinallyClause#ASTFinallyClause(int)}
   */
  @Test
  void testNewASTFinallyClause() {
    // Arrange and Act
    ASTFinallyClause actualAstFinallyClause = new ASTFinallyClause(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstFinallyClause.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstFinallyClause.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstFinallyClause.getImage());
    assertNull(actualAstFinallyClause.getFirstChild());
    assertNull(actualAstFinallyClause.getLastChild());
    assertNull(actualAstFinallyClause.getNextSibling());
    assertNull(actualAstFinallyClause.getPreviousSibling());
    assertNull(actualAstFinallyClause.getFirstToken());
    assertNull(actualAstFinallyClause.getLastToken());
    assertNull(actualAstFinallyClause.getEnclosingType());
    assertNull(actualAstFinallyClause.symbolTable);
    assertEquals(0, actualAstFinallyClause.getIndexInParent());
    assertEquals(0, actualAstFinallyClause.getNumChildren());
    assertFalse(actualAstFinallyClause.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstFinallyClause.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstFinallyClause.children().toList().isEmpty());
    assertSame(actualAstFinallyClause, nextResult.getParent());
    assertSame(ancestorsResult, actualAstFinallyClause.descendants());
  }
}
