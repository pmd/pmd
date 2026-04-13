/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.reporting.RuleContext;

import com.nawforce.pkgforce.diagnostics.UNUSED_CATEGORY;

public class UnusedMethodRule extends AbstractApexRule {

    @Override
    public RuleContext visit(ASTMethod node, RuleContext data) {
        // Check if any 'Unused' Issues align with this method
        node.getRoot().getGlobalIssues().stream()
            .filter(issue -> issue.rule().name().equals(UNUSED_CATEGORY.name()))
            .filter(issue -> issue.fileLocation().startLineNumber() == node.getBeginLine())
            .filter(issue -> issue.fileLocation().endLineNumber() <= node.getBeginLine())
            .forEach(issue -> data.addViolation(node));
        return data;
    }
}
