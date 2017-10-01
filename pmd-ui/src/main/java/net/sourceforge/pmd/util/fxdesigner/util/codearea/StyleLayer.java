/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 * Represents a layer of styling in the text. Several layers are aggregated into a {@link StyleContext}, and can evolve
 * independently. Layers are bound to the code area they style.
 */
class StyleLayer {

    private final String id;
    private Stack<StyleSpans<Collection<String>>> spans = new Stack<>();
    private CustomCodeArea codeArea;


    StyleLayer(String id, CustomCodeArea parent) {
        Objects.requireNonNull(id, "The id of a style layer cannot be null");
        this.id = id;
        codeArea = parent;
    }


    /**
     * Returns the stack of all spans contained in this one.
     *
     * @return The stack of all spans
     */
    Stack<StyleSpans<Collection<String>>> getSpans() {
        return spans;
    }


    /**
     * Resets the spans to the specified value.
     *
     * @param replacement The new spans
     */
    void reset(StyleSpans<Collection<String>> replacement) {
        spans.clear();
        spans.push(replacement);
    }


    /**
     * Adds CSS styling to a piece of text using line and column coordinates.
     *
     * @param beginLine   Begin line
     * @param beginColumn Begin column
     * @param endLine     End line
     * @param endColumn   End column
     * @param cssClasses  CSS classes with which to style the text
     */
    public void style(int beginLine, int beginColumn,
                      int endLine, int endColumn,
                      Set<String> cssClasses) {

        int offset = lengthUntil(beginLine, beginColumn);
        int spanLength = lengthBetween(beginLine, beginColumn, endLine, endColumn);

        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

        builder.add(Collections.emptySet(), offset);
        builder.add(cssClasses, spanLength);
        builder.add(Collections.emptySet(), codeArea.getLength() - (offset + spanLength));

        spans.push(builder.create());
    }


    /** Length in characters before the specified position. */
    private int lengthUntil(int line, int column) {
        List<Paragraph<Collection<String>>> paragraphs = codeArea.getParagraphs();
        int length = 0;
        for (int i = 0; i < line - 1; i++) {
            length += paragraphs.get(i).length() + 1;
        }
        return length + column - 1;
    }


    /** Length in characters between the two positions. */
    private int lengthBetween(int l1, int c1, int l2, int c2) {
        int par1 = l1 - 1;
        int par2 = l2 - 1;
        if (l1 == l2) {
            return c2 - c1 + 1;
        } else if (l1 < l2) {
            List<Paragraph<Collection<String>>> paragraphs = codeArea.getParagraphs();
            int length = paragraphs.get(par1).length() - c1 + 1;
            for (int i = par1 + 1; i < par2; i++) {
                length += paragraphs.get(i).length() + 1;
            }
            return length + c2 + 1;
        } else {
            throw new IllegalArgumentException();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StyleLayer that = (StyleLayer) o;

        return id.equals(that.id);
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }


    public void clearStyles() {
        spans.clear();
    }


    @Override
    public String toString() {
        return "StyleLayer{"
            + "id='" + id + '\''
            + ", spans=\"" + spans.size() + " spans\""
            + '}';
    }
}
