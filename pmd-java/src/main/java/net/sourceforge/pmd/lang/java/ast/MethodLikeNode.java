/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Locale;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;


/**
 * Groups method, constructor and lambda declarations under a common type.
 *
 * @author Clément Fournier
 * @since 6.1.0
 * @deprecated Lambda expressions should not be grouped with other kinds
 *     of method declarations, they have nothing in common. Giving them a
 *     qualified name is hacky and compiler-implementation-dependent.
 *     Ultimately this supertype is not useful and can go away.
 */
@Deprecated
public interface MethodLikeNode extends AccessNode, JavaQualifiableNode, JavaNode {

    /**
     * Returns a token indicating whether this node is a lambda
     * expression or a method or constructor declaration. Can
     * be used to downcast safely to a subinterface or an
     * implementing class.
     *
     * @return The kind of method-like
     * @deprecated Same reason as for {@link TypeKind}
     */
    @Deprecated
    MethodLikeKind getKind();


    /**
     * @deprecated Qualified names are not very useful objects. Use them
     *     to get a nice string for a method, but this is not going
     */
    @Override
    @Deprecated
    JavaOperationQualifiedName getQualifiedName();


    /**
     * Kind of method-like.
     *
     * @deprecated Same reason as for {@link TypeKind}
     */
    @Deprecated
    enum MethodLikeKind {
        METHOD,
        CONSTRUCTOR,
        LAMBDA;

        public String getPrintableName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }


}
