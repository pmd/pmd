/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unnecessary;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.CollectionUtil;

public class UnnecessaryConversionTemporaryRule extends AbstractJavaRule {

    private boolean inPrimaryExpressionContext;
    private ASTPrimaryExpression primary;
    private boolean usingPrimitiveWrapperAllocation;
    
    private static final Set<String> PRIMITIVE_WRAPPERS = CollectionUtil.asSet(
    	new String[] {"Integer", "Boolean", "Double", "Long", "Short", "Byte", "Float"}
    	);

    public Object visit(ASTPrimaryExpression node, Object data) {
        if (node.jjtGetNumChildren() == 0 || (node.jjtGetChild(0)).jjtGetNumChildren() == 0 || !(node.jjtGetChild(0).jjtGetChild(0) instanceof ASTAllocationExpression)) {
            return super.visit(node, data);
        }
        // TODO... hmmm... is this inPrimaryExpressionContext gibberish necessary?
        inPrimaryExpressionContext = true;
        primary = node;
        super.visit(node, data);
        inPrimaryExpressionContext = false;
        usingPrimitiveWrapperAllocation = false;
        return data;
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        if (!inPrimaryExpressionContext || !(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
            return super.visit(node, data);
        }
        if (!PRIMITIVE_WRAPPERS.contains(node.jjtGetChild(0).getImage())) {
            return super.visit(node, data);
        }
        usingPrimitiveWrapperAllocation = true;
        return super.visit(node, data);
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if (inPrimaryExpressionContext && usingPrimitiveWrapperAllocation) {
            if (node.hasImageEqualTo("toString")) {
                if (node.jjtGetParent() == primary) {
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }

}
