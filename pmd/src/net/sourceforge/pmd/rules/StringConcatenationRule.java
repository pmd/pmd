/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class StringConcatenationRule extends AbstractJavaRule {

    public Object visit(ASTForStatement node, Object data) {
        Node forLoopStmt = null;
        for (int i = 0; i < 4; i++) {
            forLoopStmt = node.jjtGetChild(i);
            if (forLoopStmt instanceof ASTBlockStatement) {
                break;
            }
        }
        if (forLoopStmt == null) {
            return data;
        }


        return data;
    }
}
