/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * Internal base class for Modelica declarations, see ${@link ModelicaDeclaration} for public API.
 */
abstract class AbstractModelicaDeclaration implements ModelicaDeclaration {
    /**
     * Resolves further name components, supposing previous ones resolved to this declaration.
     *
     * @param result Where to place resolution results
     * @param name   Further name parts to resolve
     * @throws Watchdog.CountdownException if too many resolution steps were performed
     */
    abstract void resolveFurtherNameComponents(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException;
}
