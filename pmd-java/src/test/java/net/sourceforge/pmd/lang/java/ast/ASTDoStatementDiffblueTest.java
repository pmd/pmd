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

class ASTDoStatementDiffblueTest {
  /**
   * Method under test: {@link ASTDoStatement#ASTDoStatement(int)}
   */
  @Test
  void testNewASTDoStatement() {
    // Arrange and Act
    ASTDoStatement actualAstDoStatement = new ASTDoStatement(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstDoStatement.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstDoStatement.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstDoStatement.getImage());
    assertNull(actualAstDoStatement.getFirstChild());
    assertNull(actualAstDoStatement.getLastChild());
    assertNull(actualAstDoStatement.getNextSibling());
    assertNull(actualAstDoStatement.getPreviousSibling());
    assertNull(actualAstDoStatement.getFirstToken());
    assertNull(actualAstDoStatement.getLastToken());
    assertNull(actualAstDoStatement.getEnclosingType());
    assertNull(actualAstDoStatement.symbolTable);
    assertEquals(0, actualAstDoStatement.getIndexInParent());
    assertEquals(0, actualAstDoStatement.getNumChildren());
    assertFalse(actualAstDoStatement.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstDoStatement.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstDoStatement.children().toList().isEmpty());
    assertSame(actualAstDoStatement, nextResult.getParent());
    assertSame(ancestorsResult, actualAstDoStatement.descendants());
  }
}
