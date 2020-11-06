/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Basic interface for qualified names usable in the metrics framework.
 * Qualified names identify unambiguously operations and classes across
 * the analysed project. Language specific pmd modules should have at
 * most one implementation of this interface, to allow safe downcasting
 * from QualifiedName to e.g. JavaQualifiedName.
 *
 * @author Clément Fournier
 * @deprecated Not useful anymore
 */
@Deprecated
public interface QualifiedName {

    @Override
    String toString();


    /**
     * Returns the qualified name of the class the resource is located in. If this instance addresses a class, returns
     * this instance.
     *
     * @return The qualified name of the class
     */
    QualifiedName getClassName();


    /**
     * Returns true if the resource addressed by this qualified name is a class.
     *
     * @return true if the resource addressed by this qualified name is a class.
     */
    boolean isClass();


    /**
     * Returns true if the resource addressed by this qualified name is an operation.
     *
     * @return true if the resource addressed by this qualified name is an operation.
     */
    boolean isOperation();


}
