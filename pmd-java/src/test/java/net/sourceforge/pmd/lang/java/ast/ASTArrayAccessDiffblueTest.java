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

class ASTArrayAccessDiffblueTest {
  /**
   * Method under test: {@link ASTArrayAccess#ASTArrayAccess(int)}
   */
  @Test
  void testNewASTArrayAccess() {
    // Arrange and Act
    ASTArrayAccess actualAstArrayAccess = new ASTArrayAccess(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstArrayAccess.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("0", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstArrayAccess.getXPathNodeName());
    assertEquals("ParenthesisDepth", nextResult.getName());
    assertNull(actualAstArrayAccess.getImage());
    assertNull(actualAstArrayAccess.getFirstChild());
    assertNull(actualAstArrayAccess.getLastChild());
    assertNull(actualAstArrayAccess.getNextSibling());
    assertNull(actualAstArrayAccess.getPreviousSibling());
    assertNull(actualAstArrayAccess.getFirstToken());
    assertNull(actualAstArrayAccess.getLastToken());
    assertNull(actualAstArrayAccess.getEnclosingType());
    assertNull(actualAstArrayAccess.symbolTable);
    assertNull(actualAstArrayAccess.getTypeMirrorInternal());
    assertEquals(0, actualAstArrayAccess.getIndexInParent());
    assertEquals(0, actualAstArrayAccess.getNumChildren());
    assertEquals(ASTAssignableExpr.AccessType.READ, actualAstArrayAccess.getAccessType());
    assertFalse(actualAstArrayAccess.isFindBoundary());
    assertFalse(actualAstArrayAccess.isCompileTimeConstant());
    assertFalse(actualAstArrayAccess.isParenthesized());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstArrayAccess.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstArrayAccess.children().toList().isEmpty());
    assertTrue(actualAstArrayAccess.isExpression());
    assertSame(actualAstArrayAccess, nextResult.getParent());
    assertSame(ancestorsResult, actualAstArrayAccess.descendants());
  }
}
