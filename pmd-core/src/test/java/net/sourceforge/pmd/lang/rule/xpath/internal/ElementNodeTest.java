/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertSame(root, treeInfo.getRootNode().getUnderlyingNode());
        Assertions.assertEquals(Type.DOCUMENT, treeInfo.getRootNode().getNodeKind());

        AstElementNode rootElt = treeInfo.getRootNode().getRootElement();
        Assertions.assertSame(root, rootElt.getUnderlyingNode());
        Assertions.assertEquals(Type.ELEMENT, rootElt.getNodeKind());
        Assertions.assertSame(rootElt, treeInfo.findWrapperFor(root));

        AstElementNode elementFoo0 = rootElt.getChildren().get(0);
        Assertions.assertSame(c0, elementFoo0.getUnderlyingNode());
        Assertions.assertSame(elementFoo0, treeInfo.findWrapperFor(c0));

        AstElementNode elementFoo1 = rootElt.getChildren().get(1);
        Assertions.assertSame(c1, elementFoo1.getUnderlyingNode());
        Assertions.assertSame(elementFoo1, treeInfo.findWrapperFor(c1));

        Assertions.assertFalse(elementFoo0.isSameNodeInfo(elementFoo1));
        Assertions.assertFalse(elementFoo1.isSameNodeInfo(elementFoo0));
        Assertions.assertTrue(elementFoo0.compareOrder(elementFoo1) < 0);
        Assertions.assertTrue(elementFoo1.compareOrder(elementFoo0) > 0);
        Assertions.assertEquals(0, elementFoo0.compareOrder(elementFoo0));
        Assertions.assertEquals(0, elementFoo1.compareOrder(elementFoo1));

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
        Assertions.assertSame(root, rootElt.getUnderlyingNode());
        Assertions.assertEquals(Type.ELEMENT, rootElt.getNodeKind());
        Assertions.assertSame(rootElt, treeInfo.findWrapperFor(root));

        AstElementNode elementFoo0 = rootElt.getChildren().get(0);
        Assertions.assertEquals(Type.ELEMENT, elementFoo0.getNodeKind());
        Assertions.assertSame(c0, elementFoo0.getUnderlyingNode());
        Assertions.assertSame(elementFoo0, treeInfo.findWrapperFor(c0));

        AstElementNode elementText1 = rootElt.getChildren().get(1);
        Assertions.assertEquals(Type.TEXT, elementText1.getNodeKind());
        Assertions.assertSame(c1, elementText1.getUnderlyingNode());
        Assertions.assertSame(elementText1, treeInfo.findWrapperFor(c1));
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
        Assertions.assertEquals(Type.COMMENT, elementComment.getNodeKind());
        Assertions.assertSame(c1, elementComment.getUnderlyingNode());
        Assertions.assertSame(elementComment, treeInfo.findWrapperFor(c1));
    }

}
