/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMD;

/**
 * Represents a set of configuration options for a {@link Parser}. For each
 * unique combination of ParserOptions a Parser will be used to create an AST.
 * Therefore, implementations must implement {@link Object#equals(Object)} and
 * {@link Object#hashCode()}.
 */
public class ParserOptions {
    private String suppressMarker = PMD.SUPPRESS_MARKER;

    public String getSuppressMarker() {
        return suppressMarker;
    }

    public final void setSuppressMarker(@NonNull String suppressMarker) {
        Objects.requireNonNull(suppressMarker);
        this.suppressMarker = suppressMarker;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ParserOptions that = (ParserOptions) obj;
        return this.getSuppressMarker().equals(that.getSuppressMarker());
    }

    @Override
    public int hashCode() {
        return getSuppressMarker() != null ? getSuppressMarker().hashCode() : 0;
    }
}
