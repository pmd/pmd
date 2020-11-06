/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents an {@code if} statement, possibly with an {@code else} statement.
 *
 * <pre>
 *
 * IfStatement ::= "if" "(" {@linkplain ASTExpression Expression} ")" {@linkplain ASTStatement Statement}
 *                 ( "else" {@linkplain ASTStatement Statement} )?
 *
 * </pre>
 */
public class ASTIfStatement extends AbstractJavaNode {

    private boolean hasElse;


    @InternalApi
    @Deprecated
    public ASTIfStatement(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTIfStatement(JavaParser p, int id) {
        super(p, id);
    }


    @InternalApi
    @Deprecated
    public void setHasElse() {
        this.hasElse = true;
    }


    /**
     * Returns true if this statement has an {@code else} clause.
     */
    public boolean hasElse() {
        return this.hasElse;
    }


    /**
     * Returns the node that represents the guard of this conditional.
     * This may be any expression of type boolean.
     *
     * @deprecated Use {@link #getCondition()}
     */
    @Deprecated
    public ASTExpression getGuardExpressionNode() {
        return (ASTExpression) getChild(0);
    }

    /**
     * Returns the node that represents the guard of this conditional.
     * This may be any expression of type boolean.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns the statement that will be run if the guard evaluates
     * to true.
     */
    public ASTStatement getThenBranch() {
        return (ASTStatement) getChild(1);
    }


    /**
     * Returns the statement of the {@code else} clause, if any.
     */
    public ASTStatement getElseBranch() {
        return hasElse() ? (ASTStatement) getChild(2) : null;
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
