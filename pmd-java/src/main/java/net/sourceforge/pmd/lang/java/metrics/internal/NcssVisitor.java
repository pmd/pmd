/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
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
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.NcssOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;


/**
 * Visitor for the Ncss metric.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public class NcssVisitor extends JavaVisitorBase<MutableInt, Void> {

    protected final boolean countImports;


    @SuppressWarnings("PMD.UnusedFormalParameter")
    public NcssVisitor(MetricOptions options, JavaNode topNode) {
        countImports = options.contains(NcssOption.COUNT_IMPORTS);
        // topNode is unused, but we'll need it if we want to discount lambdas
        // if we add it later, we break binary compatibility
    }


    @Override
    public final Void visitJavaNode(JavaNode node, MutableInt data) {
        // same here
        return super.visitJavaNode(node, data);
    }


    @Override
    public Void visit(ASTClassOrInterfaceDeclaration node, MutableInt data) {
        if (countImports) {
            ASTCompilationUnit acu = node.getFirstParentOfType(ASTCompilationUnit.class);
            List<ASTImportDeclaration> imports = acu.findChildrenOfType(ASTImportDeclaration.class);

            int increment = imports.size();
            if (!acu.findChildrenOfType(ASTPackageDeclaration.class).isEmpty()) {
                increment++;
            }
            data.add(increment);
        }
        data.increment();

        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTEnumDeclaration node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTAnnotationTypeDeclaration node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTFieldDeclaration node, MutableInt data) {
        data.increment();
        // May use a lambda
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTMethodDeclaration node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTConstructorDeclaration node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTLocalVariableDeclaration node, MutableInt data) {

        // doesn't count variable declared inside a for initializer
        if (!(node.getParent() instanceof ASTForInit)) {
            data.increment();
        }
        // May declare a lambda
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTIfStatement node, MutableInt data) {
        data.increment();
        if (node.hasElse()) {
            data.increment();
        }

        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTWhileStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTSwitchStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTExpressionStatement node, MutableInt data) {
        if (!(node.getParent().getParent() instanceof ASTForUpdate)) {
            data.increment();
        }
        return null;
    }


    @Override
    public Void visit(ASTExplicitConstructorInvocation node, MutableInt data) {
        data.increment();
        return null;
    }


    @Override
    public Void visit(ASTContinueStatement node, MutableInt data) {
        data.increment();
        return null;
    }


    @Override
    public Void visit(ASTBreakStatement node, MutableInt data) {
        data.increment();
        return null;
    }


    @Override
    public Void visit(ASTReturnStatement node, MutableInt data) {
        data.increment();
        return null;
    }


    @Override
    public Void visit(ASTDoStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTForStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTSynchronizedStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTCatchClause node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTThrowStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTFinallyClause node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTLabeledStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTSwitchLabel node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTInitializer node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTAssertStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


}
