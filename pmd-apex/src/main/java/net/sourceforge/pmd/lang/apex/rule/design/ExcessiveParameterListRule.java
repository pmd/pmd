/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * This rule detects an abnormally long parameter list. Note: This counts Nodes,
 * and not necessarily parameters, so the numbers may not match up. (But
 * topcount and sigma should work.)
 */
public class ExcessiveParameterListRule extends ExcessiveNodeCountRule {
    public ExcessiveParameterListRule() {
        super(ASTMethod.class);
        setProperty(MINIMUM_DESCRIPTOR, 4d);
        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 50);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTParameter node, Object data) {
        return NumericConstants.ONE;
    }
}
