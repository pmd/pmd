/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorField;

/** Builder for an expected fields map. */
public final class ExpectedFieldsBuilder {

    private final Map<PropertyDescriptorField, Boolean> requiredFields = new HashMap<>();


    private ExpectedFieldsBuilder() {

    }


    /**
     * Adds a mapping of field/ required to the map.
     *
     * @param field      The field to expect
     * @param isRequired Whether it's required or not
     *
     * @return This instance, so that we have a fluent interface
     */
    public ExpectedFieldsBuilder put(PropertyDescriptorField field, boolean isRequired) {
        requiredFields.put(field, isRequired);
        return this;
    }


    /**
     * Gets an immutable map containing the fields we've put here.
     *
     * @return The map of field/ isRequired mappings
     */
    public Map<PropertyDescriptorField, Boolean> build() {
        return Collections.unmodifiableMap(requiredFields);
    }


    /**
     * Gets a builder for a required fields map.
     *
     * @return A builder
     *
     * @see ExpectedFieldsBuilder
     */
    public static ExpectedFieldsBuilder instance() {
        return new ExpectedFieldsBuilder();
    }

}
