/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.util.OptionalBool;

/**
 *
 */
public class MissingBreakInSwitchRule extends AbstractJavaRulechainRule {

    public MissingBreakInSwitchRule() {
        super(ASTCompilationUnit.class, ASTSwitchStatement.class);
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        DataflowPass.ensureProcessed(node);
        return null;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        for (ASTSwitchBranch branch : node.getBranches()) {
            if (branch instanceof ASTSwitchFallthroughBranch && branch != node.getLastChild()) {
                ASTSwitchFallthroughBranch fallthrough = (ASTSwitchFallthroughBranch) branch;
                OptionalBool bool = DataflowPass.switchBranchFallsThrough(branch);
                if (bool == OptionalBool.YES && fallthrough.getStatements().nonEmpty()) {
                    addViolation(data, branch.getLabel());
                }
            } else {
                return null;
            }
        }
        return null;
    }
}
