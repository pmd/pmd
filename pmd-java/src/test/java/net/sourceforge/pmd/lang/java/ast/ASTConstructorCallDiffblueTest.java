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

class ASTConstructorCallDiffblueTest {
  /**
   * Method under test: {@link ASTConstructorCall#ASTConstructorCall(int)}
   */
  @Test
  void testNewASTConstructorCall() {
    // Arrange and Act
    ASTConstructorCall actualAstConstructorCall = new ASTConstructorCall(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstConstructorCall.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("AnonymousClass", nextResult.getName());
    assertEquals("ClassBody", actualAstConstructorCall.getXPathNodeName());
    assertEquals("new", actualAstConstructorCall.getMethodName());
    assertNull(actualAstConstructorCall.getImage());
    assertNull(actualAstConstructorCall.getFirstChild());
    assertNull(actualAstConstructorCall.getLastChild());
    assertNull(actualAstConstructorCall.getNextSibling());
    assertNull(actualAstConstructorCall.getPreviousSibling());
    assertNull(actualAstConstructorCall.getFirstToken());
    assertNull(actualAstConstructorCall.getLastToken());
    assertNull(actualAstConstructorCall.getAnonymousClassDeclaration());
    assertNull(actualAstConstructorCall.getArguments());
    assertNull(actualAstConstructorCall.getTypeNode());
    assertNull(actualAstConstructorCall.getQualifier());
    assertNull(actualAstConstructorCall.getExplicitTypeArguments());
    assertNull(actualAstConstructorCall.getEnclosingType());
    assertNull(actualAstConstructorCall.symbolTable);
    assertNull(actualAstConstructorCall.getTypeMirrorInternal());
    assertEquals(0, actualAstConstructorCall.getIndexInParent());
    assertEquals(0, actualAstConstructorCall.getNumChildren());
    assertFalse(actualAstConstructorCall.isFindBoundary());
    assertFalse(actualAstConstructorCall.isAnonymousClass());
    assertFalse(actualAstConstructorCall.isCompileTimeConstant());
    assertFalse(actualAstConstructorCall.isParenthesized());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstConstructorCall.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstConstructorCall.children().toList().isEmpty());
    assertTrue(actualAstConstructorCall.isExpression());
    String expectedStringValue = Boolean.FALSE.toString();
    assertEquals(expectedStringValue, nextResult.getStringValue());
    assertSame(actualAstConstructorCall, nextResult.getParent());
    assertSame(ancestorsResult, actualAstConstructorCall.descendants());
  }
}
