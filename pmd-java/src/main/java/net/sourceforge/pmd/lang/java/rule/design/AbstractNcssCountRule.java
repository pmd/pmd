/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpressionList;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Abstract superclass for NCSS counting methods. Counts tokens according to
 * <a href="http://www.kclee.de/clemens/java/javancss/">JavaNCSS rules</a>.
 *
 * @author Jason Bennett
 */
public abstract class AbstractNcssCountRule extends AbstractStatisticalJavaRule {

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
    public Object visit(JavaNode node, Object data) {
        int numNodes = 0;

        for (int i = 0; i < node.getNumChildren(); i++) {
            JavaNode n = (JavaNode) node.getChild(i);
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
        for (int i = 0; i < node.getNumChildren(); i++) {
            nodeCount = (Integer) ((JavaNode) node.getChild(i)).jjtAccept(this, data);
            lineCount += nodeCount.intValue();
        }
        return ++lineCount;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {

        Integer lineCount = countNodeChildren(node, data);

        if (node.hasElse()) {
            lineCount++;
        }

        return lineCount;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTFinallyStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTSynchronizedStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTStatementExpression node, Object data) {

        // "For" update expressions do not count as separate lines of code
        if (node.getParent() instanceof ASTStatementExpressionList) {
            return NumericConstants.ZERO;
        }

        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTLabeledStatement node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {

        // "For" init declarations do not count as separate lines of code
        if (node.getParent() instanceof ASTForInit) {
            return NumericConstants.ZERO;
        }

        /*
         * This will count variables declared on the same line as separate NCSS
         * counts. This violates JavaNCSS standards, but I'm not convinced
         * that's a bad thing here.
         */

        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTSwitchLabel node, Object data) {
        return countNodeChildren(node, data);
    }

}
