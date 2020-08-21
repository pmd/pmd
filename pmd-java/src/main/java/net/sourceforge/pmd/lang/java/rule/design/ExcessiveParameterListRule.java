/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;

/**
 * This rule detects an abnormally long parameter list. Note: This counts Nodes,
 * and not necessarily parameters, so the numbers may not match up. (But
 * topcount and sigma should work.)
 */
public class ExcessiveParameterListRule extends ExcessiveNodeCountRule {

    private static final Integer COUNT = 1;
    private static final Integer SKIP = 0;

    public ExcessiveParameterListRule() {
        super(ASTFormalParameters.class);
        setProperty(MINIMUM_DESCRIPTOR, 10d);
    }

    @Override
    public Object visit(ASTFormalParameters params, Object data) {
        if (areParametersOfPrivateConstructor(params)) {
            return SKIP;
        }
        return super.visit(params, data);
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
    public Object visit(ASTFormalParameter param, Object data) {
        return COUNT;
    }
}
