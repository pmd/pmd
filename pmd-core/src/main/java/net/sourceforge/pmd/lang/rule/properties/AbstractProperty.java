/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorFields.DEFAULT_VALUE;
import static net.sourceforge.pmd.PropertyDescriptorFields.DESCRIPTION;
import static net.sourceforge.pmd.PropertyDescriptorFields.NAME;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFields;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Abstract class for properties.
 *
 * @param <T> The type of the values.
 *
 * @author Brian Remedios
 */
public abstract class AbstractProperty<T> implements PropertyDescriptor<T> {

    /**
     * Default delimiter for multi properties. Note: Numeric properties usual
     * use the {@value #DEFAULT_NUMERIC_DELIMITER}.
     */
    public static final char DEFAULT_DELIMITER = '|';

    /**
     * Default delimiter for numeric properties.
     */
    public static final char DEFAULT_NUMERIC_DELIMITER = ',';

    private final String name;
    private final String description;
    private final boolean isRequired;
    private final float uiOrder;
    private char multiValueDelimiter = DEFAULT_DELIMITER;


    /**
     * Creates an AbstractProperty using the default delimiter {@link #DEFAULT_DELIMITER}.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theUIOrder     UI order (must be positive or zero)
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractProperty(String theName, String theDescription, float theUIOrder) {
        this(theName, theDescription, theUIOrder, DEFAULT_DELIMITER);
    }

    /**
     * Constructor for AbstractPMDProperty.
     *
     * @param theName        Name of the property (must not be empty)
     * @param theDescription Description (must not be empty)
     * @param theUIOrder     UI order (must be positive or zero)
     * @param delimiter      The delimiter to separate multi values
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractProperty(String theName, String theDescription,
                               float theUIOrder, char delimiter) {
        if (theUIOrder < 0) {
            throw new IllegalArgumentException("Property attribute 'UI order' cannot be null or blank");
        }

        name = checkNotEmpty(theName, NAME);
        description = checkNotEmpty(theDescription, DESCRIPTION);
        isRequired = false; // TODO - do we need this?
        uiOrder = theUIOrder;
        multiValueDelimiter = delimiter;
    }


    private static String checkNotEmpty(String arg, String argId) throws IllegalArgumentException {
        if (StringUtil.isEmpty(arg)) {
            throw new IllegalArgumentException("Property attribute '" + argId + "' cannot be null or blank");
        }
        return arg;
    }

    /**
     * Tests if two values are equal.
     *
     * @param value      First value
     * @param otherValue Object
     *
     * @return True if the two values are equal.
     *
     * @deprecated Never used in pmd's codebase + is just an alias for Object#equals.
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static boolean areEqual(Object value, Object otherValue) {
        return value != null && value.equals(otherValue);
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public float uiOrder() {
        return uiOrder;
    }

    @Override
    public char multiValueDelimiter() {
        return multiValueDelimiter;
    }

    @Override
    public final String asDelimitedString(T values) {
        return asDelimitedString(values, multiValueDelimiter());
    }

    /**
     * Return the specified values as a single string using the delimiter.
     *
     * @param values    Values to format
     * @param delimiter char
     *
     * @return String
     *
     * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(T)
     */
    public abstract String asDelimitedString(T values, char delimiter);

    @Override
    public final int compareTo(PropertyDescriptor<?> otherProperty) {
        float otherOrder = otherProperty.uiOrder();
        return (int) (otherOrder - uiOrder);
    }


    @Override
    public int preferredRowCount() {
        return 1;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof PropertyDescriptor) {
            return name.equals(((PropertyDescriptor<?>) obj).name());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }


    @Override
    public String toString() {
        return "[PropertyDescriptor: name=" + name() + ", type=" + type() + ", value=" + defaultValue() + "]";
    }

    /**
     * Returns a string representation of the default value.
     *
     * @return A string representation of the default value.
     */
    protected abstract String defaultAsString();

    @Override
    public Map<String, String> attributeValuesById() {
        Map<String, String> values = new HashMap<>();
        addAttributesTo(values);
        return values;
    }

    protected void addAttributesTo(Map<String, String> attributes) {
        attributes.put(NAME, name);
        attributes.put(DESCRIPTION, description);
        attributes.put(DEFAULT_VALUE, defaultAsString());
        if (isMultiValue()) {
            attributes.put(PropertyDescriptorFields.DELIMITER, Character.toString(multiValueDelimiter()));
        }
    }

}
