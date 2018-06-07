/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Objects;


/**
 * Represents a layer of styling in the text. Several layers are
 * aggregated into a {@link StyleContext}, and can evolve
 * independently.
 */
class StyleLayer {


    private final StyleCollection nodesToCssClass = new StyleCollection();

    /** Reset this layer to its empty state, clearing all the styles. */
    public void clearStyles() {
        nodesToCssClass.clear();
    }


    public StyleCollection getStyleSpansCoordinates() {
        return nodesToCssClass;
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
        return Objects.equals(nodesToCssClass, that.nodesToCssClass);
    }


    @Override
    public int hashCode() {

        return Objects.hash(nodesToCssClass);
    }


    public void styleNodes(StyleCollection updates) {
        nodesToCssClass.addAll(updates);
    }
}
