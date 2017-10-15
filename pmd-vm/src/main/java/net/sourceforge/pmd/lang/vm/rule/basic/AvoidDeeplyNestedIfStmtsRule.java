/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.basic;

import net.sourceforge.pmd.lang.vm.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTprocess;
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;
import net.sourceforge.pmd.properties.IntegerProperty;

public class AvoidDeeplyNestedIfStmtsRule extends AbstractVmRule {

    private int depth;
    private int depthLimit;

    private static final IntegerProperty PROBLEM_DEPTH_DESCRIPTOR 
            = IntegerProperty.builder("problemDepth")
                             .desc("The if statement depth reporting threshold")
                             .min(1).max(25).defalt(3).uiOrder(1.0f).build();

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
