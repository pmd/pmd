/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;

/**
 * A field access expression.
 *
 * <pre class="grammar">
 *
 * FieldAccess ::= {@link ASTExpression Expression} "." &lt;IDENTIFIER&gt;
 *
 * </pre>
 */
public final class ASTFieldAccess extends AbstractJavaExpr implements ASTNamedReferenceExpr, QualifiableExpression {

    private FieldSig typedSym;


    ASTFieldAccess(int id) {
        super(id);
    }

    /**
     * Promotes an ambiguous name to the LHS of this node.
     */
    ASTFieldAccess(ASTAmbiguousName lhs, String fieldName) {
        super(JavaParserImplTreeConstants.JJTFIELDACCESS);
        assert fieldName != null;
        this.addChild(lhs, 0);
        this.setImage(fieldName);
    }

    ASTFieldAccess(ASTExpression lhs, JavaccToken identifier) {
        super(JavaParserImplTreeConstants.JJTFIELDACCESS);
        TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);
        this.addChild((AbstractJavaNode) lhs, 0);
        this.setImage(identifier.getImage());
        this.setFirstToken(lhs.getFirstToken());
        this.setLastToken(identifier);
    }


    @Override
    public @NonNull ASTExpression getQualifier() {
        return (ASTExpression) getChild(0);
    }


    @Override
    public String getName() {
        return getImage();
    }

    @Override
    public @Nullable FieldSig getSignature() {
        forceTypeResolution();
        return typedSym;
    }

    @Override
    public @Nullable JFieldSymbol getReferencedSym() {
        return (JFieldSymbol) ASTNamedReferenceExpr.super.getReferencedSym();
    }

    void setTypedSym(@Nullable FieldSig sig) {
        this.typedSym = sig;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
