/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaParserVisitorDecorator;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.rule.codesize.NPathComplexityRule;

/**
 * Decorator which counts the complexity of boolean expressions for Cyclo.
 *
 * @author Clément Fournier
 * @see net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric
 */
public class CycloPathAwareDecorator extends AbstractJavaParserVisitorDecorator {

    public CycloPathAwareDecorator(JavaParserVisitor javaParserVisitor) {
        super(javaParserVisitor);
    }


    @Override
    public Object visit(ASTIfStatement node, Object data) {
        super.visit(node, data);

        int boolCompIf = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        ((MutableInt) data).add(boolCompIf);
        return data;
    }


    @Override
    public Object visit(ASTForStatement node, Object data) {
        super.visit(node, data);

        int boolCompFor = CycloMetric.booleanExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));
        ((MutableInt) data).add(boolCompFor);
        return data;
    }


    @Override
    public Object visit(ASTDoStatement node, Object data) {
        super.visit(node, data);

        int boolCompDo = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        ((MutableInt) data).add(boolCompDo);
        return data;
    }


    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        super.visit((JavaNode) node, data); // skip the superclass' treatment

        int boolCompSwitch = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        ((MutableInt) data).add(boolCompSwitch);
        return data;
    }


    @Override
    public Object visit(ASTSwitchLabel node, Object data) {
        if (!node.isDefault()) {
            ((MutableInt) data).increment();
        }
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        super.visit(node, data);

        int boolCompWhile = CycloMetric.booleanExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        ((MutableInt) data).add(boolCompWhile);
        return data;
    }


    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        super.visit(node, data);

        if (node.isTernary()) {
            int boolCompTern = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
            ((MutableInt) data).add(1 + boolCompTern);
        }
        return data;
    }

}
