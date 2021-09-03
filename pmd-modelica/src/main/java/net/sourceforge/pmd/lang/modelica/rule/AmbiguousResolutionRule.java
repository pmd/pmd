/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.rule;

import net.sourceforge.pmd.lang.modelica.ast.ASTName;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolvableEntity;

public class AmbiguousResolutionRule extends AbstractModelicaRule {
    @Override
    public Object visit(ASTName node, Object data) {
        ResolutionResult<ResolvableEntity> candidates = node.getResolutionCandidates();
        if (candidates.isClashed()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Candidate resolutions: ");
            for (ResolvableEntity candidate: candidates.getBestCandidates()) {
                sb.append(candidate.getDescriptiveName());
                sb.append(", ");
            }
            addViolation(data, node, sb.substring(0, sb.length() - 2));
        }
        return super.visit(node, data);
    }
}
