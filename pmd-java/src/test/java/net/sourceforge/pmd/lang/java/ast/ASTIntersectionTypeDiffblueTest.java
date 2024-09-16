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

class ASTIntersectionTypeDiffblueTest {
  /**
   * Method under test: {@link ASTIntersectionType#ASTIntersectionType(int)}
   */
  @Test
  void testNewASTIntersectionType() {
    // Arrange and Act
    ASTIntersectionType actualAstIntersectionType = new ASTIntersectionType(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstIntersectionType.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstIntersectionType.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstIntersectionType.getImage());
    assertNull(actualAstIntersectionType.getNextSibling());
    assertNull(actualAstIntersectionType.getPreviousSibling());
    assertNull(actualAstIntersectionType.getFirstToken());
    assertNull(actualAstIntersectionType.getLastToken());
    assertNull(actualAstIntersectionType.getEnclosingType());
    assertNull(actualAstIntersectionType.symbolTable);
    assertNull(actualAstIntersectionType.getTypeMirrorInternal());
    assertEquals(0, actualAstIntersectionType.getIndexInParent());
    assertEquals(0, actualAstIntersectionType.getNumChildren());
    assertFalse(actualAstIntersectionType.isFindBoundary());
    assertFalse(actualAstIntersectionType.isVoid());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstIntersectionType.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstIntersectionType.children().toList().isEmpty());
    assertSame(actualAstIntersectionType, nextResult.getParent());
    assertSame(ancestorsResult, actualAstIntersectionType.descendants());
    assertSame(ancestorsResult, actualAstIntersectionType.getComponents());
    assertSame(ancestorsResult, actualAstIntersectionType.getDeclaredAnnotations());
  }
}
