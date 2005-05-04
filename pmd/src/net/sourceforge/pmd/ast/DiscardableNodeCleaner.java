package net.sourceforge.pmd.ast;

import java.util.Iterator;
import java.util.List;

public class DiscardableNodeCleaner {

    private static final Class[] clazzes = new Class[]{
        ASTEqualityExpression.class,
        ASTAndExpression.class,
        ASTInstanceOfExpression.class,
        ASTUnaryExpression.class,
        ASTShiftExpression.class,
        ASTConditionalOrExpression.class,
        ASTInclusiveOrExpression.class,
        ASTExclusiveOrExpression.class,
        ASTConditionalExpression.class,
        ASTRelationalExpression.class,
        ASTUnaryExpression.class,
        ASTUnaryExpressionNotPlusMinus.class,
        ASTPostfixExpression.class,
        ASTMultiplicativeExpression.class,
        ASTAdditiveExpression.class,
        ASTConditionalAndExpression.class,
        ASTModifiers.class
    };

    public void clean(ASTCompilationUnit root) {
        for (int i = 0; i < clazzes.length; i++) {
            List nodes = root.findChildrenOfType(clazzes[i]);
            for (Iterator i1 = nodes.iterator(); i1.hasNext();) {
                SimpleNode n = (SimpleNode) i1.next();
                n.discardIfNecessary();
            }
        }
    }

}
