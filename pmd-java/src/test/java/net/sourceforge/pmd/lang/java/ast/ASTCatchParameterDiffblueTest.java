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

class ASTCatchParameterDiffblueTest {
  /**
   * Method under test: {@link ASTCatchParameter#ASTCatchParameter(int)}
   */
  @Test
  void testNewASTCatchParameter() {
    // Arrange and Act
    ASTCatchParameter actualAstCatchParameter = new ASTCatchParameter(1);

    // Assert
    Iterator<Attribute> xPathAttributesIterator = actualAstCatchParameter.getXPathAttributesIterator();
    assertTrue(xPathAttributesIterator instanceof AttributeAxisIterator);
    Attribute nextResult = xPathAttributesIterator.next();
    assertEquals("", nextResult.getStringValue());
    assertEquals("ClassBody", actualAstCatchParameter.getXPathNodeName());
    assertEquals("Name", nextResult.getName());
    assertNull(nextResult.getValue());
    assertNull(actualAstCatchParameter.getImage());
    assertNull(actualAstCatchParameter.getFirstChild());
    assertNull(actualAstCatchParameter.getLastChild());
    assertNull(actualAstCatchParameter.getNextSibling());
    assertNull(actualAstCatchParameter.getPreviousSibling());
    assertNull(actualAstCatchParameter.getFirstToken());
    assertNull(actualAstCatchParameter.getLastToken());
    assertNull(actualAstCatchParameter.getModifiers());
    assertNull(actualAstCatchParameter.getEnclosingType());
    assertNull(actualAstCatchParameter.getVarId());
    assertNull(actualAstCatchParameter.symbolTable);
    assertEquals(0, actualAstCatchParameter.getIndexInParent());
    assertEquals(0, actualAstCatchParameter.getNumChildren());
    assertFalse(actualAstCatchParameter.isFindBoundary());
    assertTrue(xPathAttributesIterator.hasNext());
    NodeStream<? extends Node> ancestorsResult = actualAstCatchParameter.ancestors();
    assertTrue(ancestorsResult.toList().isEmpty());
    assertTrue(actualAstCatchParameter.children().toList().isEmpty());
    assertSame(actualAstCatchParameter, nextResult.getParent());
    assertSame(ancestorsResult, actualAstCatchParameter.descendants());
  }
}
