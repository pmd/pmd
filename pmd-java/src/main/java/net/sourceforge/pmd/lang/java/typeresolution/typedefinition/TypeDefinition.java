/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

public interface TypeDefinition {
    /**
     * Get the raw Class type of the definition.
     *
     * @return Raw Class type.
     */
    Class<?> getType();
}
