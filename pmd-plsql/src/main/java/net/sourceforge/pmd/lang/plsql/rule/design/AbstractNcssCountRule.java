/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTElseClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTExceptionHandler;
import net.sourceforge.pmd.lang.plsql.ast.ASTExitStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTGotoStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLabelledStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTRaiseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;

/**
 * Abstract superclass for NCSS counting methods. Analogous to and cribbed from
 * the Java version of the rule.
 *
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public abstract class AbstractNcssCountRule<T extends PLSQLNode> extends AbstractCounterCheckRule<T> {


    /**
     * Count the nodes of the given type using NCSS rules.
     *
     * @param nodeClass class of node to count
     */
    AbstractNcssCountRule(Class<T> nodeClass) {
        super(nodeClass);
    }


    @Override
    protected int getMetric(T node) {
        return 1 + (Integer) node.jjtAccept(new NcssVisitor(), null);
    }

    private static class NcssVisitor extends PLSQLParserVisitorAdapter {

        @Override
        public Object visitPlsqlNode(PLSQLNode node, Object data) {
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
        protected Integer countNodeChildren(Node node, Object data) {
            int nodeCount = 0;
            for (int i = 0; i < node.getNumChildren(); i++) {
                nodeCount += (Integer) ((PLSQLNode) node.getChild(i)).jjtAccept(this, data);
            }
            return nodeCount;
        }


        @Override
        public Object visit(ASTForStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTLoopStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTIfStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTElsifClause node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTElseClause node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTExitStatement node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTExceptionHandler node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTContinueStatement node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTGotoStatement node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTCaseStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTRaiseStatement node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTExpression node, Object data) {
            // "For" update expressions do not count as separate lines of code
            return node.getParent() instanceof ASTStatement ? 0 : 1;
        }

        @Override
        public Object visit(ASTFieldDeclaration node, Object data) {
            return 1;
        }

        @Override
        public Object visit(ASTLabelledStatement node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

        @Override
        public Object visit(ASTCaseWhenClause node, Object data) {
            return countNodeChildren(node, data) + 1;
        }

    }
}
