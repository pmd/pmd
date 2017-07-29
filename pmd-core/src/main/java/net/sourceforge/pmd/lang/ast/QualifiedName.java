/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Basic interface for qualified names. That's used in the metrics framework to refer unambiguously to operations or
 * classes. Language specific pmd modules should have at most one implementation of this interface, to allow safe
 * downcasting from QualifiedName to e.g. JavaQualifiedName.
 *
 * @author Clément Fournier
 */
public interface QualifiedName {

    @Override
    String toString();


    /**
     * Returns the operation specific part of the name. It identifies an operation in its namespace.
     *
     * @return The operation string.
     */
    String getOperation();


    /**
     * Returns the class specific part of the name. It identifies a class in the namespace it's declared in. If the
     * class is nested inside another, then the array returned contains all enclosing classes in order.
     *
     * @return The class names array.
     */
    String[] getClasses();

}
