/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Groups method, constructor and lambda declarations under a common type.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public interface MethodLikeNode extends AccessNode, JavaQualifiableNode, JavaNode {

    /**
     * Returns a token indicating whether this node is a lambda
     * expression or a method or constructor declaration. Can
     * be used to downcast safely to a subinterface or an
     * implementing class.
     *
     * @return The kind of method-like
     */
    MethodLikeKind getKind();


    @Override
    JavaOperationQualifiedName getQualifiedName();


    /** Kind of method-like. */
    enum MethodLikeKind {
        METHOD,
        CONSTRUCTOR,
        LAMBDA
    }


}
