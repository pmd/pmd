/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import com.nawforce.common.api.UNUSED_CATEGORY$;

public class UnusedMethodRule extends AbstractApexRule {

    @Override
    public Object visit(ASTMethod node, Object data) {

        // Check if any 'Unused' Issues align with this method
        node.getRoot().getGlobalIssues().stream()
            .filter(issue -> UNUSED_CATEGORY$.MODULE$ == issue.diagnostic().category())
            .filter(issue -> issue.diagnostic().location().startLine() == node.getBeginLine())
            .filter(issue -> issue.diagnostic().location().endLine() <= node.getBeginLine())
            .forEach(issue -> addViolation(data, node));
        return data;
    }
}
