/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;

/**
 * Visitor for the default n-path complexity version. It takes a root {@link NpathDataNode} as data.
 *
 * @author Clément Fournier
 */
public class DefaultNpathVisitor extends JavaParserVisitorAdapter {

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        int boolComplexity = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        NpathDataNode n = ((NpathDataNode) data).addAndGetChild(boolComplexity);
        super.visit(node, n);
        return n.parent;
    }


    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        int boolComplexity = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        NpathDataNode n = ((NpathDataNode) data).addAndGetChild(boolComplexity);
        super.visit(node, n);
        return n.parent;
    }


    @Override
    public Object visit(ASTForStatement node, Object data) {
        int boolComplexity = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        NpathDataNode n = ((NpathDataNode) data).addAndGetChild(boolComplexity);
        super.visit(node, n);
        return n.parent;
    }


    @Override
    public Object visit(ASTDoStatement node, Object data) {
        int boolComplexity = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        NpathDataNode n = ((NpathDataNode) data).addAndGetChild(boolComplexity);
        super.visit(node, n);
        return n.parent;
    }


    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        int boolComplexity = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        NpathDataNode n = ((NpathDataNode) data).addAndGetChild(boolComplexity);
        super.visit(node, n);
        return n.parent;
    }


    @Override
    public Object visit(ASTTryStatement node, Object data) {
        int boolComplexity = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        NpathDataNode n = ((NpathDataNode) data).addAndGetChild(boolComplexity);
        super.visit(node, n);
        return n.parent;
    }


    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        int boolComplexity = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        NpathDataNode n = ((NpathDataNode) data).addAndGetChild(boolComplexity);
        super.visit(node, n);
        return n.parent;
    }


    /**
     * Heap structure used as the data of the visitor. Every node corresponds to a control flow statement. A control
     * flow statement nested inside another is represented by a node that's a children of the other.
     *
     * <p>The complexity of a node is given by multiplying the complexities of its children plus 1. The complexity of a
     * leaf is the boolean complexity of the guard condition of the statement. The root node bears the complexity of the
     * method.
     */
    public static class NpathDataNode {

        private List<NpathDataNode> children = new ArrayList<>();
        private NpathDataNode parent;

        private int booleanComplexity;


        /** Creates a root node. */
        public NpathDataNode() {

        }


        private NpathDataNode(int booleanComplexity, NpathDataNode parent) {
            this.booleanComplexity = booleanComplexity;
            this.parent = parent;
        }


        NpathDataNode addAndGetChild(int booleanComplexity) {
            NpathDataNode newChild = new NpathDataNode(booleanComplexity, this);
            children.add(newChild);
            return newChild;
        }


        /** Gets the complexity of this node. */
        public int getComplexity() {
            int complexity = 1 + booleanComplexity;
            for (NpathDataNode child : children) {
                complexity *= child.getComplexity();
            }

            return complexity;
        }
    }
}
