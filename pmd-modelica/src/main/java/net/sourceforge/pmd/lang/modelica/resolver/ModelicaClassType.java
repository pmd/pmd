/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * A Modelica type that is defined as a <i>class</i> (i.e., class, package, model, etc.).
 */
public interface ModelicaClassType extends ModelicaType, ModelicaDeclaration, SubcomponentResolver {
    /**
     * Returns the class specialization (i.e., package, model, function, etc.)
     */
    ModelicaClassSpecialization getSpecialization();

    /**
     * Returns whether this class is some kind of connector.
     */
    boolean isConnectorLike();

    /**
     * Returns whether this class is encapsulated.
     */
    boolean isEncapsulated();

    /**
     * Returns whether this class is partial.
     */
    boolean isPartial();

    /**
     * Returns the scope defined by this class itself.
     */
    ModelicaClassScope getClassScope();
}
