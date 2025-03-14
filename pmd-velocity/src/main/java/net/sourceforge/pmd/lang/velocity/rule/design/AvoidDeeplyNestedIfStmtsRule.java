/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.lang.velocity.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.velocity.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.velocity.ast.ASTTemplate;
import net.sourceforge.pmd.lang.velocity.ast.VtlNode;
import net.sourceforge.pmd.lang.velocity.rule.AbstractVtlRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


public class AvoidDeeplyNestedIfStmtsRule extends AbstractVtlRule {

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

    private Object handleIf(VtlNode node, Object data) {
        depth++;
        super.visitVtlNode(node, data);
        if (depth == depthLimit) {
            asCtx(data).addViolation(node);
        }
        depth--;
        return data;
    }

}
