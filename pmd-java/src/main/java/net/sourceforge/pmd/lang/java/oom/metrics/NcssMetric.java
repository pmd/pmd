/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.oom.AbstractClassMetric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetric;

/**
 * Non Commenting Source Statements. Similar to LOC but only counts statements, which is roughly equivalent
 * to counting the number of semicolons and opening braces in the program. The precise rules for counting
 * statements comply with <a href="http://www.kclee.de/clemens/java/javancss/">JavaNCSS rules</a>.
 *
 * @author Clément Fournier
 * @see LocMetric
 * @since June 2017
 */
public class NcssMetric extends AbstractClassMetric implements OperationMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, MetricVersion version) {
        return ((MutableInt) node.jjtAccept(new NcssVisitor(), new MutableInt(1))).getValue();
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {
        return ((MutableInt) node.jjtAccept(new NcssVisitor(), new MutableInt(1))).getValue();
    }

    /**
     * Counts source code statements.
     *
     * @author Clément Fournier
     */
    static class NcssVisitor extends JavaParserVisitorAdapter {

        @Override
        public Object visit(ASTImportDeclaration node, Object data) {
            ((MutableInt) data).increment();
            return data;
        }

        @Override
        public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTFieldDeclaration node, Object data) {
            ((MutableInt) data).increment();
            return data;
        }

        @Override
        public Object visit(ASTMethodDeclaration node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTConstructorDeclaration node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTLocalVariableDeclaration node, Object data) {

            // doesn't count variable declared inside a for initializer
            if (!(node.jjtGetParent() instanceof ASTForInit)) {
                ((MutableInt) data).increment();
            }
            return data;
        }

        @Override
        public Object visit(ASTIfStatement node, Object data) {
            ((MutableInt) data).increment();
            if (!node.hasDescendantOfType(ASTIfStatement.class) && node.hasElse()) {
                ((MutableInt) data).increment();
            }

            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTSwitchStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTStatementExpression node, Object data) {
            ((MutableInt) data).increment();
            return data;
        }

        @Override
        public Object visit(ASTExplicitConstructorInvocation node, Object data) {
            ((MutableInt) data).increment();
            return data;
        }

        @Override
        public Object visit(ASTContinueStatement node, Object data) {
            ((MutableInt) data).increment();
            return data;
        }

        @Override
        public Object visit(ASTBreakStatement node, Object data) {
            ((MutableInt) data).increment();
            return data;
        }

        @Override
        public Object visit(ASTReturnStatement node, Object data) {
            ((MutableInt) data).increment();
            return data;
        }

        @Override
        public Object visit(ASTDoStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTForStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTSynchronizedStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTCatchStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTThrowStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTFinallyStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTLabeledStatement node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTSwitchLabel node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTInitializer node, Object data) {
            ((MutableInt) data).increment();
            return super.visit(node, data);
        }
    }
}
