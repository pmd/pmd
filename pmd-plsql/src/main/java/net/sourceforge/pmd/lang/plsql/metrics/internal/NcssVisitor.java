/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

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
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.ast.PlsqlVisitorBase;

public class NcssVisitor extends PlsqlVisitorBase<MutableInt, Void> {
    @Override
    public Void visit(ASTForStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTLoopStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTIfStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTElsifClause node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTElseClause node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTWhileStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTExitStatement node, MutableInt data) {
        data.increment();
        return null;
    }

    @Override
    public Void visit(ASTExceptionHandler node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTContinueStatement node, MutableInt data) {
        data.increment();
        return null;
    }

    @Override
    public Void visit(ASTGotoStatement node, MutableInt data) {
        data.increment();
        return null;
    }

    @Override
    public Void visit(ASTReturnStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTCaseStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTRaiseStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTExpression node, MutableInt data) {
        data.increment();
        return null;
    }

    @Override
    public Void visit(ASTFieldDeclaration node, MutableInt data) {
        data.increment();
        return null;
    }

    @Override
    public Void visit(ASTLabelledStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTCaseWhenClause node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }
}
