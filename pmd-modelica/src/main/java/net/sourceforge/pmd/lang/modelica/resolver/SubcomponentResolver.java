/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * This interface represents something that, being looked up by some prefix of composite name,
 * may resolve further name parts. Lexically, this is represented by a dot-notation.
 *
 * Please do not confuse this with {@link ModelicaScope} representing <b>lexical</b> scope,
 * that resolves names as if they are written in the corresponding part of the source file.
 */
public interface SubcomponentResolver {
    /**
     * Resolves `<code>name</code> as if resolving subcomponents through the type of base component
     *
     * @param state The resolution state
     * @param name  The name to resolve
     * @return The resolved declarations
     */
    <T extends ResolvableEntity> ResolutionResult<T> safeResolveComponent(Class<T> clazz, ResolutionState state, CompositeName name);
}
