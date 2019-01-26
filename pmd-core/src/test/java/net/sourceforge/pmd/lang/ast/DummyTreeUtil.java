/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.function.Supplier;


/**
 * @author Clément Fournier
 */
public final class DummyTreeUtil {


    private DummyTreeUtil() {

    }


    /** Creates a dummy node with the given children. */
    public static DummyNode node(DummyNode... children) {
        DummyNode node = new DummyNode(0);
        node.children = children;
        return node;
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
    public static DummyNode tree(Supplier<DummyNode> supplier) {
        DummyNode dummyNode = supplier.get();
        assignPathImage(dummyNode, "");
        return dummyNode;
    }


    private static void assignPathImage(Node node, String curPath) {
        node.setImage(curPath);

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            assignPathImage(node.jjtGetChild(i), curPath + i);
        }
    }

}
