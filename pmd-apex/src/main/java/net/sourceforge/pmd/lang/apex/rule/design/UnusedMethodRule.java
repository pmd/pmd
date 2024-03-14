/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class UnusedMethodRule extends AbstractApexRule {

    @Override
    public Object visit(ASTMethod node, Object data) {
        // Check if any 'Unused' Issues align with this method
        node.getRoot().getGlobalIssues().stream()
            .filter(issue -> "Unused".equals(issue.category()))
            .filter(issue -> issue.fileLocation().startLineNumber() == node.getBeginLine())
            .filter(issue -> issue.fileLocation().endLineNumber() <= node.getBeginLine())
            .forEach(issue -> asCtx(data).addViolation(node));
        return data;
    }
}
