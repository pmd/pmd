/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
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
        super(ASTSwitchStatement.class);
        definePropertyDescriptor(REPORT_LEVEL);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        double density = new SwitchDensityVisitor().compute(node);
        if (density >= getProperty(REPORT_LEVEL)) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    private static class SwitchDensityVisitor extends JavaParserVisitorAdapter {

        private int labels = 0;
        private int stmts = 0;
        private ASTSwitchStatement root;


        double compute(ASTSwitchStatement root) {
            this.root = root;
            root.jjtAccept(this, null);
            return labels == 0 ? 0 : ((double) stmts) / labels;
        }


        @Override
        public Object visitStatement(ASTStatement statement, Object data) {
            stmts++;
            return super.visitStatement(statement, data);
        }

        @Override
        public Object visit(ASTExpression node, Object data) {
            // don't recurse on anonymous class, etc
            return data;
        }

        @Override
        public Object visit(ASTSwitchLabel switchLabel, Object data) {
            if (switchLabel.getParent() == root) {
                labels++;
            }
            return super.visit(switchLabel, data);
        }
    }
}
