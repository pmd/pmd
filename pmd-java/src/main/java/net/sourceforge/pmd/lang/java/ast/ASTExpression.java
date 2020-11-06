/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an expression, in the most general sense.
 * This corresponds roughly to the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-AssignmentExpression">AssignmentExpression</a>
 * of the JLS. One difference though, is that this production
 * also matches lambda expressions, contrary to the JLS.
 *
 * <pre>
 *
 * Expression ::= {@linkplain ASTConditionalExpression ConditionalExpression} ( {@linkplain ASTAssignmentOperator AssignmentOperator} Expression )?
 *
 * </pre>
 */
public class ASTExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isStandAlonePrimitive() {
        if (getNumChildren() != 1) {
            return false;
        }

        ASTPrimaryExpression primaryExpression = getFirstChildOfType(ASTPrimaryExpression.class);

        if (primaryExpression == null || primaryExpression.getNumChildren() != 1) {
            return false;
        }

        ASTPrimaryPrefix primaryPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);

        if (primaryPrefix == null || primaryPrefix.getNumChildren() != 1) {
            return false;
        }

        ASTLiteral literal = primaryPrefix.getFirstChildOfType(ASTLiteral.class);

        // if it is not a string literal and not a null, then it is one of
        // byte, short, char, int, long, float, double, boolean
        return literal != null && !literal.isStringLiteral()
            && (literal.getNumChildren() == 0 || !(literal.getChild(0) instanceof ASTNullLiteral));
    }
}
