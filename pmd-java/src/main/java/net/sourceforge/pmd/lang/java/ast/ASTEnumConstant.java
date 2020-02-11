/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JMethodSig;

/**
 * Represents an enum constant declaration within an {@linkplain ASTEnumDeclaration enum type declaration}.
 *
 * <pre class="grammar">
 *
 * EnumConstant ::= {@link ASTModifierList AnnotationList} {@link ASTVariableDeclaratorId VariableDeclaratorId} {@linkplain ASTArgumentList ArgumentList}? {@linkplain ASTAnonymousClassDeclaration AnonymousClassDeclaration}?
 *
 * </pre>
 */
public final class ASTEnumConstant extends AbstractJavaTypeNode
    implements Annotatable,
               InvocationNode,
               AccessNode,
               ASTBodyDeclaration,
               InternalInterfaces.VariableIdOwner {

    private JMethodSig calledConstructor;
    private boolean varargsPhase;

    ASTEnumConstant(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }

    @Override
    public String getImage() {
        return getVarId().getImage();
    }

    @Override
    @Nullable
    public ASTArgumentList getArguments() {
        return getFirstChildOfType(ASTArgumentList.class);
    }

    /**
     * Returns the name of the enum constant.
     */
    public String getName() {
        return getImage();
    }

    /**
     * Returns true if this enum constant defines a body,
     * which is compiled like an anonymous class.
     */
    public boolean isAnonymousClass() {
        return getLastChild() instanceof ASTAnonymousClassDeclaration;
    }

    /**
     * Returns the anonymous class declaration, or null if
     * there is none.
     */
    public ASTAnonymousClassDeclaration getAnonymousClass() {
        return AstImplUtil.getChildAs(this, getNumChildren() - 1, ASTAnonymousClassDeclaration.class);
    }

    @Override
    public @Nullable ASTTypeArguments getExplicitTypeArguments() {
        // no syntax for that
        return null;
    }

    @Override
    public JMethodSig getMethodType() {
        getTypeMirror();
        return calledConstructor;
    }

    @Override
    public boolean isVarargsCall() {
        getTypeMirror();
        return varargsPhase;
    }

    void setCalledConstructor(JMethodSig calledConstructor, boolean varargsPhase) {
        this.calledConstructor = calledConstructor;
        this.varargsPhase = varargsPhase;
    }
}
