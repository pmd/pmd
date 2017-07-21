/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.PropertyDescriptorField.LEGAL_PACKAGES;

import java.util.Map;

/**
 * Defines a property descriptor type whose values can be described by qualified names and thus restricted to only some
 * packages. These typically use values such as {@link Class} and {@link java.lang.reflect.Method}.
 *
 * @param <T> type of the property value
 *
 * @author Clément Fournier
 */
public interface PackagedPropertyDescriptor<T> extends PropertyDescriptor<T> {

    /** Required additional fields. */
    Map<PropertyDescriptorField, Boolean> PACKAGED_FIELD_TYPES_BY_KEY
        = AbstractPropertyDescriptorFactory.expectedFieldTypesWith(new PropertyDescriptorField[] {LEGAL_PACKAGES},
                                                                   new Boolean[] {false});

    /** Delimiter used to separate package names. */
    char PACKAGE_NAME_DELIMITER = ' ';
    /** Delimiter used to separate multiple values if this descriptor is multi valued. */
    char MULTI_VALUE_DELIMITER = '|';


    /**
     * Returns the legal package names.
     *
     * @return The legal package names
     */
    String[] legalPackageNames();
}
