/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractStatisticalApexRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * NPath complexity is a measurement of the acyclic execution paths through a
 * function. See Nejmeh, Communications of the ACM Feb 1988 pp 188-200.
 * 
 * @author ported from Java version of Jason Bennett
 */
public class NPathComplexityRule extends AbstractStatisticalApexRule {

    public NPathComplexityRule() {
        super();
        setProperty(MINIMUM_DESCRIPTOR, 200d);
    }

    private int complexityMultipleOf(ApexNode node, int npathStart, Object data) {

        int npath = npathStart;
        ApexNode n;

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            n = (ApexNode) node.jjtGetChild(i);
            npath *= (Integer) n.jjtAccept(this, data);
        }

        return npath;
    }

    private int complexitySumOf(ApexNode node, int npathStart, Object data) {

        int npath = npathStart;
        ApexNode n;

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            n = (ApexNode) node.jjtGetChild(i);
            npath += (Integer) n.jjtAccept(this, data);
        }

        return npath;
    }

    public Object visit(ASTMethod node, Object data) {
        int npath = complexityMultipleOf(node, 1, data);

        DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(1.0 * npath);
        point.setMessage(getMessage());
        addDataPoint(point);

        return Integer.valueOf(npath);
    }

    public Object visit(ApexNode node, Object data) {
        int npath = complexityMultipleOf(node, 1, data);
        return Integer.valueOf(npath);
    }

    public Object visit(ASTIfBlockStatement node, Object data) {
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of
        // next

        List<ApexNode> statementChildren = new ArrayList<>();
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i).getClass() == ASTStatement.class) {
                statementChildren.add((ApexNode) node.jjtGetChild(i));
            }
        }

        if (statementChildren.isEmpty() || statementChildren.size() == 1 && node.hasElse()
                || statementChildren.size() != 1 && !node.hasElse()) {
            throw new IllegalStateException("If node has wrong number of children");
        }

        // add path for not taking if
        int complexity = 0;
        if (!node.hasElse()) {
            complexity++;
        }

        for (ApexNode element : statementChildren) {
            complexity += (Integer) element.jjtAccept(this, data);
        }

        int boolCompIf = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        return Integer.valueOf(boolCompIf + complexity);
    }

    public Object visit(ASTWhileLoopStatement node, Object data) {
        // (npath of while + bool_comp of while + 1) * npath of next

        int boolCompWhile = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        Integer nPathWhile = (Integer) ((ApexNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        return Integer.valueOf(boolCompWhile + nPathWhile + 1);
    }

    public Object visit(ASTForLoopStatement node, Object data) {
        // (npath of for + bool_comp of for + 1) * npath of next

        int boolCompFor = sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));

        Integer nPathFor = (Integer) ((ApexNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        return Integer.valueOf(boolCompFor + nPathFor + 1);
    }

    public Object visit(ASTForEachStatement node, Object data) {
        // (npath of for + bool_comp of for + 1) * npath of next

        int boolCompFor = sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));

        Integer nPathFor = (Integer) ((ApexNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        return Integer.valueOf(boolCompFor + nPathFor + 1);
    }

    public Object visit(ASTReturnStatement node, Object data) {
        // return statements are valued at 1, or the value of the boolean
        // expression

        ASTExpression expr = node.getFirstChildOfType(ASTExpression.class);

        if (expr == null) {
            return NumericConstants.ONE;
        }

        int boolCompReturn = sumExpressionComplexity(expr);
        int conditionalExpressionComplexity = complexityMultipleOf(expr, 1, data);

        if (conditionalExpressionComplexity > 1) {
            boolCompReturn += conditionalExpressionComplexity;
        }

        if (boolCompReturn > 0) {
            return Integer.valueOf(boolCompReturn);
        }
        return NumericConstants.ONE;
    }

    public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
        /*
         * This scenario was not addressed by the original paper. Based on the
         * principles outlined in the paper, as well as the Checkstyle NPath
         * implementation, this code will add the complexity of the try to the
         * complexities of the catch and finally blocks.
         */
        int npath = complexitySumOf(node, 0, data);

        return Integer.valueOf(npath);

    }

    /**
     * Calculate the boolean complexity of the given expression. NPath boolean
     * complexity is the sum of && and || tokens. This is calculated by summing
     * the number of children of the &&'s (minus one) and the children of the
     * ||'s (minus one).
     * <p>
     * Note that this calculation applies to Cyclomatic Complexity as well.
     * 
     * @param expr
     *            control structure expression
     * @return complexity of the boolean expression
     */
    public static int sumExpressionComplexity(ASTExpression expr) {
        if (expr == null) {
            return 0;
        }

        List<ASTConditionalAndExpression> andNodes = expr.findDescendantsOfType(ASTConditionalAndExpression.class);
        List<ASTConditionalOrExpression> orNodes = expr.findDescendantsOfType(ASTConditionalOrExpression.class);

        int children = 0;

        for (ASTConditionalOrExpression element : orNodes) {
            children += element.jjtGetNumChildren();
            children--;
        }

        for (ASTConditionalAndExpression element : andNodes) {
            children += element.jjtGetNumChildren();
            children--;
        }

        return children;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new String[] { ((ASTMethod) point.getNode()).getMethodName(), String.valueOf((int) point.getScore()) };
    }
}
