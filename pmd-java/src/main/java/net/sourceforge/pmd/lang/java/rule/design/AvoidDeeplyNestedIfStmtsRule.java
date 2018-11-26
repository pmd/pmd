/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


public class AvoidDeeplyNestedIfStmtsRule extends AbstractJavaRule {

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
    public Object visit(ASTCompilationUnit node, Object data) {
        depth = 0;
        depthLimit = getProperty(PROBLEM_DEPTH_DESCRIPTOR);
        return super.visit(node, data);
    }

    @Override
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
