/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.saxon;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.DocumentNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;

public class ElementNodeTest {

    @Test
    public void testCompareOrder() {
        DummyNode node = new DummyNode(false, "dummy");
        DummyNode foo1 = new DummyNode(false, "foo");
        foo1.setCoords(1, 1, 2, 2);
        DummyNode foo2 = new DummyNode(false, "foo");
        foo1.setCoords(2, 1, 2, 2);
        node.addChild(foo1, 0);
        node.addChild(foo2, 1);

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
        DummyNode node = new DummyNode(false, "dummy");
        DummyNode foo1 = new DummyNode(false, "foo");
        foo1.setCoords(1, 1, 5, 5);
        DummyNode foo2 = new DummyNode(false, "foo");
        foo2.setCoords(1, 1, 5, 5);
        node.addChild(foo1, 0);
        node.addChild(foo2, 1);

        DocumentNode document = new DocumentNode(node);
        ElementNode elementFoo1 = document.nodeToElementNode.get(foo1);
        ElementNode elementFoo2 = document.nodeToElementNode.get(foo2);

        Assert.assertFalse(elementFoo1.isSameNodeInfo(elementFoo2));
        Assert.assertFalse(elementFoo2.isSameNodeInfo(elementFoo1));
        Assert.assertTrue(elementFoo1.compareOrder(elementFoo2) < 0);
        Assert.assertTrue(elementFoo2.compareOrder(elementFoo1) > 0);
    }
}
