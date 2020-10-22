/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTApexFile;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

import com.nawforce.common.diagnostics.Issue;

public class AvoidUnusedMethodRule extends AbstractApexRule {

    private static final PropertyDescriptor<Integer> PROBLEM_DEPTH_DESCRIPTOR
            = PropertyFactory.intProperty("problemDepth")
            .desc("The if statement depth reporting threshold")
            .require(positive()).defaultValue(3).build();

    public AvoidUnusedMethodRule() {
        definePropertyDescriptor(PROBLEM_DEPTH_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        // Check if any 'Unused' Issues align with this method
        for (Issue issue: getIssues(node)) {
            if ("Unused".equals(issue.diagnostic().category().value())) {
                // Check for basic line alignment for now, Note: ASTMethod end line = block end line
                if (issue.diagnostic().location().startLine() == node.getBeginLine()
                        && issue.diagnostic().location().endLine() <= node.getEndLine()) {
                    addViolation(data, node);
                }
            }
        }
        return data;
    }

    private Issue[] getIssues(ASTMethod node) {
        // Locate multifileAnalysis handler via method root node
        List<RootNode> parents = node.getParentsOfType(RootNode.class);
        if (!parents.isEmpty()) {
            // This first parent is outermost
            ASTApexFile root = (ASTApexFile) parents.get(parents.size() - 1);
            return root.getMultifileAnalysis().getFileIssues(root.getFileName());
        }
        return new Issue[0];
    }
}
