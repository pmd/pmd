/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.rule.internal.AbstractCounterCheckRule;

/**
 * Abstract superclass for NCSS counting methods. Counts tokens according to
 * <a href="http://www.kclee.de/clemens/java/javancss/">JavaNCSS rules</a>.
 *
 * @author ported from Java original of Jason Bennett
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public abstract class AbstractNcssCountRule<T extends ApexNode<?>> extends AbstractCounterCheckRule<T> {


    /**
     * Count the nodes of the given type using NCSS rules.
     *
     * @param nodeClass class of node to count
     */
    protected AbstractNcssCountRule(Class<T> nodeClass) {
        super(nodeClass);
    }


    @Override
    protected int getMetric(T node) {
        return (Integer) new NcssVisitor().visit(node, null) + 1;
    }

    private static class NcssVisitor extends ApexParserVisitorAdapter {

        @Override
        public Object visit(ApexNode<?> node, Object data) {
            return countNodeChildren(node, data);
        }

        /**
         * Count the number of children of the given node. Adds one to count the
         * node itself.
         *
         * @param node node having children counted
         * @param data node data
         *
         * @return count of the number of children of the node, plus one
         */
        protected Integer countNodeChildren(ApexNode<?> node, Object data) {
            int nodeCount = 0;
            for (int i = 0; i < node.getNumChildren(); i++) {
                nodeCount += (Integer) node.getChild(i).jjtAccept(this, data);
            }
            return nodeCount;
        }

        @Override
        public Object visit(ASTForLoopStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTForEachStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTDoLoopStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTIfBlockStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTIfElseBlockStatement node, Object data) {
            return countNodeChildren(node, data) + 2;
        }

        @Override
        public Object visit(ASTWhileLoopStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTBreakStatement node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTContinueStatement node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTThrowStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTStatement node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTMethodCallExpression node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTMethod node, Object data) {
            return node.isSynthetic() ? 0 : countNodeChildren(node, data);
        }

        @Override
        public Object visit(ASTUserClass node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTUserEnum node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTUserInterface node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTFieldDeclaration node, Object data) {
            return 1;
        }
    }
}
