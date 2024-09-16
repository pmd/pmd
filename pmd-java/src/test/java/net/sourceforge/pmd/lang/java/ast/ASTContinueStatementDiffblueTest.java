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

class ASTContinueStatementDiffblueTest {
  /**
   * Method under test: {@link ASTContinueStatement#ASTContinueStatement(int)}
   */
  @Test
  void testNewASTContinueStatement() {
    // Arrange and Act
    ASTContinueStatement actualAstContinueStatement = new ASTContinueStatement(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstContinueStatement.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstContinueStatement.getXPathNodeName());
    assertEquals("Label", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstContinueStatement.getImage());
    assertNull(actualAstContinueStatement.getLabel());
    assertNull(actualAstContinueStatement.getFirstChild());
    assertNull(actualAstContinueStatement.getLastChild());
    assertNull(actualAstContinueStatement.getNextSibling());
    assertNull(actualAstContinueStatement.getPreviousSibling());
    assertNull(actualAstContinueStatement.getFirstToken());
    assertNull(actualAstContinueStatement.getLastToken());
    assertNull(actualAstContinueStatement.getTarget());
    assertNull(actualAstContinueStatement.getEnclosingType());
    assertNull(actualAstContinueStatement.symbolTable);
    assertEquals(0, actualAstContinueStatement.getIndexInParent());
    assertEquals(0, actualAstContinueStatement.getNumChildren());
    assertFalse(actualAstContinueStatement.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstContinueStatement.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstContinueStatement.children().toList().isEmpty());
    assertSame(actualAstContinueStatement, nextResult.getParent());
    assertSame(ancestorsResult, actualAstContinueStatement.descendants());
  }
}
