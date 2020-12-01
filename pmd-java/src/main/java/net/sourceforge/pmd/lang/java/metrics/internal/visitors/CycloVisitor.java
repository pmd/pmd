/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal.visitors;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics.CycloOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;


/**
 * Visitor for the Cyclo metric.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public class CycloVisitor extends JavaVisitorBase<MutableInt, Void> {


    protected final boolean considerBooleanPaths;
    protected final boolean considerAssert;
    private final JavaNode topNode;


    public CycloVisitor(MetricOptions options, JavaNode topNode) {
        considerBooleanPaths = !options.getOptions().contains(CycloOption.IGNORE_BOOLEAN_PATHS);
        considerAssert = options.getOptions().contains(CycloOption.CONSIDER_ASSERT);
        this.topNode = topNode;
    }


    @Override
    public final Void visitJavaNode(JavaNode localNode, MutableInt data) {
        return localNode.isFindBoundary() && !localNode.equals(topNode) ? null : super.visitJavaNode(localNode, data);
    }

    @Override
    public Void visit(ASTSwitchExpression node, MutableInt data) {
        return handleSwitch(node, data);
    }

    @Override
    public Void visit(ASTSwitchStatement node, MutableInt data) {
        return handleSwitch(node, data);
    }

    private Void handleSwitch(ASTSwitchLike node, MutableInt data) {
        if (considerBooleanPaths) {
            data.add(JavaMetrics.booleanExpressionComplexity(node.getTestedExpression()));
        }

        for (ASTSwitchBranch branch : node) {
            if (branch.getLabel().isDefault()) {
                // like for "else", default is not a decision point
                continue;
            }

            if (considerBooleanPaths) {
                data.add(branch.getLabel().getExprList().count());
            }
//            else if (branch instanceof ASTSwitchFallthroughBranch) {
//                if (considerBooleanPaths && ((ASTSwitchFallthroughBranch) branch).getStatements().isEmpty())
                // an empty label is only counted if we count boolean paths
//                data.increment();
//            }
        }

        return visitJavaNode(node, data);
    }


    @Override
    public Void visit(ASTConditionalExpression node, MutableInt data) {
        data.increment();
        if (considerBooleanPaths) {
            data.add(JavaMetrics.booleanExpressionComplexity(node.getCondition()));
        }
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTWhileStatement node, MutableInt data) {
        data.increment();
        if (considerBooleanPaths) {
            data.add(JavaMetrics.booleanExpressionComplexity(node.getCondition()));
        }
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTIfStatement node, MutableInt data) {
        data.increment();
        if (considerBooleanPaths) {
            data.add(JavaMetrics.booleanExpressionComplexity(node.getCondition()));
        }

        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTForStatement node, MutableInt data) {
        data.increment();

        if (considerBooleanPaths) {
            data.add(JavaMetrics.booleanExpressionComplexity(node.getCondition()));
        }

        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTForeachStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visitMethodOrCtor(ASTMethodOrConstructorDeclaration node, MutableInt data) {
        data.increment();
        return super.visitMethodOrCtor(node, data);
    }

    @Override
    public Void visit(ASTDoStatement node, MutableInt data) {
        data.increment();
        if (considerBooleanPaths) {
            data.add(JavaMetrics.booleanExpressionComplexity(node.getCondition()));
        }

        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTCatchClause node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTThrowStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTAssertStatement node, MutableInt data) {
        if (considerAssert) {
            data.add(2); // equivalent to if (condition) { throw .. }

            if (considerBooleanPaths) {
                data.add(JavaMetrics.booleanExpressionComplexity(node.getCondition()));
            }
        }

        return super.visit(node, data);
    }

}
