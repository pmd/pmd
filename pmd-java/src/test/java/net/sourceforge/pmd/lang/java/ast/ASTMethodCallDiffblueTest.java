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

class ASTMethodCallDiffblueTest {
  /**
   * Method under test: {@link ASTMethodCall#ASTMethodCall(int)}
   */
  @Test
  void testNewASTMethodCall() {
    // Arrange and Act
    ASTMethodCall actualAstMethodCall = new ASTMethodCall(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstMethodCall.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstMethodCall.getXPathNodeName());
    assertEquals("MethodName", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstMethodCall.getImage());
    assertNull(actualAstMethodCall.getMethodName());
    assertNull(actualAstMethodCall.getFirstChild());
    assertNull(actualAstMethodCall.getLastChild());
    assertNull(actualAstMethodCall.getNextSibling());
    assertNull(actualAstMethodCall.getPreviousSibling());
    assertNull(actualAstMethodCall.getFirstToken());
    assertNull(actualAstMethodCall.getLastToken());
    assertNull(actualAstMethodCall.getQualifier());
    assertNull(actualAstMethodCall.getExplicitTypeArguments());
    assertNull(actualAstMethodCall.getEnclosingType());
    assertNull(actualAstMethodCall.symbolTable);
    assertNull(actualAstMethodCall.getTypeMirrorInternal());
    assertEquals(0, actualAstMethodCall.getIndexInParent());
    assertEquals(0, actualAstMethodCall.getNumChildren());
    assertFalse(actualAstMethodCall.isFindBoundary());
    assertFalse(actualAstMethodCall.isCompileTimeConstant());
    assertFalse(actualAstMethodCall.isParenthesized());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstMethodCall.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstMethodCall.children().toList().isEmpty());
    assertTrue(actualAstMethodCall.isExpression());
    assertSame(actualAstMethodCall, nextResult.getParent());
    assertSame(ancestorsResult, actualAstMethodCall.descendants());
  }
}
