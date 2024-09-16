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

class ASTGuardDiffblueTest {
  /**
   * Method under test: {@link ASTGuard#ASTGuard(int)}
   */
  @Test
  void testNewASTGuard() {
    // Arrange and Act
    ASTGuard actualAstGuard = new ASTGuard(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstGuard.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstGuard.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstGuard.getImage());
    assertNull(actualAstGuard.getFirstChild());
    assertNull(actualAstGuard.getLastChild());
    assertNull(actualAstGuard.getNextSibling());
    assertNull(actualAstGuard.getPreviousSibling());
    assertNull(actualAstGuard.getFirstToken());
    assertNull(actualAstGuard.getLastToken());
    assertNull(actualAstGuard.getEnclosingType());
    assertNull(actualAstGuard.symbolTable);
    assertEquals(0, actualAstGuard.getIndexInParent());
    assertEquals(0, actualAstGuard.getNumChildren());
    assertFalse(actualAstGuard.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstGuard.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstGuard.children().toList().isEmpty());
    assertSame(actualAstGuard, nextResult.getParent());
    assertSame(ancestorsResult, actualAstGuard.descendants());
  }
}
