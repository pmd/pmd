/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.Arrays;
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

    // setProperty should probably be hidden from Rule implementations, they have no business using that
    // The apex rules that do that could be refactored to do it in the XML

    // TODO RuleReference should extend this class
    // This would avoid duplicating the implementation between Rule and RuleReference,
    // which should use exactly the same mechanism to override properties (XML).

    // BUT to do that RuleReference should not extend AbstractDelegateRule
    // Indeed, AbstractDelegateRule has no business having this overriding logic built-in,
    // it would break its contract. For all deeds and purposes that class should be removed.

    // TODO 7.0.0 these fields should be made private final

    /**
     * The list of known properties that can be configured.
     *
     * @deprecated Will be made private final
     */
    @Deprecated
    protected List<PropertyDescriptor<?>> propertyDescriptors = new ArrayList<>();

    /**
     * The values for each property that were overridden here.
     * Default property values are not contained in this map.
     * In other words, if this map doesn't contain a descriptor
     * which is in {@link #propertyDescriptors}, then it's assumed
     * to have a default value.
     *
     * @deprecated Will be made private final
     */
    @Deprecated
    protected Map<PropertyDescriptor<?>, Object> propertyValuesByDescriptor = new HashMap<>();


    /**
     * Creates a copied list of the property descriptors and returns it.
     *
     * @return a copy of the property descriptors.
     * @deprecated Just use {@link #getPropertyDescriptors()}
     */
    @Deprecated
    protected List<PropertyDescriptor<?>> copyPropertyDescriptors() {
        return new ArrayList<>(propertyDescriptors);
    }


    /**
     * Creates a copied map of the values of the properties and returns it.
     *
     * @return a copy of the values
     *
     * @deprecated Just use {@link #getPropertiesByPropertyDescriptor()} or {@link #getOverriddenPropertiesByPropertyDescriptor()}
     */
    @Deprecated
    protected Map<PropertyDescriptor<?>, Object> copyPropertyValues() {
        return new HashMap<>(propertyValuesByDescriptor);
    }


    @Override
    public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
        // Check to ensure the property does not already exist.
        if (getPropertyDescriptor(propertyDescriptor.name()) != null) {
            throw new IllegalArgumentException("There is already a PropertyDescriptor with name '"
                                                       + propertyDescriptor.name() + "' defined on " + getPropertySourceType() + " " + getName() + ".");

        }
        propertyDescriptors.add(propertyDescriptor);
        // Sort in UI order
        Collections.sort(propertyDescriptors);
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


    @Override
    @Deprecated
    public <V> void setProperty(MultiValuePropertyDescriptor<V> propertyDescriptor, V... values) {
        checkValidPropertyDescriptor(propertyDescriptor);
        propertyValuesByDescriptor.put(propertyDescriptor, Collections.unmodifiableList(Arrays.asList(values)));
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


    // todo Java 8 move up to interface
    @Override
    public String dysfunctionReason() {
        for (PropertyDescriptor<?> descriptor : getOverriddenPropertyDescriptors()) {
            String error = errorForPropCapture(descriptor);
            if (error != null) {
                return error;
            }
        }
        return null;
    }


    private <T> String errorForPropCapture(PropertyDescriptor<T> descriptor) {
        return descriptor.errorFor(getProperty(descriptor));
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
            String valueString = ((PropertyDescriptor) propertyDescriptor).asDelimitedString(value);
            propertiesWithValues.put(propertyDescriptor.name(), valueString);
        });
        Map<String, String> thatPropertiesWithValues = new HashMap<>();
        that.propertyDescriptors.forEach(propertyDescriptor -> {
            Object value = that.propertyValuesByDescriptor.getOrDefault(propertyDescriptor, propertyDescriptor.defaultValue());
            @SuppressWarnings({"unchecked", "rawtypes"})
            String valueString = ((PropertyDescriptor) propertyDescriptor).asDelimitedString(value);
            thatPropertiesWithValues.put(propertyDescriptor.name(), valueString);
        });
        return Objects.equals(propertiesWithValues, thatPropertiesWithValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyDescriptors, propertyValuesByDescriptor);
    }
}
