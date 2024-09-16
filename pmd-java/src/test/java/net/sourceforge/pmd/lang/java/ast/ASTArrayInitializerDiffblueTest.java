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

class ASTArrayInitializerDiffblueTest {
  /**
   * Method under test: {@link ASTArrayInitializer#ASTArrayInitializer(int)}
   */
  @Test
  void testNewASTArrayInitializer() {
    // Arrange and Act
    ASTArrayInitializer actualAstArrayInitializer = new ASTArrayInitializer(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstArrayInitializer.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("0", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstArrayInitializer.getXPathNodeName());
    assertEquals("Length", nextResult.getName());
    assertNull(actualAstArrayInitializer.getImage());
    assertNull(actualAstArrayInitializer.getFirstChild());
    assertNull(actualAstArrayInitializer.getLastChild());
    assertNull(actualAstArrayInitializer.getNextSibling());
    assertNull(actualAstArrayInitializer.getPreviousSibling());
    assertNull(actualAstArrayInitializer.getFirstToken());
    assertNull(actualAstArrayInitializer.getLastToken());
    assertNull(actualAstArrayInitializer.getEnclosingType());
    assertNull(actualAstArrayInitializer.symbolTable);
    assertNull(actualAstArrayInitializer.getTypeMirrorInternal());
    assertEquals(0, actualAstArrayInitializer.getIndexInParent());
    assertEquals(0, actualAstArrayInitializer.getNumChildren());
    assertEquals(0, actualAstArrayInitializer.length());
    assertFalse(actualAstArrayInitializer.isFindBoundary());
    assertFalse(actualAstArrayInitializer.isCompileTimeConstant());
    assertFalse(actualAstArrayInitializer.isParenthesized());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstArrayInitializer.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstArrayInitializer.children().toList().isEmpty());
    assertTrue(actualAstArrayInitializer.isExpression());
    assertSame(actualAstArrayInitializer, nextResult.getParent());
    assertSame(ancestorsResult, actualAstArrayInitializer.descendants());
  }
}
