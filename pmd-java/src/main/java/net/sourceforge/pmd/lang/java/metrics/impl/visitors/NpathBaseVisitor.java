/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;


/**
 * Visitor for the default n-path complexity version.
 *
 * @author Clément Fournier
 * @author Jason Bennett
 * @deprecated Is internal API, will be moved in 7.0.0
 */
@Deprecated
@InternalApi
public class NpathBaseVisitor extends JavaParserVisitorReducedAdapter {

    /** Instance. */
    public static final NpathBaseVisitor INSTANCE = new NpathBaseVisitor();


    /* Multiplies the complexity of the children of this node. */
    private int multiplyChildrenComplexities(JavaNode node, Object data) {
        int product = 1;

        for (int i = 0; i < node.getNumChildren(); i++) {
            JavaNode n = (JavaNode) node.getChild(i);
            int childComplexity = (int) n.jjtAccept(this, data);

            int newProduct = product * childComplexity;
            if (newProduct >= product) {
                product = newProduct;
            } else {
                // Overflow happened
                product = Integer.MAX_VALUE;
                break;
            }
        }

        return product;
    }


    /* Sums the complexity of the children of the node. */
    private int sumChildrenComplexities(JavaNode node, Object data) {
        int sum = 0;

        for (int i = 0; i < node.getNumChildren(); i++) {
            JavaNode n = (JavaNode) node.getChild(i);
            int childComplexity = (int) n.jjtAccept(this, data);

            int newSum = sum + childComplexity;
            if (newSum >= sum) {
                sum = newSum;
            } else {
                // Overflow happened
                sum = Integer.MAX_VALUE;
                break;
            }
        }

        return sum;
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        return multiplyChildrenComplexities(node, data);
    }


    @Override
    public Object visit(JavaNode node, Object data) {
        return multiplyChildrenComplexities(node, data);
    }


    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of next

        List<ASTStatement> statementChildren = node.findChildrenOfType(ASTStatement.class);

        // add path for not taking if
        int complexity = node.hasElse() ? 0 : 1;

        for (ASTStatement element : statementChildren) {
            complexity += (int) element.jjtAccept(this, data);
        }

        int boolCompIf = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        return boolCompIf + complexity;
    }


    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        // (npath of while + bool_comp of while + 1) * npath of next

        int boolCompWhile = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int nPathWhile = (int) node.getFirstChildOfType(ASTStatement.class).jjtAccept(this, data);

        return boolCompWhile + nPathWhile + 1;
    }


    @Override
    public Object visit(ASTDoStatement node, Object data) {
        // (npath of do + bool_comp of do + 1) * npath of next

        int boolCompDo = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int nPathDo = (int) node.getFirstChildOfType(ASTStatement.class).jjtAccept(this, data);

        return boolCompDo + nPathDo + 1;
    }


    @Override
    public Object visit(ASTForStatement node, Object data) {
        // (npath of for + bool_comp of for + 1) * npath of next

        int boolCompFor = CycloMetric.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));

        int nPathFor = (int) node.getFirstChildOfType(ASTStatement.class).jjtAccept(this, data);

        return boolCompFor + nPathFor + 1;
    }


    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        // return statements are valued at 1, or the value of the boolean expression

        ASTExpression expr = node.getFirstChildOfType(ASTExpression.class);

        if (expr == null) {
            return 1;
        }

        int boolCompReturn = CycloMetric.booleanExpressionComplexity(expr);
        int conditionalExpressionComplexity = multiplyChildrenComplexities(expr, data);

        if (conditionalExpressionComplexity > 1) {
            boolCompReturn += conditionalExpressionComplexity;
        }

        return boolCompReturn > 0 ? boolCompReturn : 1;
    }


    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        // bool_comp of switch + sum(npath(case_range))

        int boolCompSwitch = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int npath = 0;
        int caseRange = 0;
        for (int i = 0; i < node.getNumChildren(); i++) {
            JavaNode n = (JavaNode) node.getChild(i);

            // Fall-through labels count as 1 for complexity
            if (n instanceof ASTSwitchLabel) {
                npath += caseRange;
                caseRange = 1;
            } else {
                int complexity = (int) n.jjtAccept(this, data);
                caseRange *= complexity;
            }
        }
        // add in npath of last label
        npath += caseRange;
        return boolCompSwitch + npath;
    }


    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        // bool comp of guard clause + complexity of last two children (= total - 1)

        ASTExpression wrapper = new ASTExpression(Integer.MAX_VALUE);
        wrapper.jjtAddChild(node.getChild(0), 0);
        int boolCompTernary = CycloMetric.booleanExpressionComplexity(wrapper);

        return boolCompTernary + sumChildrenComplexities(node, data) - 1;
    }


    @Override
    public Object visit(ASTTryStatement node, Object data) {
        /*
         * This scenario was not addressed by the original paper. Based on the
         * principles outlined in the paper, as well as the Checkstyle NPath
         * implementation, this code will add the complexity of the try to the
         * complexities of the catch and finally blocks.
         */
        return sumChildrenComplexities(node, data);
    }
}
