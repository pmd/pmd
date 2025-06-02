/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Base class for {@link PropertySource}.
 *
 * @author Brian Remedios
 */
public abstract class AbstractPropertySource implements PropertySource {

    // TODO setProperty should probably be hidden from Rule implementations, they have no business using that
    // The apex rules that do that could be refactored to do it in the XML

    /**
     * The list of known properties that can be configured.
     */
    private final List<PropertyDescriptor<?>> propertyDescriptors = new ArrayList<>();

    /**
     * The values for each property that were overridden here.
     * Default property values are not contained in this map.
     * In other words, if this map doesn't contain a descriptor
     * which is in {@link #propertyDescriptors}, then it's assumed
     * to have a default value.
     */
    private final Map<PropertyDescriptor<?>, Object> propertyValuesByDescriptor = new HashMap<>();


    @Override
    public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
        // Check to ensure the property does not already exist.
        if (getPropertyDescriptor(propertyDescriptor.name()) != null) {
            throw new IllegalArgumentException("There is already a PropertyDescriptor with name '"
                                                       + propertyDescriptor.name() + "' defined on " + getPropertySourceType() + " " + getName() + ".");

        }
        propertyDescriptors.add(propertyDescriptor);
    }


    protected abstract String getPropertySourceType();

    @Override
    public PropertyDescriptor<?> getPropertyDescriptor(String name) {
        for (PropertyDescriptor<?> propertyDescriptor : propertyDescriptors) {
            if (name.equals(propertyDescriptor.name())) {
                return propertyDescriptor;
            }
        }
        return null;
    }


    @Override
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
        return propertyDescriptors.contains(descriptor);
    }


    @Override
    public final List<PropertyDescriptor<?>> getOverriddenPropertyDescriptors() {
        return new ArrayList<>(propertyValuesByDescriptor.keySet());
    }


    @Override
    public List<PropertyDescriptor<?>> getPropertyDescriptors() {
        return Collections.unmodifiableList(propertyDescriptors);
    }


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


    @Override
    public boolean isPropertyOverridden(PropertyDescriptor<?> propertyDescriptor) {
        return propertyValuesByDescriptor.containsKey(propertyDescriptor);
    }


    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        checkValidPropertyDescriptor(propertyDescriptor);
        if (value instanceof List) {
            propertyValuesByDescriptor.put(propertyDescriptor, Collections.unmodifiableList((List) value));
        } else {
            propertyValuesByDescriptor.put(propertyDescriptor, value);
        }
    }


    /**
     * Checks whether this property descriptor is defined for this property source.
     *
     * @param propertyDescriptor The property descriptor to check
     */
    private void checkValidPropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
        if (!hasDescriptor(propertyDescriptor)) {
            throw new IllegalArgumentException("Property descriptor not defined for " + getPropertySourceType() + " " + getName() + ": " + propertyDescriptor);
        }
    }


    @Override
    public final Map<PropertyDescriptor<?>, Object> getOverriddenPropertiesByPropertyDescriptor() {
        return new HashMap<>(propertyValuesByDescriptor);
    }


    @Override
    public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
        if (propertyDescriptors.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor = new HashMap<>(propertyDescriptors.size());
        // Fill with existing explicitly values
        propertiesByPropertyDescriptor.putAll(this.propertyValuesByDescriptor);

        // Add default values for anything not yet set
        for (PropertyDescriptor<?> propertyDescriptor : this.propertyDescriptors) {
            if (!propertiesByPropertyDescriptor.containsKey(propertyDescriptor)) {
                propertiesByPropertyDescriptor.put(propertyDescriptor, propertyDescriptor.defaultValue());
            }
        }

        return Collections.unmodifiableMap(propertiesByPropertyDescriptor);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractPropertySource that = (AbstractPropertySource) o;

        if (!Objects.equals(propertyDescriptors, that.propertyDescriptors)) {
            return false;
        }

        // Convert the values to strings for comparisons. This is needed at least for RegexProperties,
        // as java.util.regex.Pattern doesn't implement equals().
        Map<String, String> propertiesWithValues = new HashMap<>();
        propertyDescriptors.forEach(propertyDescriptor -> {
            Object value = propertyValuesByDescriptor.getOrDefault(propertyDescriptor, propertyDescriptor.defaultValue());
            @SuppressWarnings({"unchecked", "rawtypes"})
            String valueString = ((PropertyDescriptor) propertyDescriptor).serializer().toString(value);
            propertiesWithValues.put(propertyDescriptor.name(), valueString);
        });
        Map<String, String> thatPropertiesWithValues = new HashMap<>();
        that.propertyDescriptors.forEach(propertyDescriptor -> {
            Object value = that.propertyValuesByDescriptor.getOrDefault(propertyDescriptor, propertyDescriptor.defaultValue());
            @SuppressWarnings({"unchecked", "rawtypes"})
            String valueString = ((PropertyDescriptor) propertyDescriptor).serializer().toString(value);
            thatPropertiesWithValues.put(propertyDescriptor.name(), valueString);
        });
        return Objects.equals(propertiesWithValues, thatPropertiesWithValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyDescriptors, propertyValuesByDescriptor);
    }
}
