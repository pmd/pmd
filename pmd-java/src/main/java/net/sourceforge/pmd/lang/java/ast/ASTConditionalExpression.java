/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a conditional expression, aka ternary expression.
 *
 * <pre class="grammar">
 *
 * ConditionalExpression ::= {@linkplain ASTExpression Expression} "?"  {@linkplain ASTExpression Expression} ":" {@linkplain ASTExpression Expression}
 *
 * </pre>
 */
public final class ASTConditionalExpression extends AbstractJavaExpr {


    private boolean isStandalone;

    ASTConditionalExpression(int id) {
        super(id);
    }


    /**
     * Returns the node that represents the guard of this conditional.
     * That is the expression before the '?'.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to true.
     */
    public ASTExpression getThenBranch() {
        return (ASTExpression) getChild(1);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to false.
     */
    public ASTExpression getElseBranch() {
        return (ASTExpression) getChild(2);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Note: this method is not used at all in pre-Java 8 analysis,
     * because standalone/poly exprs weren't formalized before java 8.
     * Calling this method then is undefined.
     */
    // very internal
    boolean isStandalone() {
        assert getAstInfo().getLanguageVersion().compareToVersion("8") >= 0
            : "This method's result is undefined in pre java 8 code";
        return this.isStandalone;
    }

    void setStandaloneTernary() {
        this.isStandalone = true;
    }
}
