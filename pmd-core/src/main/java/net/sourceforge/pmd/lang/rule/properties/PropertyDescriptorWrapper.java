/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;

/**
 * This class serves as a wrapper class for a PropertyDescriptor instance. It
 * exists to allowing the {@link PropertyDescriptorFactory} to readily flag
 * properties it has created, versus those created by Rule classes. This is used
 * in the encoding of a Rule to XML format to distinguish Rule defined
 * PropertyDescriptors and those which were originally defined in XML.
 * 
 * @param <T> The type of the underlying PropertyDescriptor.
 */
public class PropertyDescriptorWrapper<T> implements PropertyDescriptor<T> {
    private final PropertyDescriptor<T> propertyDescriptor;

    public PropertyDescriptorWrapper(PropertyDescriptor<T> propertyDescriptor) {
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("PropertyDescriptor cannot be null.");
        }
        this.propertyDescriptor = propertyDescriptor;
    }

    public PropertyDescriptor<T> getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public String asDelimitedString(T value) {
        return propertyDescriptor.asDelimitedString(value);
    }

    public Object[][] choices() {
        return propertyDescriptor.choices();
    }

    public int compareTo(PropertyDescriptor<?> o) {
        return propertyDescriptor.compareTo(o);
    }

    public T defaultValue() {
        return propertyDescriptor.defaultValue();
    }

    public String description() {
        return propertyDescriptor.description();
    }

    public String errorFor(Object value) {
        return propertyDescriptor.errorFor(value);
    }

    public boolean isMultiValue() {
        return propertyDescriptor.isMultiValue();
    }

    public boolean isRequired() {
        return propertyDescriptor.isRequired();
    }

    public char multiValueDelimiter() {
        return propertyDescriptor.multiValueDelimiter();
    }

    public String name() {
        return propertyDescriptor.name();
    }

    public int preferredRowCount() {
        return propertyDescriptor.preferredRowCount();
    }

    public String propertyErrorFor(Rule rule) {
        return propertyDescriptor.propertyErrorFor(rule);
    }

    public Class<T> type() {
        return propertyDescriptor.type();
    }

    public float uiOrder() {
        return propertyDescriptor.uiOrder();
    }

    public T valueFrom(String propertyString) throws IllegalArgumentException {
        return propertyDescriptor.valueFrom(propertyString);
    }

    public Map<String, String> attributeValuesById() {
        return propertyDescriptor.attributeValuesById();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyDescriptorWrapper) {
            return this.getPropertyDescriptor().equals(((PropertyDescriptorWrapper<?>) obj).getPropertyDescriptor());
        }
        return this.getPropertyDescriptor().equals(obj);
    }

    @Override
    public int hashCode() {
        return this.getPropertyDescriptor().hashCode();
    }

    @Override
    public String toString() {
        return "wrapped:" + propertyDescriptor.toString();
    }
}
