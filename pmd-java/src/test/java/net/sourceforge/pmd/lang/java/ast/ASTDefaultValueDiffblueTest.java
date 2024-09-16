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

class ASTDefaultValueDiffblueTest {
  /**
   * Method under test: {@link ASTDefaultValue#ASTDefaultValue(int)}
   */
  @Test
  void testNewASTDefaultValue() {
    // Arrange and Act
    ASTDefaultValue actualAstDefaultValue = new ASTDefaultValue(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstDefaultValue.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstDefaultValue.getXPathNodeName());
    assertEquals("Image", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstDefaultValue.getImage());
    assertNull(actualAstDefaultValue.getFirstChild());
    assertNull(actualAstDefaultValue.getLastChild());
    assertNull(actualAstDefaultValue.getNextSibling());
    assertNull(actualAstDefaultValue.getPreviousSibling());
    assertNull(actualAstDefaultValue.getFirstToken());
    assertNull(actualAstDefaultValue.getLastToken());
    assertNull(actualAstDefaultValue.getEnclosingType());
    assertNull(actualAstDefaultValue.symbolTable);
    assertEquals(0, actualAstDefaultValue.getIndexInParent());
    assertEquals(0, actualAstDefaultValue.getNumChildren());
    assertFalse(actualAstDefaultValue.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstDefaultValue.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstDefaultValue.children().toList().isEmpty());
    assertSame(actualAstDefaultValue, nextResult.getParent());
    assertSame(ancestorsResult, actualAstDefaultValue.descendants());
  }
}
