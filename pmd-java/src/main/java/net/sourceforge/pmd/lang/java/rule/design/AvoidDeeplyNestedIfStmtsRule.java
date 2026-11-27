/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;


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
        RuleContext ctx = (RuleContext) data;
        depth = 0;
        depthLimit = getProperty(PROBLEM_DEPTH_DESCRIPTOR);
        super.visit(node, ctx);
        return null;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (depth + 1 == depthLimit) {
            ctx.addViolation(node);
            // return early so that if-else chains are only reported once
            return null;
        }
        // deep ifs in if condition are highly unlikely, but possible via lambdas
        node.getCondition().acceptVisitor(this, ctx);
        depth++;
        node.getThenBranch().acceptVisitor(this, ctx);
        depth--;
        ASTStatement elseBranch = node.getElseBranch();
        if (elseBranch != null) {
            int elseIncrement = elseBranch instanceof ASTIfStatement ? 0 : 1;
            depth += elseIncrement;
            elseBranch.acceptVisitor(this, ctx);
            depth -= elseIncrement;
        }
        return null;
    }
}
