/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.Rule;


/**
 * Single value property.
 *
 * @param <T> The type of the value.
 *
 * @author Clément Fournier
 */
@Deprecated
/* default */ abstract class AbstractSingleValueProperty<T> extends AbstractProperty<T>
        implements SingleValuePropertyDescriptor<T> {

    /** Default value. */
    private T defaultValue;


    /**
     * Creates a single value property.
     *
     * @param theName             Name of the property
     * @param theDescription      Description
     * @param theUIOrder          UI order
     * @param theDefault          Default value
     * @param isDefinedExternally Whether the property is defined in the XML (by a XPath rule) or not
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractSingleValueProperty(String theName, String theDescription, T theDefault,
                                          float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theUIOrder, isDefinedExternally);

        defaultValue = theDefault;
    }


    @Override
    public final T defaultValue() {
        return defaultValue;
    }


    @Override
    public final boolean isMultiValue() {
        return false;
    }


    @Override
    public String asDelimitedString(T value) {
        return asString(value);
    }


    /**
     * Returns a string representation of the value, even if it's null.
     *
     * @param value The value to describe
     *
     * @return A string representation of the value
     */
    protected String asString(T value) {
        return value == null ? "" : value.toString();
    }


    @Override
    public String propertyErrorFor(Rule rule) {
        T realValue = rule.getProperty(this);
        return realValue == null ? null : errorFor(realValue);
    }


    @Override
    public String errorFor(T value) {
        String typeError = typeErrorFor(value);
        if (typeError != null) {
            return typeError;
        }
        return valueErrorFor(value);
    }


    private String typeErrorFor(T value) {
        if (value != null && !type().isAssignableFrom(value.getClass())) {
            return value + " is not an instance of " + type();
        }
        return null;
    }


    /**
     * Checks the value for an error.
     *
     * @param value Value to check
     *
     * @return A diagnostic error message, or null if there's no problem
     */
    protected String valueErrorFor(T value) {
        return value != null || defaultHasNullValue() ? null : "missing value";
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
    protected final String defaultAsString() {
        return asString(defaultValue);
    }


    @Override
    public final T valueFrom(String valueString) throws IllegalArgumentException {
        return createFrom(valueString);
    }


    /**
     * Parse a string and returns an instance of a value.
     *
     * @param toParse String to parse
     *
     * @return An instance of a value
     */
    protected abstract T createFrom(String toParse);    // this is there to be symmetrical to multi values


}
