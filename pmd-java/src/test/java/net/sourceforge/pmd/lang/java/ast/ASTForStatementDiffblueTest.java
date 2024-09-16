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

class ASTForStatementDiffblueTest {
  /**
   * Method under test: {@link ASTForStatement#ASTForStatement(int)}
   */
  @Test
  void testNewASTForStatement() {
    // Arrange and Act
    ASTForStatement actualAstForStatement = new ASTForStatement(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstForStatement.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstForStatement.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstForStatement.getImage());
    assertNull(actualAstForStatement.getFirstChild());
    assertNull(actualAstForStatement.getLastChild());
    assertNull(actualAstForStatement.getNextSibling());
    assertNull(actualAstForStatement.getPreviousSibling());
    assertNull(actualAstForStatement.getFirstToken());
    assertNull(actualAstForStatement.getLastToken());
    assertNull(actualAstForStatement.getCondition());
    assertNull(actualAstForStatement.getInit());
    assertNull(actualAstForStatement.getBody());
    assertNull(actualAstForStatement.getUpdate());
    assertNull(actualAstForStatement.getEnclosingType());
    assertNull(actualAstForStatement.symbolTable);
    assertEquals(0, actualAstForStatement.getIndexInParent());
    assertEquals(0, actualAstForStatement.getNumChildren());
    assertFalse(actualAstForStatement.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstForStatement.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstForStatement.children().toList().isEmpty());
    assertSame(actualAstForStatement, nextResult.getParent());
    assertSame(ancestorsResult, actualAstForStatement.descendants());
  }
}
