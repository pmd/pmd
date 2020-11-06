/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import net.sourceforge.pmd.annotation.InternalApi;

@Deprecated
@InternalApi
public interface TypeDefinition {
    /**
     * Get the raw Class type of the definition.
     *
     * @return Raw Class type.
     */
    Class<?> getType();
}
