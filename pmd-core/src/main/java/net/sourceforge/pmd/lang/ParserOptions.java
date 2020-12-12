/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Objects;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Represents a set of configuration options for a {@link Parser}. For each
 * unique combination of ParserOptions a Parser will be used to create an AST.
 * Therefore, implementations must implement {@link Object#equals(Object)} and
 * {@link Object#hashCode()}.
 */
public class ParserOptions {
    private String suppressMarker = PMD.SUPPRESS_MARKER;

    /**
     * Language used to construct environment variable names that match PropertyDescriptors.
     */
    private final String languageId;

    private final ParserOptionsProperties parserOptionsProperties;

    public ParserOptions() {
        this.languageId = null;
        this.parserOptionsProperties = new ParserOptionsProperties();
    }

    public ParserOptions(String languageId) {
        this.languageId = Objects.requireNonNull(languageId);
        this.parserOptionsProperties = new ParserOptionsProperties();
    }

    protected final void defineProperty(PropertyDescriptor<?> propertyDescriptor) {
        parserOptionsProperties.definePropertyDescriptor(propertyDescriptor);
    }

    protected final <T> void defineProperty(PropertyDescriptor<T> propertyDescriptor, T initialValue) {
        defineProperty(propertyDescriptor);
        setProperty(propertyDescriptor, initialValue);
    }

    protected final <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T initialValue) {
        parserOptionsProperties.setProperty(propertyDescriptor, initialValue);
    }

    public final <T> T getProperty(PropertyDescriptor<T> propertyDescriptor) {
        return parserOptionsProperties.getProperty(propertyDescriptor);
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
        return Objects.equals(suppressMarker, that.suppressMarker)
                && Objects.equals(languageId, that.languageId)
                && Objects.equals(parserOptionsProperties, that.parserOptionsProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suppressMarker, languageId, parserOptionsProperties);
    }


    private final class ParserOptionsProperties extends AbstractPropertySource {

        @Override
        protected String getPropertySourceType() {
            return "ParserOptions";
        }

        @Override
        public String getName() {
            return ParserOptions.this.getClass().getSimpleName();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ParserOptionsProperties)) {
                return false;
            }
            final ParserOptionsProperties that = (ParserOptionsProperties) obj;
            return Objects.equals(getPropertiesByPropertyDescriptor(),
                                  that.getPropertiesByPropertyDescriptor());
        }

        @Override
        public int hashCode() {
            return getPropertiesByPropertyDescriptor().hashCode();
        }
    }
}
