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
public class ParserOptions extends AbstractPropertySource {
    protected String suppressMarker;

    /**
     * Language used to construct environment variable names that match PropertyDescriptors.
     */
    private final Language language;

    public ParserOptions() {
        this(null);
    }

    public ParserOptions(Language language) {
        this.language = language;
    }

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
        if (!super.equals(obj)) {
            return false;
        }
        final ParserOptions that = (ParserOptions) obj;
        return Objects.equals(suppressMarker, that.suppressMarker)
                && Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), suppressMarker, language);
    }

    @Override
    protected String getPropertySourceType() {
        return "ParserOptions";
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Returns the environment variable name that a user can set in order to override the default value.
     */
    String getEnvironmentVariableName(PropertyDescriptor propertyDescriptor) {
        if (language == null) {
            throw new IllegalStateException("Language is null");
        }
        return "PMD_" + language.getTerseName().toUpperCase(Locale.ROOT) + "_"
                + propertyDescriptor.name().toUpperCase(Locale.ROOT);
    }

    /**
     * @return environment variable that overrides the PropertyDesciptors default value. Returns null if no environment
     * variable has been set.
     */
    protected String getEnvValue(PropertyDescriptor propertyDescriptor) {
        return System.getenv(getEnvironmentVariableName(propertyDescriptor));
    }

    /**
     * Overrides the default PropertyDescriptors with values found in environment variables.
     * TODO: Move this to net.sourceforge.pmd.PMD#parserFor when CLI options are implemented
     */
    protected void overridePropertiesFromEnv() {
        for (PropertyDescriptor propertyDescriptor : getPropertyDescriptors()) {
            String propertyValue = getEnvValue(propertyDescriptor);

            if (propertyValue != null) {
                Object value = propertyDescriptor.valueFrom(propertyValue);
                setProperty(propertyDescriptor, value);
            }
        }
    }
}
