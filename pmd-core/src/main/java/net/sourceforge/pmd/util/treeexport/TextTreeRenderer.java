/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.IOException;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySource;

/**
 * A simple recursive printer. Output looks like so:
 *
 * <pre>
 *
 * +- LocalVariableDeclaration
 *    +- Type
 *    |  +- PrimitiveType
 *    +- VariableDeclarator
 *       +- VariableDeclaratorId
 *       +- VariableInitializer
 *          +- 1 child not shown
 *
 * </pre>
 *
 * or
 *
 * <pre>
 *
 * └─ LocalVariableDeclaration
 *    ├─ Type
 *    │  └─ PrimitiveType
 *    └─ VariableDeclarator
 *       ├─ VariableDeclaratorId
 *       └─ VariableInitializer
 *          └─ 1 child not shown
 *
 * </pre>
 *
 *
 * By default just prints the structure, like shown above. You can
 * configure it to render nodes differently by overriding {@link #appendNodeInfoLn(Appendable, Node)}.
 */
@Experimental
public class TextTreeRenderer implements TreeRenderer {

    static final TreeRendererDescriptor DESCRIPTOR = new TreeRendererDescriptor() {

        private final PropertyDescriptor<Boolean> onlyAscii =
            PropertyFactory.booleanProperty("onlyAsciiChars")
                           .defaultValue(false)
                           .desc("Use only ASCII characters in the structure")
                           .build();

        private final PropertyDescriptor<Integer> maxLevel =
            PropertyFactory.intProperty("maxLevel")
                           .defaultValue(-1)
                           .desc("Max level on which to recurse. Negative means unbounded")
                           .build();

        @Override
        public PropertySource newPropertyBundle() {

            PropertySource bundle = new AbstractPropertySource() {
                @Override
                protected String getPropertySourceType() {
                    return "tree renderer";
                }

                @Override
                public String getName() {
                    return "text";
                }
            };

            bundle.definePropertyDescriptor(onlyAscii);
            bundle.definePropertyDescriptor(maxLevel);

            return bundle;
        }

        @Override
        public String id() {
            return "text";
        }

        @Override
        public String description() {
            return "Text renderer";
        }

        @Override
        public TreeRenderer produceRenderer(PropertySource properties) {
            return new TextTreeRenderer(properties.getProperty(onlyAscii), properties.getProperty(maxLevel));
        }
    };

    private final Strings str;
    private final int maxLevel;

    /**
     * Creates a new text renderer.
     *
     * @param onlyAscii Whether to output the skeleton of the tree with
     *                  only ascii characters. If false, uses unicode chars
     *                  like '├'
     * @param maxLevel  Max level on which to recurse. Negative means
     *                  unbounded. If the max level is reached, a placeholder
     *                  is dumped, like "1 child is not shown". This is
     *                  controlled by {@link #appendBoundaryForNodeLn(Node, Appendable, String)}.
     */
    public TextTreeRenderer(boolean onlyAscii, int maxLevel) {
        this.str = onlyAscii ? Strings.ASCII : Strings.UNICODE;
        this.maxLevel = maxLevel;
    }

    @Override
    public void renderSubtree(Node node, Appendable out) throws IOException {
        printInnerNode(node, out, 0, "", true);
    }

    private String childPrefix(String prefix, boolean isTail) {
        return prefix + (isTail ? str.gap : str.verticalEdge);
    }


    protected final void appendIndent(Appendable out, String prefix, boolean isTail) throws IOException {
        out.append(prefix).append(isTail ? str.tailFork : str.fork);
    }

    /**
     * Append info about the node. The indent has already been appended.
     * This should end with a newline. The default just appends the name
     * of the node, and no other information.
     */
    protected void appendNodeInfoLn(Appendable out, Node node) throws IOException {
        out.append(node.getXPathNodeName()).append("\n");
    }


    private void printInnerNode(Node node,
                                Appendable out,
                                int level,
                                String prefix,
                                boolean isTail) throws IOException {

        appendIndent(out, prefix, isTail);
        appendNodeInfoLn(out, node);

        if (level == maxLevel) {
            if (node.getNumChildren() > 0) {
                appendBoundaryForNodeLn(node, out, childPrefix(prefix, isTail));
            }
        } else {
            int n = node.getNumChildren() - 1;
            String childPrefix = childPrefix(prefix, isTail);
            for (int i = 0; i < node.getNumChildren(); i++) {
                Node child = node.getChild(i);
                printInnerNode(child, out, level + 1, childPrefix, i == n);
            }
        }
    }

    protected void appendBoundaryForNodeLn(Node node, Appendable out, String indentStr) throws IOException {
        appendIndent(out, indentStr, true);

        if (node.getNumChildren() == 1) {
            out.append("1 child is not shown");
        } else {
            out.append(String.valueOf(node.getNumChildren())).append(" children are not shown");
        }

        out.append('\n');
    }

    private static final class Strings {

        private static final Strings ASCII = new Strings(
            "+- ",
            "+- ",
            "|  ",
            "   "
        );
        private static final Strings UNICODE = new Strings(
            "└─ ",
            "├─ ",
            "│  ",
            "   "
        );

        private final String tailFork;
        private final String fork;
        private final String verticalEdge;
        private final String gap;


        private Strings(String tailFork, String fork, String verticalEdge, String gap) {
            this.tailFork = tailFork;
            this.fork = fork;
            this.verticalEdge = verticalEdge;
            this.gap = gap;
        }
    }

}
