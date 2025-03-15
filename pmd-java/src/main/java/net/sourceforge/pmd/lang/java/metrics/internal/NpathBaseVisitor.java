/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import java.math.BigInteger;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;


/**
 * Visitor for the default n-path complexity version.
 *
 * @author Clément Fournier
 * @author Jason Bennett
 */
public class NpathBaseVisitor extends JavaVisitorBase<Void, BigInteger> {

    /** Instance. */
    public static final NpathBaseVisitor INSTANCE = new NpathBaseVisitor();


    /* Multiplies the complexity of the children of this node. */
    private BigInteger multiplyChildrenComplexities(JavaNode node) {
        return multiplyComplexities(node.children());
    }

    private BigInteger multiplyComplexities(NodeStream<? extends JavaNode> nodes) {
        return nodes.reduce(BigInteger.ONE, (acc, n) -> acc.multiply(n.acceptVisitor(this, null)));
    }


    /* Sums the complexity of the children of the node. */
    private BigInteger sumChildrenComplexities(JavaNode node, Void data) {
        BigInteger sum = BigInteger.ZERO;

        for (JavaNode child : node.children()) {
            BigInteger childComplexity = child.acceptVisitor(this, data);
            sum = sum.add(childComplexity);
        }

        return sum;
    }


    @Override
    public BigInteger visitMethodOrCtor(ASTExecutableDeclaration node, Void data) {
        return node.getBody() == null ? BigInteger.ONE
                                      : node.getBody().acceptVisitor(this, data);
    }


    @Override
    public BigInteger visitJavaNode(JavaNode node, Void data) {
        return multiplyChildrenComplexities(node);
    }


    @Override
    public BigInteger visit(ASTIfStatement node, Void data) {
        // (npath of if + npath of else (or 1) ) * bool_comp of if * npath of next

        BooleanPaths cond = pathsInCondition(node.getCondition());

        BigInteger thenResult = node.getThenBranch().acceptVisitor(this, data);
        ASTStatement elseBranch = node.getElseBranch();
        BigInteger elseResult = elseBranch != null ? elseBranch.acceptVisitor(this, data) : BigInteger.ONE;

        return thenResult.multiply(cond.truePaths())
                         .add(elseResult.multiply(cond.falsePaths()));
    }


    @Override
    public BigInteger visitLoop(ASTLoopStatement node, Void data) {
        // (npath of while + bool_comp of while + 1) * npath of next

        BooleanPaths cond = node.getCondition() == null
                            ? BooleanPaths.UNIT
                            : pathsInCondition(node.getCondition());

        BigInteger nPathBody = node.getBody().acceptVisitor(this, data);

        return nPathBody.multiply(cond.truePaths()).add(cond.falsePaths());
    }

    @Override
    public BigInteger visit(ASTSwitchExpression node, Void data) {
        return handleSwitch(node, data);
    }

    @Override
    public BigInteger visit(ASTSwitchStatement node, Void data) {
        return handleSwitch(node, data);
    }

    public BigInteger handleSwitch(ASTSwitchLike node, Void data) {
        // bool_comp of switch + sum(npath(case_range))

        BigInteger boolCompSwitch = node.getTestedExpression().acceptVisitor(this, data);

        BigInteger npath = BigInteger.ZERO;
        int caseRange = 0;

        for (ASTSwitchBranch n : node) {

            // Fall-through labels count as 1 for complexity
            if (n instanceof ASTSwitchFallthroughBranch) {
                caseRange += JavaAstUtils.numAlternatives(n);
                NodeStream<ASTStatement> statements = ((ASTSwitchFallthroughBranch) n).getStatements();
                if (statements.nonEmpty()) {
                    BigInteger branchNpath = multiplyComplexities(statements);
                    npath = npath.add(branchNpath.multiply(BigInteger.valueOf(caseRange)));
                    caseRange = 0;
                }
            } else if (n instanceof ASTSwitchArrowBranch) {
                int numAlts = JavaAstUtils.numAlternatives(n);
                BigInteger branchNpath = ((ASTSwitchArrowBranch) n).getRightHandSide().acceptVisitor(this, data);
                npath = npath.add(branchNpath.multiply(BigInteger.valueOf(numAlts)));
            }
        }
//        if (!node.isExhaustive()) {
//            npath = npath.add(BigInteger.ONE);
//        }
        return npath.multiply(boolCompSwitch);
    }

    @Override
    public BigInteger visit(ASTSwitchLabel node, Void data) {
        if (node.isDefault()) {
            return BigInteger.ONE;
        }
        return BigInteger.valueOf(node.children(ASTExpression.class).count());
    }

    @Override
    public BigInteger visit(ASTConditionalExpression node, Void data) {
        // bool comp of guard clause * sum of complexity of branches

        BigInteger cond = node.getCondition().acceptVisitor(this, data);
        BigInteger thenBranch = node.getThenBranch().acceptVisitor(this, data);
        BigInteger elseBranch = node.getElseBranch().acceptVisitor(this, data);

        return thenBranch.add(elseBranch).multiply(cond);
    }

    @Override
    public BigInteger visit(ASTInfixExpression node, Void data) {
        if (BinaryOp.CONDITIONAL_OPS.contains(node.getOperator())) {
            BigInteger leftOp = node.getLeftOperand().acceptVisitor(this, data);
            BigInteger rightOp = node.getRightOperand().acceptVisitor(this, data);
            return leftOp.add(BigInteger.ONE).multiply(rightOp);
        }
        return super.visit(node, data);
    }

    @Override
    public BigInteger visit(ASTTryStatement node, Void data) {
        /*
         * This scenario was not addressed by the original paper. Based on the
         * principles outlined in the paper, as well as the Checkstyle NPath
         * implementation, this code will add the complexity of the try to the
         * complexities of the catch and finally blocks.
         */
        return sumChildrenComplexities(node, data);
    }

    private static class BooleanPaths {
        public static final BooleanPaths UNIT = new BooleanPaths(1, 1);
        private final int truePaths;
        private final int falsePaths;

        private BooleanPaths(int truePaths, int falsePaths) {
            this.truePaths = truePaths;
            this.falsePaths = falsePaths;
        }

        BigInteger truePaths() {
            return BigInteger.valueOf(truePaths);
        }

        BigInteger falsePaths() {
            return BigInteger.valueOf(falsePaths);
        }
    }

    private static BooleanPaths pathsInCondition(ASTExpression e) {

        if (e instanceof ASTInfixExpression) {
            BooleanPaths left = pathsInCondition(((ASTInfixExpression) e).getLeftOperand());
            BooleanPaths right = pathsInCondition(((ASTInfixExpression) e).getRightOperand());

            if (JavaAstUtils.isInfixExprWithOperator(e, BinaryOp.CONDITIONAL_OR)) {
                return new BooleanPaths(left.truePaths * right.truePaths + 1,
                                        left.falsePaths * right.falsePaths);
            } else if (JavaAstUtils.isInfixExprWithOperator(e, BinaryOp.CONDITIONAL_AND)) {
                return new BooleanPaths(left.truePaths * right.truePaths,
                                        left.falsePaths * right.falsePaths + 1);
            }
        }

        return BooleanPaths.UNIT;
    }
}
