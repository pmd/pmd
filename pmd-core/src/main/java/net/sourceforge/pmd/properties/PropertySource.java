/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Entity that manages a list of properties. Properties are described by
 * {@linkplain PropertyDescriptor property descriptors}. A property source
 * maintains a mapping of property descriptors to property values. The value
 * of a property can be set by {@link #setProperty(PropertyDescriptor, Object)}.
 * If the property wasn't set with this method, then the property is assumed
 * to take a default value, which is specified by {@linkplain PropertyDescriptor#defaultValue() the property descriptor}.
 *
 * <p>Bad configuration of the properties may be reported by {@link #dysfunctionReason()}.
 *
 * <p>Notable instances of this interface are {@linkplain net.sourceforge.pmd.Rule rules} and
 * {@linkplain net.sourceforge.pmd.renderers.Renderer renderers}.
 *
 * @author Brian Remedios
 */
public interface PropertySource {

    /**
     * Defines a new property. Properties must be defined before they can be set a value.
     *
     * @param propertyDescriptor The property descriptor.
     *
     * @throws IllegalArgumentException If there is already a property defined the same name.
     */
    void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) throws IllegalArgumentException;


    /**
     * Gets the name of this property source. This is e.g. the name
     * of the rule or renderer.
     *
     * @return The name
     */
    String getName();


    /**
     * Get the PropertyDescriptor for the given property name.
     *
     * @param name The name of the property.
     *
     * @return The PropertyDescriptor for the named property, <code>null</code> if there is no such property defined.
     */
    PropertyDescriptor<?> getPropertyDescriptor(String name);


    /**
     * Get the descriptors of all defined properties.
     * The properties are returned sorted by UI order.
     *
     * @return The PropertyDescriptors in UI order.
     */
    List<PropertyDescriptor<?>> getPropertyDescriptors();


    /**
     * Returns a modifiable list of the property descriptors
     * that don't use default values.
     *
     * @return The descriptors that don't use default values
     */
    List<PropertyDescriptor<?>> getOverriddenPropertyDescriptors();

    /**
     * Get the typed value for the given property.
     * Multi valued properties return immutable lists.
     *
     * @param <T>                The underlying type of the property descriptor.
     * @param propertyDescriptor The property descriptor.
     *
     * @return The property value.
     */
    <T> T getProperty(PropertyDescriptor<T> propertyDescriptor);


    /**
     * Returns true if the given property has been set to a value
     * somewhere in the XML.
     *
     * @param propertyDescriptor The descriptor
     *
     * @return True if the property has been set
     */
    boolean isPropertyOverridden(PropertyDescriptor<?> propertyDescriptor);

    /**
     * Set the property value specified. This is also referred to as "overriding"
     * the (default) value of a property.
     *
     * @param <T>                The underlying type of the property descriptor.
     * @param propertyDescriptor The property descriptor.
     * @param value              The value to set.
     */
    <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value);


    /**
     * Sets the value of a multi value property descriptor with a variable number of arguments.
     * This is also referred to as "overriding" the (default) value of a property.
     *
     * @param propertyDescriptor The property descriptor for which to add a value
     * @param values             Values
     * @param <V>                The type of the values
     *
     * @deprecated {@link MultiValuePropertyDescriptor} is deprecated
     */
    @Deprecated
    <V> void setProperty(MultiValuePropertyDescriptor<V> propertyDescriptor, V... values);


    /**
     * Returns an unmodifiable map of descriptors to property values
     * for the current receiver. The returned map has an entry for
     * every defined descriptor ({@link #getPropertyDescriptors()}),
     * if they were not specified explicitly, then default values are
     * used.
     *
     * @return An unmodifiable map of descriptors to property values
     */
    Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor();


    /**
     * Returns a modifiable map of the property descriptors
     * that don't use default values, to their overridden value.
     * Modifications on the returned map don't affect this property
     * source.
     *
     * @return The descriptors that don't use default values
     */
    Map<PropertyDescriptor<?>, Object> getOverriddenPropertiesByPropertyDescriptor();


    /**
     * Returns whether the specified property is defined on this property source,
     * in which case it can be set or retrieved with {@link #getProperty(PropertyDescriptor)}
     * and {@link #setProperty(PropertyDescriptor, Object)}.
     *
     * @param descriptor The descriptor to look for
     *
     * @return {@code true} if the descriptor is defined, {@code false} otherwise.
     */
    boolean hasDescriptor(PropertyDescriptor<?> descriptor);


    /**
     * Returns whether this Rule uses default values for properties.
     *
     * @return boolean <code>true</code> if the properties all have default values, <code>false</code> otherwise.
     *
     * @deprecated Has no real utility, will be removed by 7.0.0
     */
    @Deprecated
    boolean usesDefaultValues();


    /**
     * Clears out any user-specified value for the property allowing it to use the default value in the descriptor.
     *
     * @param desc the property to clear out
     *
     * @deprecated Has no real utility, and the name is confusing, will be removed by 7.0.0
     */
    @Deprecated
    void useDefaultValueFor(PropertyDescriptor<?> desc);


    /**
     * Return the properties that are effectively ignored due to the configuration of the rule and values held by other
     * properties. This can be used to disable corresponding widgets in a UI.
     *
     * @return the properties that are ignored
     * @deprecated Has no real utility, will be removed by 7.0.0
     */
    @Deprecated
    Set<PropertyDescriptor<?>> ignoredProperties();


    /**
     * Returns a description of why the receiver may be dysfunctional.
     * Usually due to missing property values or some kind of conflict
     * between values. Returns null if the receiver is ok.
     *
     * @return String
     */
    String dysfunctionReason();
}
