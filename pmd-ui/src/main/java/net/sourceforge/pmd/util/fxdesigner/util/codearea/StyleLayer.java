/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * Represents a layer of styling in the text. Several layers are
 * aggregated into a {@link StyleContext}, and can evolve
 * independently.
 */
class StyleLayer {


    private final Map<Set<String>, UniformStyleCollection> styleToCollection = new HashMap<>();


    /** Reset this layer to its empty state, clearing all the styles. */
    public void clearStyles() {
        styleToCollection.clear();
    }


    public Collection<UniformStyleCollection> getCollections() {
        return styleToCollection.values();
    }


    public void styleNodes(UniformStyleCollection updates) {
        UniformStyleCollection newValue = Optional.ofNullable(styleToCollection.get(updates.getStyle()))
                                                  .map(updates::merge)
                                                  .orElse(updates);

        styleToCollection.put(updates.getStyle(), newValue);
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
        return Objects.equals(styleToCollection, that.styleToCollection);
    }


    @Override
    public int hashCode() {

        return Objects.hash(styleToCollection);
    }
}
