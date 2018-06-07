/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpan;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * @author Clément Fournier
 * @since 6.5.0
 */
public class NodeStyleSpan {

    private static final Pattern TAB_INDENT = Pattern.compile("^(\t*).*$");
    private final Node node;
    private final Collection<String> style;
    private final CustomCodeArea codeArea;


    private NodeStyleSpan(Node node, Collection<String> style, CustomCodeArea codeArea) {
        this.node = node;
        this.style = style;
        this.codeArea = codeArea;
    }


    public Node getNode() {
        return node;
    }


    /**
     * Snapshots the node's position, which factors-in changes to the nodes's position
     * that occurred since the node was parsed.
     */
    public PositionSnapshot snapshot() {
        try {

            int lastKnownStart = getAbsolutePosition(node.getBeginLine(), node.getBeginColumn() - 1);
            int lastKnownEnd = getAbsolutePosition(node.getEndLine(), node.getEndColumn());
            int offset = codeArea.getAccumulatedOffsetSinceLastAstRefresh(lastKnownStart);
            return new PositionSnapshot(lastKnownStart + offset, lastKnownEnd + offset);

        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }


    public Collection<String> getStyle() {
        return style;
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


    public static NodeStyleSpan fromNode(Node node, Collection<String> styles, CustomCodeArea codeArea) {
        return new NodeStyleSpan(node, styles, codeArea);
    }


    class PositionSnapshot {
        private final int beginIndex;
        private final int endIndex;


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

        //        public boolean isBefore(PositionSnapshot other) {
        //            return other.getBeginIndex()
        //        }


        public Collection<String> getStyle() {
            return NodeStyleSpan.this.getStyle();
        }


        public StyleSpan<Collection<String>> toSpan() {
            return new StyleSpan<>(getStyle(), getLength());
        }
    }
}
