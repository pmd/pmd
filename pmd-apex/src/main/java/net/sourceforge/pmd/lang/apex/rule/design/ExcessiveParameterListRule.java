/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.internal.AbstractCounterCheckRule;

/**
 * This rule detects an abnormally long parameter list. Note: This counts Nodes,
 * and not necessarily parameters, so the numbers may not match up. (But
 * topcount and sigma should work.)
 */
public class ExcessiveParameterListRule extends AbstractCounterCheckRule<ASTMethod> {


    public ExcessiveParameterListRule() {
        super(ASTMethod.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 4;
    }

    @Override
    protected int getMetric(ASTMethod node) {
        return node.getArity();
    }
}
