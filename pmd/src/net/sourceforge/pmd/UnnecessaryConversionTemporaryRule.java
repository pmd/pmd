/*
 * User: tom
 * Date: Jun 20, 2002
 * Time: 1:35:27 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.*;

public class UnnecessaryConversionTemporaryRule extends AbstractRule implements Rule{

    private boolean inPrimaryExpressionContext;
    private boolean usingPrimitiveWrapperAllocation;

    public String getDescription() {
        return "Avoid unnecessay temporaries when converting primitives to Strings";
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
        if (node.jjtGetNumChildren() == 0 || ((SimpleNode)node.jjtGetChild(0)).jjtGetNumChildren() == 0 || !(node.jjtGetChild(0).jjtGetChild(0) instanceof ASTAllocationExpression)) {
            return super.visit(node, data);
        }
        inPrimaryExpressionContext = true;
        Object report = super.visit(node, data);
        inPrimaryExpressionContext = false;
        usingPrimitiveWrapperAllocation = false;
        return report;
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        if (!inPrimaryExpressionContext) {
            return super.visit(node, data);
        }
        if (!(node.jjtGetChild(0) instanceof ASTName)) {
            return super.visit(node, data);
        }
        SimpleNode child = (SimpleNode)node.jjtGetChild(0);
        if (!isPrimitiveWrapperType(child.getImage())) {
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
            ((Report)data).addRuleViolation(new RuleViolation(this, node.getBeginLine()));
        }
        return super.visit(node, data);
    }

    private boolean isPrimitiveWrapperType(String name) {
        return name.equals("Integer") || name.equals("Boolean") || name.equals("Double") || name.equals("Long") || name.equals("Short") || name.equals("Byte") || name.equals("Float");
    }
}
