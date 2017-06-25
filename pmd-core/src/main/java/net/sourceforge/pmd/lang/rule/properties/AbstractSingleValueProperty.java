/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.Rule;

/**
 * Single value property.
 *
 * @param <T> The type of the value.
 *
 * @author Clément Fournier
 */
public abstract class AbstractSingleValueProperty<T> extends AbstractProperty<T> {

    /** Default value. */
    protected T defaultValue;

    /**
     * Creates a single value property using the default delimiter {@link #DEFAULT_DELIMITER}.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theUIOrder     UI order (must be positive or zero)
     * @param theDefault     Default value
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractSingleValueProperty(String theName, String theDescription, T theDefault, float theUIOrder) {
        super(theName, theDescription, theUIOrder, DEFAULT_DELIMITER);

        defaultValue = theDefault;
    }


    @Override
    public final T defaultValue() {
        return defaultValue;
    }

    /**
     * Returns true if the default value is {@code null}.
     *
     * @return True if the default value is {@code null}.
     */
    private boolean defaultHasNullValue() {
        return defaultValue == null;
    }

    @Override
    public final boolean isMultiValue() {
        return false;
    }

    @Override
    public String asDelimitedString(T values, char delimiter) {
        return values == null ? "" : values.toString();
    }

    @Override
    public String propertyErrorFor(Rule rule) {
        T realValue = rule.getProperty(this);
        if (realValue == null && !isRequired()) {
            return null;
        }
        return errorFor(realValue);
    }

    @Override
    public String errorFor(T value) {
        String typeError = typeErrorFor(value);
        if (typeError != null) {
            return typeError;
        }
        return valueErrorFor(value);
    }

    private String typeErrorFor(T value) { // TODO:cf consider subtypes!!

        if (value != null && !type().isAssignableFrom(value.getClass())) {
            return value + " is not an instance of " + type();
        }

        return null;
    }

    protected String valueErrorFor(T value) {
        return value != null || defaultHasNullValue() ? null : "missing value";
    }

    @Override
    protected final String defaultAsString() {
        return defaultValue().toString();
    }

    @Override
    public Map<String, T> choices() {
        return null;
    }

    /**
     * Parse a string and returns an instance of a value.
     *
     * @param toParse String to parse
     *
     * @return An instance of a value
     */
    protected abstract T createFrom(String toParse);

    // this is there to be symmetrical.

    @Override
    public final T valueFrom(String valueString) throws IllegalArgumentException {
        return createFrom(valueString);
    }

}
