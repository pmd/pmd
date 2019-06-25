/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A switch expression, as introduced in Java 12. This node only occurs
 * in the contexts where an expression is expected. In particular, if
 * switch constructs occurring in statement position are parsed as a
 * {@linkplain ASTSwitchStatement SwitchStatement}, and not a
 * {@link ASTSwitchExpression SwitchExpression} within a
 * {@link ASTStatementExpression StatementExpression}. That is
 * because switch statements are not required to be exhaustive, contrary
 * to switch expressions.
 *
 * <p>Their syntax is identical though.
 *
 * <p>TODO Do we group the statements of a SwitchNormalBlock ? Do we unify
 * their interface ? SwitchStatement implements Iterator&lt;SwitchLabel&gt;
 * which seems shitty tbh.
 *
 * <pre class="grammar">
 *
 * SwitchExpression  ::= "switch" "(" {@link ASTExpression Expression} ")" SwitchBlock
 *
 * SwitchBlock       ::= SwitchArrowBlock | SwitchNormalBlock
 *
 * SwitchArrowBlock  ::=  "{" ( {@link ASTSwitchLabeledRule SwitchLabeledRule} )* "}"
 * SwitchNormalBlock ::=  "{" ( {@linkplain ASTSwitchLabel SwitchLabel} {@linkplain ASTBlockStatement BlockStatement}* )* "}"
 *
 * </pre>
 */
public final class ASTSwitchExpression extends AbstractJavaExpr implements ASTExpression {

    ASTSwitchExpression(int id) {
        super(id);
    }

    ASTSwitchExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Gets the expression tested by this switch.
     * This is the expression between the parentheses.
     */
    public ASTExpression getTestedExpression() {
        return (ASTExpression) jjtGetChild(0);
    }

}
