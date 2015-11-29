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
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.util.StringUtil;

/**
 *
 * @author Brian Remedios
 * @param <T>
 */
public abstract class AbstractProperty<T> implements PropertyDescriptor<T> {

    private final String name;
    private final String description;
    private final T defaultValue;
    private final boolean isRequired;
    private final float uiOrder;

    /**
     * Default delimiter for multi properties.
     * Note: Numeric properties usual use the {@value #DEFAULT_NUMERIC_DELIMITER}.
     */
    public static final char DEFAULT_DELIMITER = '|';
    /**
     * Default delimiter for numeric properties.
     */
    public static final char DEFAULT_NUMERIC_DELIMITER = ',';

    private char multiValueDelimiter = DEFAULT_DELIMITER;

    protected AbstractProperty(String theName, String theDescription, T theDefault, float theUIOrder) {
        this(theName, theDescription, theDefault, theUIOrder, DEFAULT_DELIMITER);
    }

    /**
     * Constructor for AbstractPMDProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theDefault Object
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    protected AbstractProperty(String theName, String theDescription, T theDefault, float theUIOrder, char delimiter) {
        name = checkNotEmpty(theName, NAME);
        description = checkNotEmpty(theDescription, DESCRIPTION);
        defaultValue = theDefault;
        isRequired = false; // TODO - do we need this?
        uiOrder = checkPositive(theUIOrder, "UI order");
        multiValueDelimiter = delimiter;
    }

    /**
     * @param arg String
     * @param argId String
     * @return String
     * @throws IllegalArgumentException
     */
    private static String checkNotEmpty(String arg, String argId) {

        if (StringUtil.isEmpty(arg)) {
            throw new IllegalArgumentException("Property attribute '" + argId + "' cannot be null or blank");
        }

        return arg;
    }

    /**
     * @param arg float
     * @param argId String
     * @return float
     * @throws IllegalArgumentException
     */
    private static float checkPositive(float arg, String argId) {
        if (arg < 0) {
            throw new IllegalArgumentException("Property attribute " + argId + "' must be zero or positive");
        }
        return arg;
    }

    /**
     * {@inheritDoc}
     */
    public char multiValueDelimiter() {
        return multiValueDelimiter;
    }

    /**
     * {@inheritDoc}
     */
    public String name() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String description() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Method defaultHasNullValue.
     * 
     * @return boolean
     */
    protected boolean defaultHasNullValue() {

        if (defaultValue == null) {
            return true;
        }

        if (isMultiValue() && isArray(defaultValue)) {
            Object[] defaults = (Object[]) defaultValue;
            for (Object default1 : defaults) {
                if (default1 == null) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMultiValue() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * {@inheritDoc}
     */
    public float uiOrder() {
        return uiOrder;
    }

    /**
     * Return the value as a string that can be easily recognized and parsed
     * when we see it again.
     *
     * @param value Object
     * @return String
     */
    protected String asString(Object value) {
        return value == null ? "" : value.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String asDelimitedString(T values) {
        return asDelimitedString(values, multiValueDelimiter());
    }

    /**
     * Return the specified values as a single string using the delimiter.
     * 
     * @param values Object
     * @param delimiter char
     * @return String
     * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
     */
    public String asDelimitedString(T values, char delimiter) {
        if (values == null) {
            return "";
        }

        if (values instanceof Object[]) {
            Object[] valueSet = (Object[]) values;
            if (valueSet.length == 0) {
                return "";
            }
            if (valueSet.length == 1) {
                return asString(valueSet[0]);
            }

            StringBuilder sb = new StringBuilder();
            sb.append(asString(valueSet[0]));
            for (int i = 1; i < valueSet.length; i++) {
                sb.append(delimiter);
                sb.append(asString(valueSet[i]));
            }
            return sb.toString();
        }

        return asString(values);
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(PropertyDescriptor<?> otherProperty) {
        float otherOrder = otherProperty.uiOrder();
        return (int) (otherOrder - uiOrder);
    }

    /**
     * {@inheritDoc}
     */
    public String errorFor(Object value) {

        String typeError = typeErrorFor(value);
        if (typeError != null) {
            return typeError;
        }
        return isMultiValue() ? valuesErrorFor(value) : valueErrorFor(value);
    }

    /**
     * @param value Object
     * @return String
     */
    protected String valueErrorFor(Object value) {

        if (value == null) {
            if (defaultHasNullValue()) {
                return null;
            }
            return "missing value";
        }
        return null;
    }

    /**
     * @param value Object
     * @return String
     */
    protected String valuesErrorFor(Object value) {

        if (!isArray(value)) {
            return "multiple values expected";
        }

        Object[] values = (Object[]) value;

        String err = null;
        for (Object value2 : values) {
            err = valueErrorFor(value2);
            if (err != null) {
                return err;
            }
        }

        return null;
    }

    /**
     * @param value Object
     * @return boolean
     */
    protected static boolean isArray(Object value) {
        return value != null && value.getClass().getComponentType() != null;
    }

    /**
     * @param value Object
     * @return String
     */
    protected String typeErrorFor(Object value) {

        if (value == null && !isRequired) {
            return null;
        }

        if (isMultiValue()) {
            if (!isArray(value)) {
                return "Value is not an array of type: " + type();
            }

            Class<?> arrayType = value.getClass().getComponentType();
            if (arrayType == null || !arrayType.isAssignableFrom(type().getComponentType())) {
                return "Value is not an array of type: " + type();
            }
            return null;
        }

        if (!type().isAssignableFrom(value.getClass())) {
            return value + " is not an instance of " + type();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String propertyErrorFor(Rule rule) {
        Object realValue = rule.getProperty(this);
        if (realValue == null && !isRequired()) {
            return null;
        }
        return errorFor(realValue);
    }

    /**
     * {@inheritDoc}
     */
    public Object[][] choices() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int preferredRowCount() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[PropertyDescriptor: name=" + name() + ", type=" + type() + ", value=" + defaultValue() + "]";
    }

    /**
     * @return String
     */
    protected String defaultAsString() {
        if (isMultiValue()) {
            return asDelimitedString(defaultValue(), multiValueDelimiter());
        } else {
            return defaultValue().toString();
        }
    }

    /**
     * @param value Object
     * @param otherValue Object
     * @return boolean
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static final boolean areEqual(Object value, Object otherValue) {
        if (value == otherValue) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (otherValue == null) {
            return false;
        }

        return value.equals(otherValue);
    }

    /**
     * @return Map<String,String>
     */
    public Map<String, String> attributeValuesById() {

        Map<String, String> values = new HashMap<>();
        addAttributesTo(values);
        return values;
    }

    /**
     * @param attributes Map<String,String>
     */
    protected void addAttributesTo(Map<String, String> attributes) {
        attributes.put(NAME, name);
        attributes.put(DESCRIPTION, description);
        attributes.put(DEFAULT_VALUE, defaultAsString());
        if (isMultiValue()) {
            attributes.put(PropertyDescriptorFields.DELIMITER, Character.toString(multiValueDelimiter()));
        }
    }

}
