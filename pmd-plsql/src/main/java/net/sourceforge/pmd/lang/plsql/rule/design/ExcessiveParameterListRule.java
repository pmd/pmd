/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameters;

/**
 * This rule detects an abnormally long parameter list. Note: This counts Nodes,
 * and not necessarily parameters, so the numbers may not match up. (But
 * topcount and sigma should work.)
 */
public class ExcessiveParameterListRule extends AbstractCounterCheckRule<ASTFormalParameters> {
    public ExcessiveParameterListRule() {
        super(ASTFormalParameters.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 10;
    }

    @Override
    protected int getMetric(ASTFormalParameters node) {
        return node.findChildrenOfType(ASTFormalParameter.class).size();
    }

}
