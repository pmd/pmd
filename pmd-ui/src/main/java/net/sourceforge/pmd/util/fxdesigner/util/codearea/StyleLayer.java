/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


/**
 * Represents a layer of styling in the text. Several layers are aggregated into a {@link StyleContext}, and can evolve
 * independently. Layers are bound to the code area they style.
 */
class StyleLayer {

    private static final Pattern TAB_INDENT = Pattern.compile("^(\t*).*$");
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
    public Stack<StyleSpans<Collection<String>>> getSpans() {
        return spans;
    }


    /**
     * Resets the spans to the specified value.
     *
     * @param replacement The new spans
     */
    public void reset(StyleSpans<Collection<String>> replacement) {
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
     *
     * @throws IllegalArgumentException if the region identified is out of bounds
     */
    public void style(int beginLine, int beginColumn,
                      int endLine, int endColumn,
                      Set<String> cssClasses) {

        if (endLine > codeArea.getParagraphs().size()
            || endLine == codeArea.getParagraphs().size() && endColumn > codeArea.getParagraph(endLine - 1).length()) {
            throw new IllegalArgumentException("Cannot style, the region is out of bounds");
        }

        int offset = getAbsolutePosition(beginLine, beginColumn - 1);
        int spanLength = getAbsolutePosition(endLine, endColumn) - offset;


        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

        builder.add(Collections.emptySet(), offset);
        builder.add(cssClasses, spanLength);
        builder.add(Collections.emptySet(), codeArea.getLength() - (offset + spanLength));


        spans.push(builder.create());
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
