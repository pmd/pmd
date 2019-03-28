/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Base class for {@link ASTAdditiveExpression} and {@link ASTMultiplicativeExpression},
 * which use the same parsing scheme.
 *
 * @author Clément Fournier
 */
abstract class AbstractLrBinaryExpr extends AbstractJavaTypeNode implements ASTExpression, LeftRecursiveNode {

    private BinaryOp operator;

    AbstractLrBinaryExpr(int i) {
        super(i);
    }

    AbstractLrBinaryExpr(JavaParser p, int i) {
        super(p, i);
    }


    @Override
    public void jjtClose() {
        super.jjtClose();

        // At this point the expression is fully left-recursive
        // If any of its left children are also AdditiveExpressions with the same operator,
        // we adopt their children to flatten the node

        AbstractJavaNode first = (AbstractJavaNode) jjtGetChild(0);
        if (first instanceof ASTAdditiveExpression && ((ASTAdditiveExpression) first).getOp() == getOp()) {
            flatten(0);
        }
    }


    @Override
    public void setImage(String image) {
        super.setImage(image);
        this.operator = BinaryOp.fromImage(image);
    }

    /**
     * Returns the image of the operator, i.e. "+" or "-".
     */
    public String getOperator() {
        return getImage();
    }

    public BinaryOp getOp() {
        return operator;
    }
}
