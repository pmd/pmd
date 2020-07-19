/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.QualifierOwner;

/**
 * A field access expression.
 *
 * <pre class="grammar">
 *
 * FieldAccess ::= {@link ASTExpression Expression} "." &lt;IDENTIFIER&gt;
 *
 * </pre>
 */
public final class ASTFieldAccess extends AbstractJavaExpr implements ASTAssignableExpr, QualifierOwner {

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


    /** Returns the name of the field. */
    public String getFieldName() {
        return getImage();
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
