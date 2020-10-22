/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import java.util.List;
import java.util.function.Supplier;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyNodeTypeB;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * @author Clément Fournier
 */
public final class DummyTreeUtil {


    private DummyTreeUtil() {

    }


    public static DummyRoot root(DummyNode... children) {
        return nodeImpl(new DummyRoot(), children);
    }

    /** Creates a dummy node with the given children. */
    public static DummyNode node(DummyNode... children) {
        return nodeImpl(new DummyNode(), children);
    }

    /** Creates a dummy node with the given children. */
    public static DummyNode nodeB(DummyNode... children) {
        return nodeImpl(new DummyNodeTypeB(), children);
    }

    private static <T extends DummyNode> T nodeImpl(T node, DummyNode... children) {
        node.publicSetChildren(children);
        return node;
    }


    public static DummyNode followPath(DummyNode root, String path) {
        List<Integer> pathIndices = CollectionUtil.map(path.split(""), Integer::valueOf);

        Node current = root;
        for (int i : pathIndices) {
            current = current.getChild(i);
        }

        return (DummyNode) current;
    }


    /**
     * Must wrap the actual {@link #node(DummyNode...)} usages to assign each node the
     * image of its path from the root (in indices). E.g.
     *
     * <pre>
     * node(         ""
     *   node(       "0"
     *     node(),   "00"
     *     node(     "01"
     *       node()  "010
     *     )
     *   ),
     *   node()      "1"
     * )
     * </pre>
     */
    public static DummyRoot tree(Supplier<DummyRoot> supplier) {
        DummyRoot dummyNode = supplier.get();
        assignPathImage(dummyNode, "");
        return dummyNode;
    }


    private static void assignPathImage(DummyNode node, String curPath) {
        node.setImage(curPath);

        for (int i = 0; i < node.getNumChildren(); i++) {
            assignPathImage(node.getChild(i), curPath + i);
        }
    }

    /** List of the images of the stream. */
    public static List<String> pathsOf(NodeStream<?> stream) {
        return stream.toList(Node::getImage);
    }
}
