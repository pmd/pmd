/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;

import net.sf.saxon.Configuration;
import net.sf.saxon.type.Type;

public class ElementNodeTest {


    @Test
    public void testCompareOrder() {
        DummyRootNode root = new DummyRootNode();

        DummyNode c0 = new DummyNode(false, "foo");
        c0.setCoords(1, 1, 2, 2);
        root.addChild(c0, 0);

        DummyNode c1 = new DummyNode(false, "foo");
        c1.setCoords(2, 1, 2, 2);
        root.addChild(c1, 1);


        Configuration configuration = Configuration.newConfiguration();

        AstTreeInfo treeInfo = new AstTreeInfo(root, configuration);
        Assert.assertSame(root, treeInfo.getRootNode().getUnderlyingNode());
        Assert.assertEquals(Type.DOCUMENT, treeInfo.getRootNode().getNodeKind());

        AstElementNode rootElt = treeInfo.getRootNode().getRootElement();
        Assert.assertSame(root, rootElt.getUnderlyingNode());
        Assert.assertEquals(Type.ELEMENT, rootElt.getNodeKind());
        Assert.assertSame(rootElt, treeInfo.findWrapperFor(root));

        AstElementNode elementFoo0 = rootElt.getChildren().get(0);
        Assert.assertSame(c0, elementFoo0.getUnderlyingNode());
        Assert.assertSame(elementFoo0, treeInfo.findWrapperFor(c0));

        AstElementNode elementFoo1 = rootElt.getChildren().get(1);
        Assert.assertSame(c1, elementFoo1.getUnderlyingNode());
        Assert.assertSame(elementFoo1, treeInfo.findWrapperFor(c1));

        Assert.assertFalse(elementFoo0.isSameNodeInfo(elementFoo1));
        Assert.assertFalse(elementFoo1.isSameNodeInfo(elementFoo0));
        Assert.assertTrue(elementFoo0.compareOrder(elementFoo1) < 0);
        Assert.assertTrue(elementFoo1.compareOrder(elementFoo0) > 0);
        Assert.assertEquals(0, elementFoo0.compareOrder(elementFoo0));
        Assert.assertEquals(0, elementFoo1.compareOrder(elementFoo1));

    }

    @Test
    public void verifyTextNodeType() {
        DummyRootNode root = new DummyRootNode();

        DummyNode c0 = new DummyNode(false, "foo");
        c0.setCoords(1, 1, 2, 2);
        root.addChild(c0, 0);

        DummyNode c1 = new DummyNode(false, "#text");
        c1.setCoords(2, 1, 2, 2);
        root.addChild(c1, 1);

        Configuration configuration = Configuration.newConfiguration();
        AstTreeInfo treeInfo = new AstTreeInfo(root, configuration);

        AstElementNode rootElt = treeInfo.getRootNode().getRootElement();
        Assert.assertSame(root, rootElt.getUnderlyingNode());
        Assert.assertEquals(Type.ELEMENT, rootElt.getNodeKind());
        Assert.assertSame(rootElt, treeInfo.findWrapperFor(root));

        AstElementNode elementFoo0 = rootElt.getChildren().get(0);
        Assert.assertEquals(Type.ELEMENT, elementFoo0.getNodeKind());
        Assert.assertSame(c0, elementFoo0.getUnderlyingNode());
        Assert.assertSame(elementFoo0, treeInfo.findWrapperFor(c0));

        AstElementNode elementText1 = rootElt.getChildren().get(1);
        Assert.assertEquals(Type.TEXT, elementText1.getNodeKind());
        Assert.assertSame(c1, elementText1.getUnderlyingNode());
        Assert.assertSame(elementText1, treeInfo.findWrapperFor(c1));
    }

    @Test
    public void verifyCommentNodeType() {
        DummyRootNode root = new DummyRootNode();

        DummyNode c1 = new DummyNode(false, "#comment");
        c1.setCoords(2, 1, 2, 2);
        root.addChild(c1, 0);

        Configuration configuration = Configuration.newConfiguration();
        AstTreeInfo treeInfo = new AstTreeInfo(root, configuration);
        AstElementNode rootElt = treeInfo.getRootNode().getRootElement();

        AstElementNode elementComment = rootElt.getChildren().get(0);
        Assert.assertEquals(Type.COMMENT, elementComment.getNodeKind());
        Assert.assertSame(c1, elementComment.getUnderlyingNode());
        Assert.assertSame(elementComment, treeInfo.findWrapperFor(c1));
    }

}
