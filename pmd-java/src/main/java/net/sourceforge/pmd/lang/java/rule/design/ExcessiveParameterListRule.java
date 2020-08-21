/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractJavaCounterCheckRule;

/**
 * This rule detects an abnormally long parameter list. Note: This counts Nodes,
 * and not necessarily parameters, so the numbers may not match up. (But
 * topcount and sigma should work.)
 */
public class ExcessiveParameterListRule extends AbstractJavaCounterCheckRule<ASTFormalParameters> {

    public ExcessiveParameterListRule() {
        super(ASTFormalParameters.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 10;
    }

    @Override
    protected boolean isIgnored(ASTFormalParameters node) {
        if (areParametersOfPrivateConstructor(node)) {
            return true;
        }
        return false;
    }

    private boolean areParametersOfPrivateConstructor(ASTFormalParameters params) {
        Node parent = params.getParent();
        if (parent instanceof ASTConstructorDeclaration) {
            ASTConstructorDeclaration constructor = (ASTConstructorDeclaration) parent;
            return constructor.isPrivate();
        }
        return false;
    }

    @Override
    protected boolean isViolation(ASTFormalParameters node, int reportLevel) {
        return node.getParameterCount() > reportLevel;
    }
}
