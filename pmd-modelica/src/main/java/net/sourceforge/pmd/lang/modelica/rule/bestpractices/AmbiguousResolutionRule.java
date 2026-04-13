/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.rule.bestpractices;

import net.sourceforge.pmd.lang.modelica.ast.ASTName;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolvableEntity;
import net.sourceforge.pmd.lang.modelica.rule.AbstractModelicaRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class AmbiguousResolutionRule extends AbstractModelicaRule {
    @Override
    public RuleContext visit(ASTName node, RuleContext data) {
        ResolutionResult<ResolvableEntity> candidates = node.getResolutionCandidates();
        if (candidates.isClashed()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Candidate resolutions: ");
            for (ResolvableEntity candidate: candidates.getBestCandidates()) {
                sb.append(candidate.getDescriptiveName());
                sb.append(", ");
            }
            data.addViolation(node, sb.substring(0, sb.length() - 2));
        }
        return super.visit(node, data);
    }
}
