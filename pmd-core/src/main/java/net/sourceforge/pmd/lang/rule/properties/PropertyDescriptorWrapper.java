/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

    @Override
    public String asDelimitedString(T value) {
        return propertyDescriptor.asDelimitedString(value);
    }

    @Override
    public Set<Entry<String, T>> choices() {
        return propertyDescriptor.choices();
    }

    @Override
    public int compareTo(PropertyDescriptor<?> o) {
        return propertyDescriptor.compareTo(o);
    }

    @Override
    public T defaultValue() {
        return propertyDescriptor.defaultValue();
    }

    @Override
    public String description() {
        return propertyDescriptor.description();
    }

    @Override
    public String errorFor(T value) {
        return propertyDescriptor.errorFor(value);
    }

    @Override
    public boolean isMultiValue() {
        return propertyDescriptor.isMultiValue();
    }

    @Override
    public boolean isRequired() {
        return propertyDescriptor.isRequired();
    }

    @Override
    public char multiValueDelimiter() {
        return propertyDescriptor.multiValueDelimiter();
    }

    @Override
    public String name() {
        return propertyDescriptor.name();
    }

    @Override
    public int preferredRowCount() {
        return propertyDescriptor.preferredRowCount();
    }

    @Override
    public String propertyErrorFor(Rule rule) {
        return propertyDescriptor.propertyErrorFor(rule);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> type() {
        Class<?> clazz = propertyDescriptor.type();
        Class<T> result = null;
        try {
            result = (Class<T>) clazz;
        } catch (ClassCastException e1) {
            try {
                result = (Class<T>) List.class;
            } catch (ClassCastException e2) {

            }
        }

        return result;
    }

    @Override
    public float uiOrder() {
        return propertyDescriptor.uiOrder();
    }

    @Override
    public T valueFrom(String propertyString) throws IllegalArgumentException {
        return propertyDescriptor.valueFrom(propertyString);
    }

    @Override
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
