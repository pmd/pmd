/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import com.nawforce.common.diagnostics.Issue;
import com.nawforce.common.diagnostics.IssueLog;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ApexRootNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

import scala.collection.JavaConverters;

import java.util.List;

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
        List<Issue> issues = getIssues(node);
        if (issues != null) {
            for (Issue issue: issues) {
                if (issue.diagnostic().location().startLine() == node.getBeginLine()
                        && issue.diagnostic().location().endLine() <= node.getEndLine()) {
                    addViolation(data, node);
                }
            }
        }
        return data;
    }

    private List<Issue> getIssues(ASTMethod node) {
        List<RootNode> parents = node.getParentsOfType(RootNode.class);
        if (!parents.isEmpty()) {
            ApexRootNode<?> root = (ApexRootNode<?>) parents.get(parents.size() - 1);
            IssueLog issues = root.getMultifileAnalysis().getIssues();
            if (issues != null) {
                Boolean hasIssues = issues.getIssues().get(root.getFileName()).nonEmpty();
                if (hasIssues) {
                    return JavaConverters.asJava(issues.getIssues().get(root.getFileName()).get());
                }
            }
        }
        return null;
    }
}
