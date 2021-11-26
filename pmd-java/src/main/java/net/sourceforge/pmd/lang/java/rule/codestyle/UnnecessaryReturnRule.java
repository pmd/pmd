/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnnecessaryReturnRule extends AbstractJavaRule {

    public UnnecessaryReturnRule() {
        addRuleChainVisit(ASTReturnStatement.class);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        if (node.getNumChildren() == 0 && isDirectMethodStatement(node)) {
            addViolation(data, node);
        }
        return data;
    }

    /**
     * Checks whether the given return statement is nested in some other statement (e.g. if condition)
     * or whether it is a top-level statement in the method, a "direct method statement".
     */
    private boolean isDirectMethodStatement(ASTReturnStatement node) {
        return node.getParent() instanceof ASTStatement
                && node.getNthParent(2) instanceof ASTBlockStatement
                && node.getNthParent(3) instanceof ASTBlock
                && node.getNthParent(4) instanceof ASTMethodDeclaration;
    }
}
