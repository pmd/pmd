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

class ASTArrayAllocationDiffblueTest {
  /**
   * Method under test: {@link ASTArrayAllocation#ASTArrayAllocation(int)}
   */
  @Test
  void testNewASTArrayAllocation() {
    // Arrange and Act
    ASTArrayAllocation actualAstArrayAllocation = new ASTArrayAllocation(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstArrayAllocation.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ArrayDepth", nextResult.getName());
    assertEquals("ClassBody", actualAstArrayAllocation.getXPathNodeName());
    assertNull(nextResult.getValue());
    assertNull(actualAstArrayAllocation.getImage());
    assertNull(actualAstArrayAllocation.getFirstChild());
    assertNull(actualAstArrayAllocation.getLastChild());
    assertNull(actualAstArrayAllocation.getNextSibling());
    assertNull(actualAstArrayAllocation.getPreviousSibling());
    assertNull(actualAstArrayAllocation.getFirstToken());
    assertNull(actualAstArrayAllocation.getLastToken());
    assertNull(actualAstArrayAllocation.getArrayInitializer());
    assertNull(actualAstArrayAllocation.getEnclosingType());
    assertNull(actualAstArrayAllocation.symbolTable);
    assertNull(actualAstArrayAllocation.getTypeMirrorInternal());
    assertEquals(0, actualAstArrayAllocation.getIndexInParent());
    assertEquals(0, actualAstArrayAllocation.getNumChildren());
    assertFalse(actualAstArrayAllocation.isFindBoundary());
    assertFalse(actualAstArrayAllocation.isCompileTimeConstant());
    assertFalse(actualAstArrayAllocation.isParenthesized());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstArrayAllocation.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstArrayAllocation.children().toList().isEmpty());
    assertTrue(actualAstArrayAllocation.isExpression());
    assertSame(actualAstArrayAllocation, nextResult.getParent());
    assertSame(ancestorsResult, actualAstArrayAllocation.descendants());
  }
}
