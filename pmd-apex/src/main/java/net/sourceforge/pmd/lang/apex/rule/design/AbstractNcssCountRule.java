/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractStatisticalApexRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Abstract superclass for NCSS counting methods. Counts tokens according to
 * <a href="http://www.kclee.de/clemens/java/javancss/">JavaNCSS rules</a>.
 *
 * @author ported from Java original of Jason Bennett
 */
public abstract class AbstractNcssCountRule extends AbstractStatisticalApexRule {

    private Class<?> nodeClass;

    /**
     * Count the nodes of the given type using NCSS rules.
     *
     * @param nodeClass
     *            class of node to count
     */
    protected AbstractNcssCountRule(Class<?> nodeClass) {
        this.nodeClass = nodeClass;

        setProperty(MINIMUM_DESCRIPTOR, 1000d);
        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ApexNode<?> node, Object data) {
        int numNodes = 0;

        for (ApexNode<?> child : node.children()) {
            numNodes += (Integer) child.jjtAccept(this, data);
        }

        if (this.nodeClass.isInstance(node)) {
            // Add 1 to account for base node
            numNodes++;
            DataPoint point = new DataPoint();
            point.setNode(node);
            point.setScore(1.0 * numNodes);
            point.setMessage(getMessage());
            addDataPoint(point);
        }

        return Integer.valueOf(numNodes);
    }

    /**
     * Count the number of children of the given node. Adds one to count the
     * node itself.
     *
     * @param node
     *            node having children counted
     * @param data
     *            node data
     * @return count of the number of children of the node, plus one
     */
    protected Integer countNodeChildren(ApexNode<?> node, Object data) {
        Integer nodeCount;
        int lineCount = 0;
        for (ApexNode<?> child : node.children()) {
            nodeCount = (Integer) child.jjtAccept(this, data);
            lineCount += nodeCount;
        }
        return ++lineCount;
    }

    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTIfElseBlockStatement node, Object data) {

        Integer lineCount = countNodeChildren(node, data);
        lineCount++;

        return lineCount;
    }

    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        return NumericConstants.ONE;
    }
}
