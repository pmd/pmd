/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.bestpractices;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.vm.ast.ASTDirective;
import net.sourceforge.pmd.lang.vm.ast.ASTReference;
import net.sourceforge.pmd.lang.vm.ast.ASTSetDirective;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class AvoidReassigningParametersRule extends AbstractVmRule {

    @Override
    public Object visit(final ASTDirective node, final Object data) {
        if ("macro".equals(node.getDirectiveName())) {
            final Set<String> paramNames = new HashSet<>();
            final List<ASTReference> params = node.findChildrenOfType(ASTReference.class);
            for (final ASTReference param : params) {
                paramNames.add(param.getFirstToken().toString());
            }
            final List<ASTSetDirective> assignments = node.findDescendantsOfType(ASTSetDirective.class);
            for (final ASTSetDirective assignment : assignments) {
                final ASTReference ref = assignment.getFirstChildOfType(ASTReference.class);
                if (ref != null && paramNames.contains(ref.getFirstToken().toString())) {
                    addViolation(data, node, ref.getFirstToken().toString());
                }
            }
        }
        return super.visit(node, data);
    }
}
