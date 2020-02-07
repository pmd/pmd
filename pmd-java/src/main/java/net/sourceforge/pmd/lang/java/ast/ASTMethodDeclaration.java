/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
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
 * MethodDeclaration ::= {@link ASTModifierList ModifierList}
 *                       {@link ASTTypeParameters TypeParameters}?
 *                       {@link ASTResultType ResultType}
 *                       &lt;IDENTIFIER&gt;
 *                       {@link ASTFormalParameters FormalParameters}
 *                       {@link ASTArrayDimensions ArrayDimensions}?
 *                       {@link ASTThrowsList ThrowsList}?
 *                       ({@link ASTBlock Block} | ";" )
 *
 * </pre>
 */
public final class ASTMethodDeclaration extends AbstractMethodOrConstructorDeclaration implements DFAGraphMethod {


    @InternalApi
    @Deprecated
    public ASTMethodDeclaration(int id) {
        super(id);
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
     * Returns true if the result type of this method is {@code void}.
     *
     * TODO remove, just as simple to write getResultType().isVoid()
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
        return AstImplUtil.getChildAs(this, getNumChildren() - 1, ASTDefaultValue.class);
    }

    /**
     * Returns the result type node of the method.
     */
    public ASTResultType getResultType() {
        return getFirstChildOfType(ASTResultType.class);
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
