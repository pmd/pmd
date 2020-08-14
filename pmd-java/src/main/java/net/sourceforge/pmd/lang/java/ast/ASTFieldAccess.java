/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedAssignableExpr;

/**
 * A field access expression.
 *
 * <pre class="grammar">
 *
 * FieldAccess ::= {@link ASTExpression Expression} "." &lt;IDENTIFIER&gt;
 *
 * </pre>
 */
public final class ASTFieldAccess extends AbstractJavaExpr implements ASTNamedAssignableExpr, QualifiableExpression {

    ASTFieldAccess(int id) {
        super(id);
    }


    /**
     * Promotes an ambiguous name to the LHS of this node.
     */
    ASTFieldAccess(ASTAmbiguousName lhs, String fieldName) {
        super(JavaParserImplTreeConstants.JJTFIELDACCESS);
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
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
