/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.internal;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;


/**
 * Visitor for the Ncss metric.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public class NcssVisitor extends JavaParserVisitorAdapter {

    protected final boolean countImports;


    @SuppressWarnings("PMD.UnusedFormalParameter")
    public NcssVisitor(MetricOptions options, JavaNode topNode) {
        countImports = options.contains(NcssOption.COUNT_IMPORTS);
        // topNode is unused, but we'll need it if we want to discount lambdas
        // if we add it later, we break binary compatibility
    }


    @Override
    public final Object visit(JavaNode node, Object data) {
        // same here
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (countImports) {
            ASTCompilationUnit acu = node.getFirstParentOfType(ASTCompilationUnit.class);
            List<ASTImportDeclaration> imports = acu.findChildrenOfType(ASTImportDeclaration.class);

            int increment = imports.size();
            if (!acu.findChildrenOfType(ASTPackageDeclaration.class).isEmpty()) {
                increment++;
            }
            ((MutableInt) data).add(increment);
        }
        ((MutableInt) data).increment();

        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        ((MutableInt) data).increment();
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        ((MutableInt) data).increment();
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        ((MutableInt) data).increment();
        // May use a lambda
        return super.visit(node, data);
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
        if (!(node.getParent() instanceof ASTForInit)) {
            ((MutableInt) data).increment();
        }
        // May declare a lambda
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTIfStatement node, Object data) {
        ((MutableInt) data).increment();
        if (node.hasElse()) {
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
        if (!(node.getParent().getParent() instanceof ASTForUpdate)) {
            ((MutableInt) data).increment();
        }
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


    @Override
    public Object visit(ASTAssertStatement node, Object data) {
        ((MutableInt) data).increment();
        return super.visit(node, data);
    }


}
