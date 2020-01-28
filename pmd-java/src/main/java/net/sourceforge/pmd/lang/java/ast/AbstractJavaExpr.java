/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

abstract class AbstractJavaExpr extends AbstractJavaTypeNode implements ASTExpression {

    private int parenDepth;

    AbstractJavaExpr(int i) {
        super(i);
    }


    void bumpParenDepth() {
        parenDepth++;
    }

    @Override
    public int getParenthesisDepth() {
        return parenDepth;
    }
}
