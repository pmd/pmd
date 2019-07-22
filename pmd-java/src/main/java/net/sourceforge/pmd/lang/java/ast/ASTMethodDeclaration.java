/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;


/**
 * A method declaration, in a class or interface declaration. This cannot
 * be found in {@linkplain ASTAnnotationTypeDeclaration annotation types},
 * which instead have {@linkplain ASTAnnotationMethodDeclaration annotation methods}.
 *
 * <pre class="grammar">
 *
 * MethodDeclaration ::= MethodModifier*
 *                       {@link ASTTypeParameters TypeParameters}?
 *                       {@link ASTResultType ResultType}
 *                       {@link ASTMethodDeclarator MethodDeclarator}
 *                       ("throws" {@link ASTNameList NameList})?
 *                       ({@link ASTBlock Block} | ";" )
 *
 *
 * MethodModifier ::= "public" | "private"  | "protected" | "static"
 *                  | "final"  | "abstract" | "native"
 *                  | {@linkplain ASTAnnotation Annotation}
 *
 * </pre>
 */
public final class ASTMethodDeclaration extends AbstractMethodOrConstructorDeclaration implements DFAGraphMethod {


    @InternalApi
    @Deprecated
    public ASTMethodDeclaration(int id) {
        super(id);
    }

    ASTMethodDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Returns the simple name of the method.
     */
    public String getMethodName() {
        return getName();
    }

    /**
     * Returns the simple name of the method.
     */
    @Override
    public String getName() {
        return getImage();
    }


    /**
     * Returns true if this method is explicitly modified by
     * the {@code public} modifier.
     */
    public boolean isSyntacticallyPublic() {
        return super.isPublic();
    }


    /**
     * Returns true if this method is explicitly modified by
     * the {@code abstract} modifier.
     */
    public boolean isSyntacticallyAbstract() {
        return super.isAbstract();
    }


    /**
     * Returns true if this method has public visibility.
     * Non-private interface members are implicitly public,
     * whether they declare the {@code public} modifier or
     * not.
     */
    @Override
    public boolean isPublic() {
        // interface methods are public by default, but could be private since java9
        return isInterfaceMember() && !isPrivate() || super.isPublic();
    }


    /**
     * Returns true if this method is abstract, so doesn't
     * declare a body. Interface members are
     * implicitly abstract, whether they declare the
     * {@code abstract} modifier or not. Default interface
     * methods are not abstract though, consistently with the
     * standard reflection API.
     */
    @Override
    public boolean isAbstract() {
        return isInterfaceMember() && !isDefault() || super.isAbstract();
    }


    /**
     * Returns true if this method declaration is a member of an interface type.
     */
    public boolean isInterfaceMember() {
        // for a real class/interface the 3rd parent is a ClassOrInterfaceDeclaration,
        // for anonymous classes, the parent is e.g. a AllocationExpression
        Node potentialTypeDeclaration = getNthParent(3);

        return potentialTypeDeclaration instanceof ASTClassOrInterfaceDeclaration
            && ((ASTClassOrInterfaceDeclaration) potentialTypeDeclaration).isInterface();
    }


    /**
     * Returns true if the result type of this method is {@code void}.
     */
    public boolean isVoid() {
        return getResultType().isVoid();
    }


    /**
     * Returns the result type node of the method.
     */
    public ASTResultType getResultType() {
        return getFirstChildOfType(ASTResultType.class);
    }


    /**
     * Returns the exception names listed in the {@code throws} clause
     * of this method declaration, or null if there are none.
     */
    public ASTNameList getThrows() {
        return getFirstChildOfType(ASTNameList.class);
    }


    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.METHOD;
    }

    public ASTTypeParameters getTypeParameters() {
        return getFirstChildOfType(ASTTypeParameters.class);
    }


}
