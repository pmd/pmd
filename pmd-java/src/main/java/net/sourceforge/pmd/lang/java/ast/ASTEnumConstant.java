/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;

/**
 * Represents an enum constant declaration within an {@linkplain ASTEnumDeclaration enum type declaration}.
 *
 * <pre class="grammar">
 *
 * EnumConstant ::= {@link ASTModifierList AnnotationList} {@link ASTVariableId VariableId} {@linkplain ASTArgumentList ArgumentList}? {@linkplain ASTAnonymousClassDeclaration AnonymousClassDeclaration}?
 *
 * </pre>
 */
public final class ASTEnumConstant extends AbstractJavaTypeNode
    implements InvocationNode,
               ModifierOwner,
               ASTBodyDeclaration,
               InternalInterfaces.VariableIdOwner,
               JavadocCommentOwner {

    private OverloadSelectionResult result;

    ASTEnumConstant(int id) {
        super(id);
    }


    @Override
    public FileLocation getReportLocation() {
        return getVarId().getFirstToken().getReportLocation();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public ASTVariableId getVarId() {
        return firstChild(ASTVariableId.class);
    }

    @Override
    public String getImage() {
        return getName();
    }

    @Override
    @Nullable
    public ASTArgumentList getArguments() {
        return firstChild(ASTArgumentList.class);
    }

    /**
     * Returns the name of the enum constant.
     */
    public String getName() {
        return getVarId().getName();
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

    void setOverload(OverloadSelectionResult result) {
        assert result != null;
        this.result = result;
    }

    @Override
    public OverloadSelectionResult getOverloadSelectionInfo() {
        forceTypeResolution();
        return assertNonNullAfterTypeRes(result);
    }
}
