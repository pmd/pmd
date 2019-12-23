/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import java.util.List;

/**
 * A <b>lexical</b> scope of Modelica code.
 * That is, a component declaration does not have one, it is its type that does (but these may be resolved to multiple
 * classes or not resolved at all, these classes generally reside in other files, etc.)
 *
 * Please do not confuse this with {@link SubcomponentResolver} that represents "view from the outside" on something
 * possibly looked up from other file via component reference.
 */
public interface ModelicaScope {
    /**
     * Returns the declarations that were lexically declared in this scope.
     */
    List<ModelicaDeclaration> getContainedDeclarations();

    /**
     * Resolves a name as if it is written inside this lexical scope in a file.
     */
    <T extends ResolvableEntity> ResolutionResult<T> safeResolveLexically(Class<T> clazz, ResolutionState state, CompositeName name);

    /**
     * Returns the parent (i.e., containing) scope.
     */
    ModelicaScope getParent();

    RootScope getRoot();
}
