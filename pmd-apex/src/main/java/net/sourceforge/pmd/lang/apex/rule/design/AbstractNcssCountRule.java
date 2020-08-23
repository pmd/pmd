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
import net.sourceforge.pmd.lang.apex.ast.ApexVisitorBase;
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
        return node.acceptVisitor(NcssVisitor.INSTANCE, null) + 1;
    }

    private static class NcssVisitor extends ApexVisitorBase<Void, Integer> {
        // todo this would be better with a <MutableInt, Void> signature

        static final NcssVisitor INSTANCE = new NcssVisitor();

        @Override
        public Integer visitApexNode(ApexNode<?> node, Void data) {
            return countNodeChildren(node, data);
        }

        @Override
        protected Integer zero() {
            return 0;
        }

        @Override
        protected Integer combine(Integer acc, Integer childValue) {
            return acc + childValue;
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
        protected Integer countNodeChildren(ApexNode<?> node, Void data) {
            return visitChildren(node, data);
        }

        @Override
        public Integer visit(ASTForLoopStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTForEachStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTDoLoopStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTIfBlockStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTIfElseBlockStatement node, Void data) {
            return countNodeChildren(node, data) + 2;
        }

        @Override
        public Integer visit(ASTWhileLoopStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTBreakStatement node, Void data) {
            return 1;
        }

        @Override
        public Integer visit(ASTTryCatchFinallyBlockStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTContinueStatement node, Void data) {
            return 1;
        }

        @Override
        public Integer visit(ASTReturnStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTThrowStatement node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTStatement node, Void data) {
            return 1;
        }

        @Override
        public Integer visit(ASTMethodCallExpression node, Void data) {
            return 1;
        }

        @Override
        public Integer visit(ASTMethod node, Void data) {
            return node.isSynthetic() ? 0 : countNodeChildren(node, data);
        }

        @Override
        public Integer visit(ASTUserClass node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTUserEnum node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTUserInterface node, Void data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Integer visit(ASTFieldDeclaration node, Void data) {
            return 1;
        }
    }
}
