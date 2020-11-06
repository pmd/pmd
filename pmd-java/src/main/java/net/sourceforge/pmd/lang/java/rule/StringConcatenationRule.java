/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;

//FUTURE This is not referenced by any RuleSet?
public class StringConcatenationRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTForStatement node, Object data) {
        Node forLoopStmt = null;
        for (int i = 0; i < 4; i++) {
            forLoopStmt = node.getChild(i);
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
