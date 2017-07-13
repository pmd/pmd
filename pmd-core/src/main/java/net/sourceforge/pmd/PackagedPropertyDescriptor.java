/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

/**
 * Defines a property descriptor type whose values can be described by qualified names and thus restricted to only some
 * packages. These typically use values such as {@link Class} and {@link java.lang.reflect.Method}.
 *
 * @param <T> type of the property value
 *
 * @author Clément Fournier
 */
public interface PackagedPropertyDescriptor<T> extends PropertyDescriptor<T> {

    /**
     * Returns the legal package names.
     *
     * @return The legal package names
     */
    String[] legalPackageNames();
}
