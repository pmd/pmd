/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorField;
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

    protected final PropertyDescriptor<T> propertyDescriptor;


    /* default */ PropertyDescriptorWrapper(PropertyDescriptor<T> propertyDescriptor) {
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("PropertyDescriptor cannot be null.");
        }
        this.propertyDescriptor = propertyDescriptor;
    }


    @Override
    public String asDelimitedString(T value) {
        return propertyDescriptor.asDelimitedString(value);
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


    @Override
    public Class<?> type() {
        return propertyDescriptor.type();
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
    public Map<PropertyDescriptorField, String> attributeValuesById() {
        return propertyDescriptor.attributeValuesById();
    }


    @Override
    public boolean isDefinedExternally() {
        return false;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyDescriptorWrapper) {
            return this.propertyDescriptor.equals(((PropertyDescriptorWrapper<?>) obj).getPropertyDescriptor());
        }
        return this.propertyDescriptor.equals(obj);
    }


    /**
     * Returns the underlying property descriptor.
     *
     * @return The underlying property descriptor
     */
    public PropertyDescriptor<T> getPropertyDescriptor() {
        return propertyDescriptor;
    }


    @Override
    public int hashCode() {
        return this.getPropertyDescriptor().hashCode();
    }


    @Override
    public String toString() {
        return "wrapped:" + propertyDescriptor.toString();
    }


    /**
     * Gets the wrapper of this property descriptor.
     *
     * @param desc The descriptor
     * @param <T>  The type of the descriptor
     *
     * @return The wrapper of this descriptor
     */
    public static <T> PropertyDescriptorWrapper<T> getWrapper(PropertyDescriptor<T> desc) {
        return ((AbstractProperty<T>) desc).getWrapper();
    }

}
