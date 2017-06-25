/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.Rule;

/**
 * Multi valued property.
 *
 * @param <V> The type of the individual values. The multiple values are wrapped into a list.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMultiValueProperty<V> extends AbstractProperty<List<V>> {

    /** The default value. */
    protected final List<V> defaultValue;


    /**
     * Creates a multi valued property using the default delimiter {@link #DEFAULT_DELIMITER}.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theDefault     Default value
     * @param theUIOrder     UI order (must be positive or zero)
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    public AbstractMultiValueProperty(String theName, String theDescription, List<V> theDefault, float theUIOrder) {
        this(theName, theDescription, theDefault, theUIOrder, DEFAULT_DELIMITER);
    }


    /**
     * Creates a multi valued property using another delimiter.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theDefault     Default value
     * @param theUIOrder     UI order (must be positive or zero)
     * @param delimiter      The delimiter to use
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    public AbstractMultiValueProperty(String theName, String theDescription, List<V> theDefault,
                                         float theUIOrder, char delimiter) {

        super(theName, theDescription, theUIOrder, delimiter);
        defaultValue = theDefault;
    }


    /**
     * Creates a multi valued property using the default delimiter {@link #DEFAULT_DELIMITER}.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theDefault     Default value
     * @param theUIOrder     UI order (must be positive or zero)
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractMultiValueProperty(String theName, String theDescription, V[] theDefault, float theUIOrder) {
        this(theName, theDescription, theDefault, theUIOrder, DEFAULT_DELIMITER);
    }

    /**
     * Constructor for AbstractPMDProperty.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theDefault     Default value
     * @param theUIOrder     UI order (must be positive or zero)
     * @param delimiter      The delimiter to separate multi values
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractMultiValueProperty(String theName, String theDescription, V[] theDefault,
                                         float theUIOrder, char delimiter) {

        this(theName, theDescription, Arrays.asList(theDefault), theUIOrder, delimiter);
    }


    @Override
    public List<V> defaultValue() {
        return defaultValue;
    }

    /**
     * Returns true if a default value is null.
     *
     * @return True if a default value is null
     */
    private boolean defaultHasNullValue() {
        return defaultValue == null || defaultValue.contains(null);
    }

    @Override
    public final boolean isMultiValue() {
        return true;
    }

    @Override
    public String asDelimitedString(List<V> values, char delimiter) {
        if (values == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (V value : values) {
            sb.append(value == null ? "" : value.toString()).append(delimiter);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }


    @Override
    public String errorFor(List<V> values) {

        String err;
        for (V value2 : values) {
            err = valueErrorFor(value2);
            if (err != null) {
                return err;
            }
        }

        return null;
    }

    /**
     * Checks a single value for a "missing value" error.
     *
     * @param value Value to check
     *
     * @return A descriptive String of the error or null if there was none
     */
    protected String valueErrorFor(V value) {
        return value != null || defaultHasNullValue() ? null : "missing value";
    }

    @Override
    public Map<String, List<V>> choices() {
        return null;
    }


    /* This is the one overriden in PropertyDescriptor */
    @Override
    public String propertyErrorFor(Rule rule) {
        List<V> realValues = rule.getProperty(this);
        if (realValues == null && !isRequired()) {
            return null;
        }
        return errorFor(realValues);
    }

    /**
     * Returns a string representation of the default value.
     *
     * @return A string representation of the default value.
     */
    protected String defaultAsString() {
        return asDelimitedString(defaultValue(), multiValueDelimiter());
    }

    /**
     * Parse a string and returns an instance of a value.
     *
     * @param toParse String to parse
     *
     * @return An instance of a value
     */
    protected abstract V createFrom(String toParse);

    @Override
    public List<V> valueFrom(String valueString) throws IllegalArgumentException {
        String[] strValues = valueString.split(Pattern.quote("" + multiValueDelimiter()));

        List<V> values = new ArrayList<>(strValues.length);
        for (int i = 0; i < strValues.length; i++) {
            values.add(createFrom(strValues[i]));
        }

        return values;
    }

}
