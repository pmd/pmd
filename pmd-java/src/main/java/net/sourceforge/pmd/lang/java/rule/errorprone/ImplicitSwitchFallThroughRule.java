/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.util.OptionalBool;

public class ImplicitSwitchFallThroughRule extends AbstractJavaRulechainRule {

    //todo should consider switch exprs

    private static final Pattern IGNORED_COMMENT = Pattern.compile("/[/*].*\\bfalls?[ -]?thr(ough|u)\\b.*",
                                                                   Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public ImplicitSwitchFallThroughRule() {
        super(ASTSwitchStatement.class);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        DataflowResult dataflow = DataflowPass.getDataflowResult(node.getRoot());

        for (ASTSwitchBranch branch : node.getBranches()) {
            if (branch instanceof ASTSwitchFallthroughBranch && branch != node.getLastChild()) {
                ASTSwitchFallthroughBranch fallthrough = (ASTSwitchFallthroughBranch) branch;
                OptionalBool bool = dataflow.switchBranchFallsThrough(branch);
                if (bool != OptionalBool.NO
                    && fallthrough.getStatements().nonEmpty()
                    && !nextBranchHasComment(branch)) {
                    addViolation(data, branch.getNextBranch().getLabel());
                }
            } else {
                return null;
            }
        }
        return null;
    }

    boolean nextBranchHasComment(ASTSwitchBranch branch) {
        JavaNode nextBranch = branch.getNextBranch();
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

}
