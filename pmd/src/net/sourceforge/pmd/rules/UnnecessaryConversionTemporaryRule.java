/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashSet;
import java.util.Set;

public class UnnecessaryConversionTemporaryRule extends AbstractRule implements Rule {

    private boolean inPrimaryExpressionContext;
    private boolean usingPrimitiveWrapperAllocation;
    private Set primitiveWrappers = new HashSet();

    public UnnecessaryConversionTemporaryRule() {
        primitiveWrappers.add("Integer");
        primitiveWrappers.add("Boolean");
        primitiveWrappers.add("Double");
        primitiveWrappers.add("Long");
        primitiveWrappers.add("Short");
        primitiveWrappers.add("Byte");
        primitiveWrappers.add("Float");
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
        if (node.jjtGetNumChildren() == 0 || (node.jjtGetChild(0)).jjtGetNumChildren() == 0 || !(node.jjtGetChild(0).jjtGetChild(0) instanceof ASTAllocationExpression)) {
            return super.visit(node, data);
        }
        // TODO... hmmm... is this inPrimaryExpressionContext gibberish necessary?
        inPrimaryExpressionContext = true;
        Object report = super.visit(node, data);
        inPrimaryExpressionContext = false;
        usingPrimitiveWrapperAllocation = false;
        return report;
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        if (!inPrimaryExpressionContext || !(node.jjtGetChild(0) instanceof ASTName)) {
            return super.visit(node, data);
        }
        if (!primitiveWrappers.contains(((SimpleNode) node.jjtGetChild(0)).getImage())) {
            return super.visit(node, data);
        }
        usingPrimitiveWrapperAllocation = true;
        return super.visit(node, data);
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if (!inPrimaryExpressionContext || !usingPrimitiveWrapperAllocation) {
            return super.visit(node, data);
        }
        if (node.getImage() != null && node.getImage().equals("toString")) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node, data);
    }

}
