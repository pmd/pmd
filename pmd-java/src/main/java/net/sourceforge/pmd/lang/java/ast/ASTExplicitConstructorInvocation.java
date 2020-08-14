/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JMethodSig;

/**
 * An explicit constructor invocation, occurring at the start of a
 * {@linkplain ASTConstructorDeclaration constructor declaration}.
 *
 * <p>See <a href="https://docs.oracle.com/javase/specs/jls/se11/html/jls-8.html#jls-8.8.7.1">JLS 8.8.7.1</a>.
 *
 * <pre class="grammar">
 *
 * ExplicitConstructorInvocation ::= {@link ASTTypeArguments TypeArguments}? "this" {@link ASTArgumentList ArgumentList} ";"
 *                                 | {@link ASTTypeArguments TypeArguments}? "super" {@link ASTArgumentList ArgumentList} ";"
 *                                 | {@link ASTExpression Expression} "." {@link ASTTypeArguments TypeArguments}? "super" {@link ASTArgumentList ArgumentList} ";"
 *
 * </pre>
 */
public final class ASTExplicitConstructorInvocation extends AbstractJavaTypeNode
    implements InvocationNode, ASTStatement {

    private boolean isSuper;
    private JMethodSig methodType;
    private boolean varargsPhase;

    ASTExplicitConstructorInvocation(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    @NonNull
    public ASTArgumentList getArguments() {
        return (ASTArgumentList) getLastChild();
    }

    /**
     * Returns the number of arguments of the called constructor.
     */
    public int getArgumentCount() {
        return getArguments().size();
    }

    void setIsSuper() {
        this.isSuper = true;
    }

    /**
     * Returns true if this statement calls a constructor of the same
     * class. The JLS calls that an <i>alternate constructor invocation</i>.
     */
    public boolean isThis() {
        return !isSuper;
    }

    /**
     * Returns true if this statement calls a constructor of the direct
     * superclass. The JLS calls that a <i>superclass constructor invocation</i>.
     */
    public boolean isSuper() {
        return isSuper;
    }

    /**
     * Returns true if this is a qualified superclass constructor invocation.
     * They allow a subclass constructor to explicitly specify the newly created
     * object's immediately enclosing instance with respect to the direct
     * superclass (§8.1.3). This may be necessary when the superclass is
     * an inner class.
     */
    public boolean isQualified() {
        return getFirstChild() instanceof ASTPrimaryExpression;
    }

    @Override
    @Nullable
    public ASTTypeArguments getExplicitTypeArguments() {
        return getFirstChildOfType(ASTTypeArguments.class);
    }

    /**
     * Returns the qualifying expression if this is a {@linkplain #isQualified() qualified superclass
     * constructor invocation}.
     */
    @Nullable
    public ASTExpression getQualifier() {
        return AstImplUtil.getChildAs(this, 0, ASTExpression.class);
    }


    @Override
    public JMethodSig getMethodType() {
        getTypeMirror();
        return methodType;
    }

    @Override
    public boolean isVarargsCall() {
        getTypeMirror();
        return varargsPhase;
    }

    void setMethodType(JMethodSig methodType, boolean varargsPhase) {
        this.methodType = methodType;
        this.varargsPhase = varargsPhase;
    }

}
