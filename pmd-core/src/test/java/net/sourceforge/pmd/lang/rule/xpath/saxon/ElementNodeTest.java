/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.saxon;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.DocumentNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;

import net.sf.saxon.type.Type;

public class ElementNodeTest {

    @Test
    public void testCompareOrder() {
        DummyNode node = new DummyNode(1, false, "dummy");
        DummyNode foo1 = new DummyNode(2, false, "foo");
        foo1.testingOnlySetBeginLine(1);
        DummyNode foo2 = new DummyNode(2, false, "foo");
        foo2.testingOnlySetBeginLine(2);
        node.jjtAddChild(foo1, 0);
        node.jjtAddChild(foo2, 1);

        DocumentNode document = new DocumentNode(node);
        ElementNode elementFoo1 = document.nodeToElementNode.get(foo1);
        ElementNode elementFoo2 = document.nodeToElementNode.get(foo2);

        Assert.assertFalse(elementFoo1.isSameNodeInfo(elementFoo2));
        Assert.assertFalse(elementFoo2.isSameNodeInfo(elementFoo1));
        Assert.assertTrue(elementFoo1.compareOrder(elementFoo2) < 0);
        Assert.assertTrue(elementFoo2.compareOrder(elementFoo1) > 0);

        Assert.assertEquals(0, elementFoo1.compareOrder(elementFoo1));
    }

    @Test
    public void testCompareOrderSamePosition() {
        DummyNode node = new DummyNode(1, false, "dummy");
        DummyNode foo1 = new DummyNode(2, false, "foo");
        foo1.testingOnlySetBeginLine(1);
        foo1.testingOnlySetBeginColumn(1);
        DummyNode foo2 = new DummyNode(2, false, "foo");
        foo2.testingOnlySetBeginLine(1);
        foo2.testingOnlySetBeginColumn(1);
        node.jjtAddChild(foo1, 0);
        node.jjtAddChild(foo2, 1);

        DocumentNode document = new DocumentNode(node);
        ElementNode elementFoo1 = document.nodeToElementNode.get(foo1);
        ElementNode elementFoo2 = document.nodeToElementNode.get(foo2);

        Assert.assertFalse(elementFoo1.isSameNodeInfo(elementFoo2));
        Assert.assertFalse(elementFoo2.isSameNodeInfo(elementFoo1));
        Assert.assertTrue(elementFoo1.compareOrder(elementFoo2) < 0);
        Assert.assertTrue(elementFoo2.compareOrder(elementFoo1) > 0);
    }

    @Test
    public void verifyTextNodeType() {
        DummyNode node = new DummyNode(1, false, "dummy");
        DummyNode foo1 = new DummyNode(2, false, "foo");
        DummyNode foo2 = new DummyNode(2, false, "#text");
        node.jjtAddChild(foo1, 0);
        node.jjtAddChild(foo2, 1);

        DocumentNode document = new DocumentNode(node);
        ElementNode elementFoo1 = document.nodeToElementNode.get(foo1);
        ElementNode elementFoo2 = document.nodeToElementNode.get(foo2);

        Assert.assertEquals(Type.ELEMENT, elementFoo1.getNodeKind());
        Assert.assertEquals(Type.TEXT, elementFoo2.getNodeKind());
    }

    @Test
    public void verifyCommentNodeType() {
        DummyNode node = new DummyNode(1, false, "dummy");
        DummyNode foo = new DummyNode(2, false, "#comment");
        node.jjtAddChild(foo, 0);

        DocumentNode document = new DocumentNode(node);
        ElementNode elementFoo = document.nodeToElementNode.get(foo);

        Assert.assertEquals(Type.COMMENT, elementFoo.getNodeKind());
    }
}
