/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

public class AvoidDeeplyNestedIfStmtsRule extends AbstractJavaRule {

    private int depth;
    private int depthLimit;
    
    private static final PropertyDescriptor PROBLEM_DEPTH_DESCRIPTOR = new IntegerProperty(
    		"problemDepth", 
    		"Maximum allowable statement depth",
    		1, 25,
    		0,
    		1.0f
    		);
    
    private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = asFixedMap(PROBLEM_DEPTH_DESCRIPTOR);
        
    public Object visit(ASTCompilationUnit node, Object data) {
        depth = 0;
        depthLimit = getIntProperty(PROBLEM_DEPTH_DESCRIPTOR);
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

    /**
     * @return Map
     */
    protected Map<String, PropertyDescriptor> propertiesByName() {
    	return PROPERTY_DESCRIPTORS_BY_NAME;
    }
}
