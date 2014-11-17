/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

/**
 * Represents a set of configuration options for a {@link Parser}. For each
 * unique combination of ParserOptions a Parser will be used to create an AST.
 * Therefore, implementations must implement {@link Object#equals(Object)} and
 * {@link Object#hashCode()}.
 */
public class ParserOptions {
    protected String suppressMarker;

    public String getSuppressMarker() {
        return suppressMarker;
    }

    public void setSuppressMarker(String suppressMarker) {
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
        return this.suppressMarker.equals(that.suppressMarker);
    }

    @Override
    public int hashCode() {
        return suppressMarker != null ? suppressMarker.hashCode() : 0;
    }
}
