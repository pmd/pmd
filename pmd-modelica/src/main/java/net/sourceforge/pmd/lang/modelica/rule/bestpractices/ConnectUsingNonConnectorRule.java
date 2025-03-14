/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.rule.bestpractices;

import net.sourceforge.pmd.lang.modelica.ast.ASTComponentReference;
import net.sourceforge.pmd.lang.modelica.ast.ASTConnectClause;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassSpecialization;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassType;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaComponentDeclaration;
import net.sourceforge.pmd.lang.modelica.resolver.ResolutionResult;
import net.sourceforge.pmd.lang.modelica.resolver.ResolvableEntity;
import net.sourceforge.pmd.lang.modelica.rule.AbstractModelicaRule;

public class ConnectUsingNonConnectorRule extends AbstractModelicaRule {
    private void reportIfViolated(ASTComponentReference ref, Object data) {
        ResolutionResult<ResolvableEntity> resolution = ref.getResolutionCandidates();
        if (!resolution.isUnresolved()) { // no false positive if not resolved at all
            ResolvableEntity firstDecl = resolution.getBestCandidates().get(0);
            if (firstDecl instanceof ModelicaComponentDeclaration) {
                ModelicaComponentDeclaration componentDecl = (ModelicaComponentDeclaration) firstDecl;
                ResolutionResult componentTypes = componentDecl.getTypeCandidates();
                if (!componentTypes.isUnresolved()) {
                    if (componentTypes.getBestCandidates().get(0) instanceof ModelicaClassType) {
                        ModelicaClassType classDecl = (ModelicaClassType) componentTypes.getBestCandidates().get(0);
                        ModelicaClassSpecialization restriction = classDecl.getSpecialization();
                        if (!classDecl.isConnectorLike()) {
                            asCtx(data).addViolation(ref, restriction.toString());
                        }
                    } else {
                        asCtx(data).addViolation(ref, firstDecl.getDescriptiveName());
                    }
                }
            } else {
                asCtx(data).addViolation(ref, firstDecl.getDescriptiveName());
            }
        }
    }

    @Override
    public Object visit(ASTConnectClause node, Object data) {
        ASTComponentReference lhs = (ASTComponentReference) node.getChild(0);
        ASTComponentReference rhs = (ASTComponentReference) node.getChild(1);

        reportIfViolated(lhs, data);
        reportIfViolated(rhs, data);

        return super.visit(node, data);
    }
}
