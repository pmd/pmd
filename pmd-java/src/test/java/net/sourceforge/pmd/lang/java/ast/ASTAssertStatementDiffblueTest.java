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

class ASTAssertStatementDiffblueTest {
  /**
   * Method under test: {@link ASTAssertStatement#ASTAssertStatement(int)}
   */
  @Test
  void testNewASTAssertStatement() {
    // Arrange and Act
    ASTAssertStatement actualAstAssertStatement = new ASTAssertStatement(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstAssertStatement.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    assertEquals("ClassBody", actualAstAssertStatement.getXPathNodeName());
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("DetailMessage", nextResult.getName());
    assertNull(actualAstAssertStatement.getImage());
    assertNull(actualAstAssertStatement.getFirstChild());
    assertNull(actualAstAssertStatement.getLastChild());
    assertNull(actualAstAssertStatement.getNextSibling());
    assertNull(actualAstAssertStatement.getPreviousSibling());
    assertNull(actualAstAssertStatement.getFirstToken());
    assertNull(actualAstAssertStatement.getLastToken());
    assertNull(actualAstAssertStatement.getDetailMessageNode());
    assertNull(actualAstAssertStatement.getEnclosingType());
    assertNull(actualAstAssertStatement.symbolTable);
    assertEquals(0, actualAstAssertStatement.getIndexInParent());
    assertEquals(0, actualAstAssertStatement.getNumChildren());
    assertFalse(actualAstAssertStatement.isFindBoundary());
    assertFalse(actualAstAssertStatement.hasDetailMessage());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstAssertStatement.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstAssertStatement.children().toList().isEmpty());
    String expectedStringValue = Boolean.FALSE.toString();
    assertEquals(expectedStringValue, nextResult.getStringValue());
    assertSame(actualAstAssertStatement, nextResult.getParent());
    assertSame(ancestorsResult, actualAstAssertStatement.descendants());
  }
}
