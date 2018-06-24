/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl.visitors;

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
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.impl.CycloMetric;

/**
 * @author Clément Fournier
 */
public class StandardCycloVisitor extends ApexParserVisitorAdapter {

    @Override
    public Object visit(ASTMethod node, Object data) {
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        ((MutableInt) data).add(
            1 + CycloMetric.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTCatchBlockStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        ((MutableInt) data).add(
            1 + CycloMetric.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        ((MutableInt) data).add(
            1 + CycloMetric.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        ((MutableInt) data).add(
            1 + CycloMetric.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTTernaryExpression node, Object data) {
        ((MutableInt) data).add(
            1 + CycloMetric.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTStandardCondition.class)));
        super.visit(node, data);
        return data;
    }


}
