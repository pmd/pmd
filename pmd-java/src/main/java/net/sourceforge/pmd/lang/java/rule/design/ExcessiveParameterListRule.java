/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
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
        return areParametersOfPrivateConstructor(node);
    }

    private boolean areParametersOfPrivateConstructor(ASTFormalParameters params) {
        Node parent = params.getParent();
        return parent instanceof ASTConstructorDeclaration
                && ((ASTConstructorDeclaration) parent).getVisibility() == Visibility.V_PRIVATE;
    }

    @Override
    protected boolean isViolation(ASTFormalParameters node, int reportLevel) {
        return node.size() > reportLevel;
    }
}
