/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.util.OptionalBool;

public class MissingBreakInSwitchRule extends AbstractJavaRulechainRule {

    //todo should consider switch exprs
    // todo rename to ImplicitSwitchFallThrough

    private static final Pattern IGNORED_COMMENT = Pattern.compile("/[/*].*\\bfalls?[ -]?thr(ough|u)\\b.*",
                                                                   Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

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
                if (bool != OptionalBool.NO
                    && fallthrough.getStatements().nonEmpty()
                    && !nextBranchHasComment(branch)) {
                    addViolation(data, nextBranch(branch).getLabel());
                }
            } else {
                return null;
            }
        }
        return null;
    }

    boolean nextBranchHasComment(ASTSwitchBranch branch) {
        JavaNode nextBranch = nextBranch(branch);
        if (nextBranch == null) {
            return false;
        }
        for (JavaccToken special : GenericToken.previousSpecials(nextBranch.getFirstToken())) {
            if ((JavaTokenKinds.SINGLE_LINE_COMMENT == special.kind
                || JavaTokenKinds.MULTI_LINE_COMMENT == special.kind)
                && IGNORED_COMMENT.matcher(special.getImage()).find()) {
                return true;
            }
        }
        return false;
    }

    private ASTSwitchBranch nextBranch(ASTSwitchBranch branch) {
        return (ASTSwitchBranch) branch.asStream().followingSiblings().first();
    }
}
