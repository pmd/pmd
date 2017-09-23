/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.fxmisc.richtext.Paragraph;

/**
 * Represents a layer of styling in the text. Several layers are aggregated into a {@link StyleContext}, and can evolve
 * independently. Layers are bound to the code area they style.
 */
class StyleLayer {

    private final String id;
    private List<SpanBound> bounds = new ArrayList<>();
    private CustomCodeArea codeArea;


    StyleLayer(String id, CustomCodeArea parent) {
        Objects.requireNonNull(id, "The id of a style layer cannot be null");
        this.id = id;
        codeArea = parent;
    }


    /**
     * Clears this layer from previous styling.
     */
    public void clearStyles() {
        bounds.clear();
    }


    public void setBounds(List<SpanBound> newBounds) {
        bounds = newBounds;
    }


    /**
     * Gets the span bounds of this layer.
     *
     * @return The span bounds
     */
    List<SpanBound> getBounds() {
        return bounds;
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
        SpanBound start = new SpanBound(offset, cssClasses, true);
        SpanBound end = new SpanBound(offset + spanLength, cssClasses, false);


        bounds.add(start);
        bounds.add(end);
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
}
