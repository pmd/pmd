/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.plsql.ast.ASTElseClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTExceptionDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.PlsqlVisitorBase;

public class NcssVisitor extends PlsqlVisitorBase<MutableInt, Void> {
    @Override
    public Void visit(ASTPackageSpecification node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTProgramUnit node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTVariableOrConstantDeclaration node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTExceptionDeclaration node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTPackageBody node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTStatement node, MutableInt data) {
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
        // ElseClause also appears in a CaseStatement, but we only want to count else clauses in if statements
        if (node.getParent() instanceof ASTIfStatement) {
            data.increment();
        }
        return super.visit(node, data);
    }
}
