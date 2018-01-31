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
public abstract class MethodLike extends AbstractJavaAccessNode implements JavaQualifiableNode, JavaNode {
    private JavaQualifiedName qualifiedName;


    public MethodLike(int i) {
        super(i);
    }


    public MethodLike(JavaParser parser, int i) {
        super(parser, i);
    }


    // TODO refine that type to be more specific when we split JavaQualifiedName into a hierarchy
    @Override
    public JavaQualifiedName getQualifiedName() {
        if (qualifiedName == null) {
            qualifiedName = QualifiedNameFactory.ofOperation(this);
        }
        return qualifiedName;
    }


    /**
     * Returns a token indicating whether this node is a lambda
     * expression or a method or constructor declaration. Can
     * be used to downcast safely to a subinterface or an
     * implementing class.
     *
     * @return The kind of method-like
     */
    public abstract MethodLikeKind getKind();


    /** Kind of method-like. */
    public enum MethodLikeKind {
        METHOD,
        CONSTRUCTOR,
        LAMBDA
    }


}
