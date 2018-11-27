/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorDecorator;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;


/**
 * Decorator which counts the complexity of boolean expressions for Cyclo.
 *
 * @deprecated Visitor decorators are deprecated because they lead to fragile code.
 *
 * @author Clément Fournier
 * @see net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric
 */
@Deprecated
public class CycloPathAwareDecorator extends JavaParserVisitorDecorator {


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

        if (node.isForeach()) {
            return data;
        }

        int boolCompFor = CycloMetric.booleanExpressionComplexity(node.getGuardExpressionNode());
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

        int boolCompTern = CycloMetric.booleanExpressionComplexity(node.getGuardExpressionNode());
        ((MutableInt) data).add(boolCompTern);

        return data;
    }


    @Override
    public Object visit(ASTAssertStatement node, Object data) {
        int base = ((MutableInt) data).getValue();
        // This is precisely the problem with decorators
        // The control flow is completely obscured and information is spread out, for no real benefit
        super.visit(node, data);
        boolean isAssertAware = base < ((MutableInt) data).getValue();

        if (isAssertAware) {
            int boolCompAssert = CycloMetric.booleanExpressionComplexity(node.getGuardExpressionNode());
            ((MutableInt) data).add(boolCompAssert);
        }
        return data;
    }

}
