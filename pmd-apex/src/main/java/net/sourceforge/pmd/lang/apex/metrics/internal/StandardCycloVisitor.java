/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.apex.ast.ASTCatchBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTStandardCondition;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexVisitorBase;

/**
 * @author Clément Fournier
 */
public class StandardCycloVisitor extends ApexVisitorBase<MutableInt, Void> {

    @Override
    public Void visit(ASTMethod node, MutableInt data) {
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTIfBlockStatement node, MutableInt data) {
        data.add(1 + ApexMetricsHelper.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTCatchBlockStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTForLoopStatement node, MutableInt data) {
        data.add(
                1 + ApexMetricsHelper.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTForEachStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }

    @Override
    public Void visit(ASTThrowStatement node, MutableInt data) {
        data.increment();
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTWhileLoopStatement node, MutableInt data) {
        data.add(
                1 + ApexMetricsHelper.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTDoLoopStatement node, MutableInt data) {
        data.add(
                1 + ApexMetricsHelper.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        return super.visit(node, data);
    }


    @Override
    public Void visit(ASTTernaryExpression node, MutableInt data) {
        data.add(
                1 + ApexMetricsHelper.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        return super.visit(node, data);
    }


}
