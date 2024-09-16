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

class ASTForUpdateDiffblueTest {
  /**
   * Method under test: {@link ASTForUpdate#ASTForUpdate(int)}
   */
  @Test
  void testNewASTForUpdate() {
    // Arrange and Act
    ASTForUpdate actualAstForUpdate = new ASTForUpdate(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstForUpdate.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstForUpdate.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstForUpdate.getImage());
    assertNull(actualAstForUpdate.getFirstChild());
    assertNull(actualAstForUpdate.getLastChild());
    assertNull(actualAstForUpdate.getNextSibling());
    assertNull(actualAstForUpdate.getPreviousSibling());
    assertNull(actualAstForUpdate.getFirstToken());
    assertNull(actualAstForUpdate.getLastToken());
    assertNull(actualAstForUpdate.getEnclosingType());
    assertNull(actualAstForUpdate.symbolTable);
    assertEquals(0, actualAstForUpdate.getIndexInParent());
    assertEquals(0, actualAstForUpdate.getNumChildren());
    assertFalse(actualAstForUpdate.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstForUpdate.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstForUpdate.children().toList().isEmpty());
    assertSame(actualAstForUpdate, nextResult.getParent());
    assertSame(ancestorsResult, actualAstForUpdate.descendants());
  }
}
