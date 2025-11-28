/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.apex.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTCatchBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTElseWhenBlock;
import net.sourceforge.pmd.lang.apex.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTypeWhenBlock;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTValueWhenBlock;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexVisitorBase;

/**
 * @since 7.19.0
 */
public class NcssVisitor extends ApexVisitorBase<MutableInt, Void> {

    @Override
    public Void visit(ASTForLoopStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTForEachStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTDoLoopStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTIfBlockStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTIfElseBlockStatement node, MutableInt data) {
        if (node.hasElseStatement()) {
            data.increment();
        }
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTWhileLoopStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTBreakStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTTryCatchFinallyBlockStatement node, MutableInt data) {
        if (node.getFinallyBlock() != null) {
            data.increment();
        }
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTCatchBlockStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTContinueStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTReturnStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTThrowStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTSwitchStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTValueWhenBlock node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTElseWhenBlock node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTTypeWhenBlock node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTExpressionStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTMethod node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visitTypeDecl(ASTUserClassOrInterface<?> node, MutableInt data) {
        data.increment();
        return super.visitTypeDecl(node, data);
    }

    @Override
    public Void visit(ASTVariableDeclarationStatements node, MutableInt data) {
        // doesn't count variable declared inside a for initializer
        // or in switch type when block
        if (!(node.getParent() instanceof ASTForLoopStatement
              || node.getParent() instanceof ASTTypeWhenBlock)) {
            data.increment();
        }

        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTFieldDeclarationStatements node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }
}
