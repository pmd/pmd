/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.Paragraph;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Wrapper around a node used to declutter the layering algorithm with
 * convenience methods. See {@link #snapshot()}.
 *
 * @author Clément Fournier
 * @since 6.5.0
 */
public class NodeStyleSpan {

    private static final Pattern TAB_INDENT = Pattern.compile("^(\t*).*$");
    private static final Comparator<NodeStyleSpan> COMPARATOR = Comparator.comparing(NodeStyleSpan::getNode, Comparator.comparingInt(Node::getBeginLine).thenComparing(Node::getBeginColumn));
    private final Node node;
    private final CustomCodeArea codeArea;


    private NodeStyleSpan(Node node, CustomCodeArea codeArea) {
        this.node = node;
        this.codeArea = codeArea;
    }


    public Node getNode() {
        return node;
    }


    /**
     * Snapshots the absolute coordinates of the node in the code area
     * for the duration of the layering algorithm.
     */
    public PositionSnapshot snapshot() {
        try {
            int lastKnownStart = getAbsolutePosition(node.getBeginLine(), node.getBeginColumn() - 1);
            int lastKnownEnd = getAbsolutePosition(node.getEndLine(), node.getEndColumn());
            return new PositionSnapshot(lastKnownStart, lastKnownEnd);

        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    private int getAbsolutePosition(int line, int column) {
        return codeArea.getAbsolutePosition(line - 1, column) - indentationOffset(line - 1);
    }


    // CodeArea counts a tab as 1 column width but displays it as 8 columns width.
    // PMD counts it correctly as 8 columns, so we must offset the position
    private int indentationOffset(int paragraph) {
        Paragraph<Collection<String>, String, Collection<String>> p = codeArea.getParagraph(paragraph);
        Matcher m = TAB_INDENT.matcher(p.getText());
        if (m.matches()) {
            return m.group(1).length() * 7;
        }
        return 0;
    }


    @Override
    public String toString() {
        return node.getXPathNodeName() + "@" + snapshot();
    }


    /**
     * Returns a comparator that orders spans according to the start
     * index of the node they wrap.
     */
    public static Comparator<NodeStyleSpan> documentOrderComparator() {
        return COMPARATOR;
    }


    public static NodeStyleSpan fromNode(Node node, CustomCodeArea codeArea) {
        return new NodeStyleSpan(node, codeArea);
    }


    class PositionSnapshot {
        private int beginIndex;
        private int endIndex;


        private PositionSnapshot(int beginIndex, int endIndex) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }


        @Override
        public String toString() {
            // debug only
            return getText() + "@[" + beginIndex + "," + endIndex + ']';
        }


        private String getText() {
            return codeArea.getText(beginIndex, endIndex);
        }


        public int getBeginIndex() {
            return beginIndex;
        }


        public int getEndIndex() {
            return endIndex;
        }


        public int getLength() {
            return endIndex - beginIndex;
        }


        public Node getNode() {
            return NodeStyleSpan.this.getNode();
        }
    }
}
