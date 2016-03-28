/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.rule.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * This rule detects an abnormally long parameter list. Note: This counts Nodes,
 * and not necessarily parameters, so the numbers may not match up. (But
 * topcount and sigma should work.)
 */
public class ExcessiveParameterListRule extends ExcessiveNodeCountRule {
    public ExcessiveParameterListRule() {
        super(ASTParameter.class);
        setProperty(MINIMUM_DESCRIPTOR, 10d);
    }

    public Object visit(ASTParameter node, Object data) {
        return NumericConstants.ONE;
    }
}
