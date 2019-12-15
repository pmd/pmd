/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;


/**
 * A method declaration, in a class or interface declaration. Since 7.0,
 * this also represents annotation methods. Annotation methods have a
 * much more restricted grammar though, in particular:
 * <ul>
 * <li>They can't declare a {@linkplain #getThrowsList() throws clause}
 * <li>They can't declare {@linkplain #getTypeParameters() type parameters}
 * <li>Their {@linkplain #getFormalParameters() formal parameters} must be empty
 * <li>They can't be declared void
 * <li>They must be abstract
 * </ul>
 * They can however declare a {@link #getDefaultClause() default value}.
 *
 * <pre class="grammar">
 *
 * MethodDeclaration ::= MethodModifier*
 *                       {@link ASTTypeParameters TypeParameters}?
 *                       {@link ASTResultType ResultType}
 *                       &lt;IDENTIFIER&gt;
 *                       {@link ASTFormalParameters FormalParameters}
 *                       {@link ASTArrayDimensions ArrayDimensions}?
 *                       {@link ASTThrowsList ThrowsList}?
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
     *
     * @deprecated Use {@link #getName()}
     */
    @Deprecated
    public String getMethodName() {
        return getName();
    }


    /** Returns the simple name of the method. */
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
            && ((ASTClassOrInterfaceDeclaration) potentialTypeDeclaration).isInterface()
            || potentialTypeDeclaration instanceof ASTAnnotationTypeDeclaration;
    }


    /**
     * Returns true if the result type of this method is {@code void}.
     */
    public boolean isVoid() {
        return getResultType().isVoid();
    }


    /**
     * Returns the default clause, if this is an annotation method declaration
     * that features one. Otherwise returns null.
     */
    @Nullable
    public ASTDefaultValue getDefaultClause() {
        return AstImplUtil.getChildAs(this, jjtGetNumChildren() - 1, ASTDefaultValue.class);
    }

    /**
     * Returns the result type node of the method.
     */
    public ASTResultType getResultType() {
        return getFirstChildOfType(ASTResultType.class);
    }

    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.METHOD;
    }

    /**
     * Returns the extra array dimensions that may be after the
     * formal parameters.
     */
    @Nullable
    public ASTArrayDimensions getExtraDimensions() {
        return children(ASTArrayDimensions.class).first();
    }

}
