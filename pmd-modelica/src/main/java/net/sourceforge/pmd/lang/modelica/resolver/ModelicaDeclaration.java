/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * Some Modelica entity that is explicitly declared with some name inside some lexical scope.
 */
public interface ModelicaDeclaration extends ResolvableEntity {
    /**
     *  Returns the name of a declaration, such as "RealInput".
     */
    String getSimpleDeclarationName();

    /**
     * Returns the scope in which this symbol is declared.
     */
    ModelicaScope getContainingScope();
}
