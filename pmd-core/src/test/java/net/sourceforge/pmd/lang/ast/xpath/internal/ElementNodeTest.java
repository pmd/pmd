/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;

import net.sf.saxon.Configuration;
import net.sf.saxon.sxpath.XPathEvaluator;

public class ElementNodeTest {

    @Test
    public void testCompareOrder() {
        DummyNode node = new DummyNode( "dummy");
        DummyNode foo1 = new DummyNode("foo").setCoords(1,1,1,2);
        DummyNode foo2 = new DummyNode( "foo").setCoords(1,1,1,2);
        node.jjtAddChild(foo1, 0);
        node.jjtAddChild(foo2, 1);


        Configuration configuration = new XPathEvaluator().getStaticContext().getConfiguration();

        AstDocument document = new AstDocument(node, configuration);
        Assert.assertSame(node, document.getRootNode().getUnderlyingNode());

        AstNodeWrapper elementFoo1 = document.getRootNode().getChildren().get(0);
        Assert.assertSame(foo1, elementFoo1.getUnderlyingNode());

        AstNodeWrapper elementFoo2 = document.getRootNode().getChildren().get(1);
        Assert.assertSame(foo2, elementFoo2.getUnderlyingNode());

        Assert.assertFalse(elementFoo1.isSameNodeInfo(elementFoo2));
        Assert.assertFalse(elementFoo2.isSameNodeInfo(elementFoo1));
        Assert.assertTrue(elementFoo1.compareOrder(elementFoo2) < 0);
        Assert.assertTrue(elementFoo2.compareOrder(elementFoo1) > 0);
        Assert.assertEquals(0, elementFoo1.compareOrder(elementFoo1));
        Assert.assertEquals(0, elementFoo2.compareOrder(elementFoo2));

    }
}
