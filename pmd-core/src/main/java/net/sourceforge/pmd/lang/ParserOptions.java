/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Locale;
import java.util.Objects;

import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Represents a set of configuration options for a {@link Parser}. For each
 * unique combination of ParserOptions a Parser will be used to create an AST.
 * Therefore, implementations must implement {@link Object#equals(Object)} and
 * {@link Object#hashCode()}.
 */
public class ParserOptions {
    /**
     * @deprecated Use {@link #getSuppressMarker()} instead.
     */
    @Deprecated
    protected String suppressMarker;

    /**
     * Language used to construct environment variable names that match PropertyDescriptors.
     */
    private final String languageId;

    private final ParserOptionsProperties parserOptionsProperties;

    public ParserOptions() {
        this(null);
    }

    public ParserOptions(String languageId) {
        this.languageId = languageId;
        this.parserOptionsProperties = new ParserOptionsProperties();
    }

    public String getSuppressMarker() {
        return suppressMarker;
    }

    public void setSuppressMarker(String suppressMarker) {
        this.suppressMarker = suppressMarker;
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

    /**
     * Returns the environment variable name that a user can set in order to override the default value.
     */
    String getEnvironmentVariableName(PropertyDescriptor<?> propertyDescriptor) {
        if (languageId == null) {
            throw new IllegalStateException("Language is null");
        }
        return "PMD_" + languageId.toUpperCase(Locale.ROOT) + "_"
            + propertyDescriptor.name().toUpperCase(Locale.ROOT);
    }

    /**
     * @return environment variable that overrides the PropertyDesciptors default value. Returns null if no environment
     *     variable has been set.
     */
    protected String getEnvValue(PropertyDescriptor<?> propertyDescriptor) {
        return System.getenv(getEnvironmentVariableName(propertyDescriptor));
    }

    /**
     * Overrides the default PropertyDescriptors with values found in environment variables.
     * TODO: Move this to net.sourceforge.pmd.PMD#parserFor when CLI options are implemented
     */
    protected void overridePropertiesFromEnv() {
        for (PropertyDescriptor<?> propertyDescriptor : parserOptionsProperties.getPropertyDescriptors()) {
            String propertyValue = getEnvValue(propertyDescriptor);

            if (propertyValue != null) {
                setPropertyCapture(propertyDescriptor, propertyValue);
            }
        }
    }

    private <T> void setPropertyCapture(PropertyDescriptor<T> propertyDescriptor, String propertyValue) {
        T value = propertyDescriptor.valueFrom(propertyValue);
        parserOptionsProperties.setProperty(propertyDescriptor, value);
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
