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

class ASTCatchClauseDiffblueTest {
  /**
   * Method under test: {@link ASTCatchClause#ASTCatchClause(int)}
   */
  @Test
  void testNewASTCatchClause() {
    // Arrange and Act
    ASTCatchClause actualAstCatchClause = new ASTCatchClause(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstCatchClause.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstCatchClause.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstCatchClause.getImage());
    assertNull(actualAstCatchClause.getFirstChild());
    assertNull(actualAstCatchClause.getLastChild());
    assertNull(actualAstCatchClause.getNextSibling());
    assertNull(actualAstCatchClause.getPreviousSibling());
    assertNull(actualAstCatchClause.getFirstToken());
    assertNull(actualAstCatchClause.getLastToken());
    assertNull(actualAstCatchClause.getBody());
    assertNull(actualAstCatchClause.getParameter());
    assertNull(actualAstCatchClause.getEnclosingType());
    assertNull(actualAstCatchClause.symbolTable);
    assertEquals(0, actualAstCatchClause.getIndexInParent());
    assertEquals(0, actualAstCatchClause.getNumChildren());
    assertFalse(actualAstCatchClause.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstCatchClause.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstCatchClause.children().toList().isEmpty());
    assertSame(actualAstCatchClause, nextResult.getParent());
    assertSame(ancestorsResult, actualAstCatchClause.descendants());
  }
}
