package net.sourceforge.pmd.ast;

import java.util.Iterator;
import java.util.List;

public class DiscardableNodeCleaner {

    private static final Class[] clazzes = new Class[]{
        ASTUnaryExpression.class,
        ASTUnaryExpressionNotPlusMinus.class,
        ASTPostfixExpression.class,
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
