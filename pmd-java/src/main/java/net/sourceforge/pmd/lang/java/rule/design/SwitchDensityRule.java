/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Switch Density - This is the number of statements over the number of
 * cases within a switch. The higher the value, the more work each case
 * is doing.
 *
 * <p>Its my theory, that when the Switch Density is high, you should start
 * looking at Subclasses or State Pattern to alleviate the problem.</p>
 *
 * @author David Dixon-Peugh
 * @author Clément Fournier
 */
public class SwitchDensityRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Double> REPORT_LEVEL =
        // can't use CommonPropertyDescriptors because we need a double property
        PropertyFactory.doubleProperty("minimum")
                       .desc("Threshold above which a node is reported")
                       .require(positive())
                       .defaultValue(10d)
                       .build();

    public SwitchDensityRule() {
        super(ASTSwitchStatement.class, ASTSwitchExpression.class);
        definePropertyDescriptor(REPORT_LEVEL);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        return visitSwitchLike(node, data);
    }

    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        return visitSwitchLike(node, data);
    }

    public Void visitSwitchLike(ASTSwitchLike node, Object data) {
        // note: this does not cross find boundaries.
        int stmtCount = node.descendants(ASTStatement.class).count();
        int labelCount = node.getBranches()
                .map(ASTSwitchBranch::getLabel)
                .sumBy(label -> label.isDefault() ? 1 : label.getExprList().count());

        // note: if labelCount is zero, double division will produce +Infinity or NaN, not ArithmeticException
        double density = stmtCount / (double) labelCount;
        if (density >= getProperty(REPORT_LEVEL)) {
            addViolation(data, node);
        }
        return null;
    }
}
