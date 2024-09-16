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

class ASTForInitDiffblueTest {
  /**
   * Method under test: {@link ASTForInit#ASTForInit(int)}
   */
  @Test
  void testNewASTForInit() {
    // Arrange and Act
    ASTForInit actualAstForInit = new ASTForInit(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstForInit.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstForInit.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstForInit.getImage());
    assertNull(actualAstForInit.getFirstChild());
    assertNull(actualAstForInit.getLastChild());
    assertNull(actualAstForInit.getNextSibling());
    assertNull(actualAstForInit.getPreviousSibling());
    assertNull(actualAstForInit.getFirstToken());
    assertNull(actualAstForInit.getLastToken());
    assertNull(actualAstForInit.getStatement());
    assertNull(actualAstForInit.getEnclosingType());
    assertNull(actualAstForInit.symbolTable);
    assertEquals(0, actualAstForInit.getIndexInParent());
    assertEquals(0, actualAstForInit.getNumChildren());
    assertFalse(actualAstForInit.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstForInit.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstForInit.children().toList().isEmpty());
    assertSame(actualAstForInit, nextResult.getParent());
    assertSame(ancestorsResult, actualAstForInit.descendants());
  }
}
