/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * Interface for entities that can be looked up.
 */
public interface ResolvableEntity {
    /**
     * Returns some name to be shown to user in violation description.
     */
    String getDescriptiveName();
}
