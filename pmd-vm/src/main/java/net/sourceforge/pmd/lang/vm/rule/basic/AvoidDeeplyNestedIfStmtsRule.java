/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vm.rule.basic;

import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.vm.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTprocess;
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class AvoidDeeplyNestedIfStmtsRule extends AbstractVmRule {

	private int depth;
    private int depthLimit;
    
    private static final IntegerProperty PROBLEM_DEPTH_DESCRIPTOR = new IntegerProperty(
    		"problemDepth", 
    		"The if statement depth reporting threshold",
    		1, 25,
    		3,
    		1.0f
    		);
    
    public AvoidDeeplyNestedIfStmtsRule() {
    	definePropertyDescriptor(PROBLEM_DEPTH_DESCRIPTOR);
    }
    
    public Object visit(ASTprocess node, Object data) {
        depth = 0;
        depthLimit = getProperty(PROBLEM_DEPTH_DESCRIPTOR);
        return super.visit(node, data);
    }
    
    public Object visit(ASTIfStatement node, Object data) {
        return handleIf(node, data);
    }
    
    public Object visit(ASTElseIfStatement node, Object data) {
        return handleIf(node, data);
    }


	private Object handleIf(AbstractVmNode node, Object data) {
		depth++;
        super.visit(node, data);
        if (depth == depthLimit) {
            addViolation(data, node);
        }
        depth--;
        return data;
	}
	
}
