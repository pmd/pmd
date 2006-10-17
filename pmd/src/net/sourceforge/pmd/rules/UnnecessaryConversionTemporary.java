/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.Set;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.CollectionUtil;

public class UnnecessaryConversionTemporary extends AbstractRule implements Rule {

    private boolean inPrimaryExpressionContext;
    private ASTPrimaryExpression primary;
    private boolean usingPrimitiveWrapperAllocation;
    
    private static final Set primitiveWrappers = CollectionUtil.asSet(
    	new String[] {"Integer", "Boolean", "Double", "Long", "Short", "Byte", "Float"}
    	);
 
    public UnnecessaryConversionTemporary() {
    }

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
        if (!primitiveWrappers.contains(((SimpleNode) node.jjtGetChild(0)).getImage())) {
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
