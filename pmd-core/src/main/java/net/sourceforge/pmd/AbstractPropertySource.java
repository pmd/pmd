/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Base class for objects which can be configured through properties. Rules and
 * Reports are such objects.
 * 
 * @author Brian Remedios
 */
public abstract class AbstractPropertySource implements PropertySource {

    /** The list of known properties that can be configured. */
    protected List<PropertyDescriptor<?>> propertyDescriptors = new ArrayList<>();
    /** The values for each property. */
    protected Map<PropertyDescriptor<?>, Object> propertyValuesByDescriptor = new HashMap<>();

    /**
     * Creates a copied list of the property descriptors and returns it.
     * 
     * @return a copy of the property descriptors.
     */
    protected List<PropertyDescriptor<?>> copyPropertyDescriptors() {
        List<PropertyDescriptor<?>> copy = new ArrayList<>(propertyDescriptors.size());
        copy.addAll(propertyDescriptors);
        return copy;
    }

    /**
     * Creates a copied map of the values of the properties and returns it.
     * 
     * @return a copy of the values
     */
    protected Map<PropertyDescriptor<?>, Object> copyPropertyValues() {
        Map<PropertyDescriptor<?>, Object> copy = new HashMap<>(
                propertyValuesByDescriptor.size());
        copy.putAll(propertyValuesByDescriptor);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    public Set<PropertyDescriptor<?>> ignoredProperties() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
        // Check to ensure the property does not already exist.
        for (PropertyDescriptor<?> descriptor : propertyDescriptors) {
            if (descriptor.name().equals(propertyDescriptor.name())) {
                throw new IllegalArgumentException("There is already a PropertyDescriptor with name '"
                        + propertyDescriptor.name() + "' defined on Rule " + getName() + ".");
            }
        }
        propertyDescriptors.add(propertyDescriptor);
        // Sort in UI order
        Collections.sort(propertyDescriptors);
    }

    /**
     * Gets the name of the property source. This is e.g. the rule name or the report name.
     * @return the name
     */
    public abstract String getName();

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyDescriptor<?> getPropertyDescriptor(String name) {
        for (PropertyDescriptor<?> propertyDescriptor : propertyDescriptors) {
            if (name.equals(propertyDescriptor.name())) {
                return propertyDescriptor;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {

        if (propertyValuesByDescriptor.isEmpty()) {
            propertyValuesByDescriptor = getPropertiesByPropertyDescriptor();
        }

        return propertyValuesByDescriptor.containsKey(descriptor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PropertyDescriptor<?>> getPropertyDescriptors() {
        return propertyDescriptors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getProperty(PropertyDescriptor<T> propertyDescriptor) {
        checkValidPropertyDescriptor(propertyDescriptor);
        T result = propertyDescriptor.defaultValue();
        if (propertyValuesByDescriptor.containsKey(propertyDescriptor)) {
            @SuppressWarnings("unchecked")
            T value = (T) propertyValuesByDescriptor.get(propertyDescriptor);
            result = value;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        checkValidPropertyDescriptor(propertyDescriptor);
        propertyValuesByDescriptor.put(propertyDescriptor, value);
    }

    private void checkValidPropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
        if (!propertyDescriptors.contains(propertyDescriptor)) {
            throw new IllegalArgumentException("Property descriptor not defined for Rule " + getName() + ": "
                    + propertyDescriptor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
        if (propertyDescriptors.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor = new HashMap<>(
                propertyDescriptors.size());
        // Fill with existing explicitly values
        propertiesByPropertyDescriptor.putAll(this.propertyValuesByDescriptor);

        // Add default values for anything not yet set
        for (PropertyDescriptor<?> propertyDescriptor : this.propertyDescriptors) {
            if (!propertiesByPropertyDescriptor.containsKey(propertyDescriptor)) {
                propertiesByPropertyDescriptor.put(propertyDescriptor, propertyDescriptor.defaultValue());
            }
        }

        return propertiesByPropertyDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean usesDefaultValues() {

        Map<PropertyDescriptor<?>, Object> valuesByProperty = getPropertiesByPropertyDescriptor();
        if (valuesByProperty.isEmpty()) {
            return true;
        }

        Iterator<Map.Entry<PropertyDescriptor<?>, Object>> iter = valuesByProperty.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<PropertyDescriptor<?>, Object> entry = iter.next();
            if (!CollectionUtil.areEqual(entry.getKey().defaultValue(), entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useDefaultValueFor(PropertyDescriptor<?> desc) {
        propertyValuesByDescriptor.remove(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String dysfunctionReason() {
        return null;
    }
}