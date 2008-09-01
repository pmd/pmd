/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

public class AvoidDeeplyNestedIfStmtsRule extends AbstractJavaRule {

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
  
    public Object visit(ASTCompilationUnit node, Object data) {
        depth = 0;
        depthLimit = getProperty(PROBLEM_DEPTH_DESCRIPTOR);
        return super.visit(node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
        if (!node.hasElse()) {
            depth++;
        }
        super.visit(node, data);
        if (depth == depthLimit) {
            addViolation(data, node);
        }
        depth--;
        return data;
    }
}
