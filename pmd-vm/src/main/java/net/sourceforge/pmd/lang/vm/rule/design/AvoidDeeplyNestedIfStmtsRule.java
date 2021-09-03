/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.vm.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTTemplate;
import net.sourceforge.pmd.lang.vm.ast.VmNode;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


public class AvoidDeeplyNestedIfStmtsRule extends AbstractVmRule {

    private int depth;
    private int depthLimit;

    private static final PropertyDescriptor<Integer> PROBLEM_DEPTH_DESCRIPTOR
            = PropertyFactory.intProperty("problemDepth")
                             .desc("The if statement depth reporting threshold")
                             .require(positive()).defaultValue(3).build();

    public AvoidDeeplyNestedIfStmtsRule() {
        definePropertyDescriptor(PROBLEM_DEPTH_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTTemplate node, Object data) {
        depth = 0;
        depthLimit = getProperty(PROBLEM_DEPTH_DESCRIPTOR);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return handleIf(node, data);
    }

    @Override
    public Object visit(ASTElseIfStatement node, Object data) {
        return handleIf(node, data);
    }

    private Object handleIf(VmNode node, Object data) {
        depth++;
        super.visitVmNode(node, data);
        if (depth == depthLimit) {
            addViolation(data, node);
        }
        depth--;
        return data;
    }

}
