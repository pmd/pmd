/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Objects;
import java.util.Stack;

import org.fxmisc.richtext.model.StyleSpans;


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
    public Stack<StyleSpans<Collection<String>>> getSpans() {
        return spans;
    }


    /** Adds new spans. */
    public void addSpans(Collection<StyleSpans<Collection<String>>> spans) {
        this.spans.addAll(spans);
    }


    /** Reset this layer to its empty state, clearing all the styles. */
    public void clearStyles() {
        spans.clear();
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



    @Override
    public String toString() {
        return "StyleLayer{"
               + "id='" + id + '\''
               + ", spans=\"" + spans.size() + " spans\""
               + '}';
    }
}
