/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;

/**
 * This rule detects an abnormally long parameter list.
 * Note:  This counts Nodes, and not necessarily parameters,
 * so the numbers may not match up.  (But topcount and sigma
 * should work.)
 */
public class LongParameterListRule extends ExcessiveNodeCountRule {
    public LongParameterListRule() {
        super(ASTFormalParameters.class);
    }

    // Count these nodes, but no others.
    public Object visit(ASTFormalParameter node, Object data) {
        return new Integer(1);
    }
}
