/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Any entity that manages a list of properties is a {@link PropertySource}.
 * These are e.g. Rules and Renderers.
 * @author Brian Remedios
 */
public interface PropertySource {

    /**
     * Define a new property via a PropertyDescriptor.
     * 
     * @param propertyDescriptor The property descriptor.
     * @throws IllegalArgumentException If there is already a property defined
     *             the same name.
     */
    void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) throws IllegalArgumentException;

    /**
     * Get the PropertyDescriptor for the given property name.
     * 
     * @param name The name of the property.
     * @return The PropertyDescriptor for the named property, <code>null</code>
     *         if there is no such property defined.
     */
    PropertyDescriptor<?> getPropertyDescriptor(String name);

    /**
     * Get the PropertyDescriptors for all defined properties. The properties
     * are returned sorted by UI order.
     * 
     * @return The PropertyDescriptors in UI order.
     */
    List<PropertyDescriptor<?>> getPropertyDescriptors();

    /**
     * Get the typed value for the given property.
     * 
     * @param <T> The underlying type of the property descriptor.
     * @param propertyDescriptor The property descriptor.
     * @return The property value.
     */
    <T> T getProperty(PropertyDescriptor<T> propertyDescriptor);

    /**
     * Set the property value specified (will be type-checked)
     * 
     * @param <T> The underlying type of the property descriptor.
     * @param propertyDescriptor The property descriptor.
     * @param value The value to set.
     */
    <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value);

    /**
     * Returns all the current property values for the receiver or an immutable
     * empty map if none are specified.
     * @return all current property values or a empty map.
     */
    Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor();

    /**
     * Returns whether this Rule has the specified PropertyDescriptor.
     * 
     * @param descriptor The PropertyDescriptor for which to check.
     * @return boolean <code>true</code> if the descriptor is present,
     *         <code>false</code> otherwise.
     */
    boolean hasDescriptor(PropertyDescriptor<?> descriptor);

    /**
     * Returns whether this Rule uses default values for properties.
     * 
     * @return boolean <code>true</code> if the properties all have default
     *         values, <code>false</code> otherwise.
     */
    boolean usesDefaultValues();

    /**
     * Clears out any user-specified value for the property allowing it to use
     * the default value in the descriptor.
     * 
     * @param desc the property to clear out
     */
    void useDefaultValueFor(PropertyDescriptor<?> desc);

    /**
     * Return the properties that are effectively ignored due to the
     * configuration of the rule and values held by other properties. This can
     * be used to disable corresponding widgets in a UI.
     * 
     * @return the properties that are ignored
     */
    Set<PropertyDescriptor<?>> ignoredProperties();

    /**
     * Returns a description of why the receiver may be dysfunctional. Usually
     * due to missing property values or some kind of conflict between values.
     * Returns null if the receiver is ok.
     * 
     * @return String
     */
    String dysfunctionReason();
}
