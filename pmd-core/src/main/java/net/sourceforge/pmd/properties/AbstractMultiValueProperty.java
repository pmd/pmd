/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.Rule;


/**
 * Multi-valued property.
 *
 * @param <V> The type of the individual values. The multiple values are wrapped into a list.
 *
 * @author Clément Fournier
 * @version 6.0.0
 */
@Deprecated
/* default */ abstract class AbstractMultiValueProperty<V> extends AbstractProperty<List<V>>
        implements MultiValuePropertyDescriptor<V> {


    /** The default value. */
    private final List<V> defaultValue;
    private final char multiValueDelimiter;


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
    AbstractMultiValueProperty(String theName, String theDescription, List<V> theDefault, float theUIOrder,
                               boolean isDefinedExternally) {
        this(theName, theDescription, theDefault, theUIOrder, DEFAULT_DELIMITER, isDefinedExternally);
    }


    /**
     * Creates a multi valued property using a custom delimiter.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theDefault     Default value
     * @param theUIOrder     UI order (must be positive or zero)
     * @param delimiter      The delimiter to separate multiple values
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    AbstractMultiValueProperty(String theName, String theDescription, List<V> theDefault,
                               float theUIOrder, char delimiter, boolean isDefinedExternally) {

        super(theName, theDescription, theUIOrder, isDefinedExternally);
        defaultValue = Collections.unmodifiableList(theDefault);
        multiValueDelimiter = delimiter;
    }


    @Override
    public final boolean isMultiValue() {
        return true;
    }


    /* This is the one overriden in PropertyDescriptor */
    @Override
    public String propertyErrorFor(Rule rule) {
        List<V> realValues = rule.getProperty(this);
        return realValues == null ? null : errorFor(realValues);
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


    private boolean defaultHasNullValue() {
        return defaultValue == null || defaultValue.contains(null);
    }


    /**
     * Returns a string representation of the default value.
     *
     * @return A string representation of the default value.
     */
    @Override
    protected String defaultAsString() {
        return asDelimitedString(defaultValue(), multiValueDelimiter());
    }


    private String asDelimitedString(List<V> values, char delimiter) {
        if (values == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (V value : values) {
            sb.append(asString(value)).append(delimiter);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }


    @Override
    public List<V> defaultValue() {
        return defaultValue;
    }


    @Override
    public char multiValueDelimiter() {
        return multiValueDelimiter;
    }


    /**
     * Returns a string representation of the value, even if it's null.
     *
     * @param value The value to describe
     *
     * @return A string representation of the value
     */
    protected String asString(V value) {
        return value == null ? "" : value.toString();
    }


    @Override
    public final String asDelimitedString(List<V> values) {
        return asDelimitedString(values, multiValueDelimiter());
    }


    @Override
    public List<V> valueFrom(String valueString) throws IllegalArgumentException {
        if (StringUtils.isBlank(valueString)) {
            return Collections.emptyList();
        }

        String[] strValues = valueString.split(Pattern.quote("" + multiValueDelimiter()));

        List<V> values = new ArrayList<>(strValues.length);
        for (String strValue : strValues) {
            values.add(createFrom(strValue));
        }

        return values;
    }


    /**
     * Parse a string and returns an instance of a single value (not a list).
     *
     * @param toParse String to parse
     *
     * @return An instance of a value
     */
    protected abstract V createFrom(String toParse);


    @Override
    protected void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        super.addAttributesTo(attributes);
        attributes.put(PropertyDescriptorField.DELIMITER, Character.toString(multiValueDelimiter()));
    }


}
