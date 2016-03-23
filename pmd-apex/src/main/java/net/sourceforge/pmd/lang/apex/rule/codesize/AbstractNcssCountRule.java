/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import net.sourceforge.pmd.lang.apex.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractStatisticalApexRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Abstract superclass for NCSS counting methods. Counts tokens according to
 * <a href="http://www.kclee.de/clemens/java/javancss/">JavaNCSS rules</a>.
 * 
 * @author ported from original of Jason Bennett
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
    }

    @Override
    public Object visit(ApexNode node, Object data) {
        int numNodes = 0;

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            ApexNode n = (ApexNode) node.jjtGetChild(i);
            Integer treeSize = (Integer) n.jjtAccept(this, data);
            numNodes += treeSize.intValue();
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
     * Count the number of children of the given Java node. Adds one to count
     * the node itself.
     * 
     * @param node
     *            java node having children counted
     * @param data
     *            node data
     * @return count of the number of children of the node, plus one
     */
    protected Integer countNodeChildren(Node node, Object data) {
        Integer nodeCount = null;
        int lineCount = 0;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            nodeCount = (Integer) ((ApexNode) node.jjtGetChild(i)).jjtAccept(this, data);
            lineCount += nodeCount.intValue();
        }
        return ++lineCount;
    }

    public Object visit(ASTForEachStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    public Object visit(ASTForLoopStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    public Object visit(ASTIfBlockStatement node, Object data) {

        Integer lineCount = countNodeChildren(node, data);

        return lineCount;
    }

    public Object visit(ASTIfElseBlockStatement node, Object data) {

        Integer lineCount = countNodeChildren(node, data);
        lineCount++;
        return lineCount;
    }

    public Object visit(ASTWhileLoopStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    public Object visit(ASTBreakStatement node, Object data) {
        return NumericConstants.ONE;
    }

    public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    public Object visit(ASTThrowStatement node, Object data) {
        return NumericConstants.ONE;
    }

}
