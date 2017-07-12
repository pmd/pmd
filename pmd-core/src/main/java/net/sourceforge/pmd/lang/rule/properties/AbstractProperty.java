/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.PropertyDescriptorField.DEFAULT_VALUE;
import static net.sourceforge.pmd.PropertyDescriptorField.DESCRIPTION;
import static net.sourceforge.pmd.PropertyDescriptorField.NAME;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Abstract class for properties.
 *
 * @param <T> The type of the property's value. This is a list type for multi-valued properties
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
/* default */ abstract class AbstractProperty<T> implements PropertyDescriptor<T> {

    private final String name;
    private final String description;
    private final float uiOrder;


    /**
     * Constructor for an abstract property.
     *
     * @param theName        Name of the property
     * @param theDescription Description
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractProperty(String theName, String theDescription,
                               float theUIOrder) {
        if (theUIOrder < 0) {
            throw new IllegalArgumentException("Property attribute 'UI order' cannot be null or blank");
        }

        name = checkNotEmpty(theName, NAME);
        description = checkNotEmpty(theDescription, DESCRIPTION);
        uiOrder = theUIOrder;
    }


    private static String checkNotEmpty(String arg, PropertyDescriptorField argId) throws IllegalArgumentException {
        if (StringUtil.isEmpty(arg)) {
            throw new IllegalArgumentException("Property attribute '" + argId + "' cannot be null or blank");
        }
        return arg;
    }


    @Override
    public String description() {
        return description;
    }


    @Override
    public float uiOrder() {
        return uiOrder;
    }


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
        return "[PropertyDescriptor: name=" + name() + ","
            + " type=" + (isMultiValue() ? "List<" + type() + ">" : type()) + ","
            + " value=" + defaultValue() + "]";
    }


    @Override
    public String name() {
        return name;
    }


    @Override
    public final Map<PropertyDescriptorField, String> attributeValuesById() {
        Map<PropertyDescriptorField, String> values = new HashMap<>();
        addAttributesTo(values);
        return values;
    }


    /**
     * Adds this property's attributes to the map. Subclasses can override this to add more
     * {@link PropertyDescriptorField}.
     *
     * @param attributes The map to fill
     */
    protected void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        attributes.put(NAME, name);
        attributes.put(DESCRIPTION, description);
        attributes.put(DEFAULT_VALUE, defaultAsString());
    }


    /**
     * Returns a string representation of the default value.
     *
     * @return A string representation of the default value.
     */
    protected abstract String defaultAsString();


    /**
     * Gets a wrapper for this property. This method ensures that a wrapper of a property always implements the same
     * interfaces as that property using double dispatch.
     *
     * @return A wrapper for this property.
     */
    /* default */ abstract PropertyDescriptorWrapper<T> getWrapper();

}
