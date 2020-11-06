/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * Some Modelica <i>type</i> (either class or built-in type) that some component may have.
 */
public interface ModelicaType extends ResolvableEntity {
    /**
     * Returns short name of a type, such as "Real" or "Filter".
     */
    String getSimpleTypeName();

    /**
     * Returns the fully-qualified name, when appropriate, or simple name for primitive types.
     */
    String getFullTypeName();
}
