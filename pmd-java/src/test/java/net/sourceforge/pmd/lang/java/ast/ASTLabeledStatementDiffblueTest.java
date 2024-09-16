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

class ASTLabeledStatementDiffblueTest {
  /**
   * Method under test: {@link ASTLabeledStatement#ASTLabeledStatement(int)}
   */
  @Test
  void testNewASTLabeledStatement() {
    // Arrange and Act
    ASTLabeledStatement actualAstLabeledStatement = new ASTLabeledStatement(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstLabeledStatement.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstLabeledStatement.getXPathNodeName());
    assertEquals("Label", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstLabeledStatement.getImage());
    assertNull(actualAstLabeledStatement.getLabel());
    assertNull(actualAstLabeledStatement.getFirstChild());
    assertNull(actualAstLabeledStatement.getLastChild());
    assertNull(actualAstLabeledStatement.getNextSibling());
    assertNull(actualAstLabeledStatement.getPreviousSibling());
    assertNull(actualAstLabeledStatement.getFirstToken());
    assertNull(actualAstLabeledStatement.getLastToken());
    assertNull(actualAstLabeledStatement.getEnclosingType());
    assertNull(actualAstLabeledStatement.symbolTable);
    assertEquals(0, actualAstLabeledStatement.getIndexInParent());
    assertEquals(0, actualAstLabeledStatement.getNumChildren());
    assertFalse(actualAstLabeledStatement.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstLabeledStatement.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstLabeledStatement.children().toList().isEmpty());
    assertSame(actualAstLabeledStatement, nextResult.getParent());
    assertSame(ancestorsResult, actualAstLabeledStatement.descendants());
  }
}
