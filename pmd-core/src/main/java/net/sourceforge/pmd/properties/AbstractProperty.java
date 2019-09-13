/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.PropertyDescriptorField.DESCRIPTION;
import static net.sourceforge.pmd.properties.PropertyDescriptorField.NAME;

import org.apache.commons.lang3.StringUtils;


/**
 * Abstract class for properties.
 *
 * @param <T> The type of the property's value. This is a list type for multi-valued properties
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version Refactored June 2017 (6.0.0)
 */
// @Deprecated // will be replaced by another base class in the next PR
/* default */ abstract class AbstractProperty<T> implements PropertyDescriptor<T> {

    private final String name;
    private final String description;


    /**
     * Constructor for an abstract property.
     *
     * @param theName        Name of the property
     * @param theDescription Description
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException If name or description are empty, or UI order is negative.
     */
    protected AbstractProperty(String theName, String theDescription, float theUIOrder) {
        if (theUIOrder < 0) {
            throw new IllegalArgumentException("Property attribute 'UI order' cannot be null or blank");
        }

        name = checkNotEmpty(theName, NAME);
        description = checkNotEmpty(theDescription, DESCRIPTION);
    }


    @Override
    public String description() {
        return description;
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
        return "[PropertyDescriptor: name=" + name() + ','
               + " value=" + defaultValue() + ']';
    }


    @Override
    public String name() {
        return name;
    }


    private static String checkNotEmpty(String arg, PropertyDescriptorField argId) throws IllegalArgumentException {
        if (StringUtils.isBlank(arg)) {
            throw new IllegalArgumentException("Property attribute '" + argId + "' cannot be null or blank");
        }
        return arg;
    }


}
