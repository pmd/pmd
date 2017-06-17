/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.OperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;

/**
 * Non-commented source statement counter for methods.
 *
 * @author Jason Bennett
 */
public class NcssMethodCountRule extends AbstractJavaRule {

    public static final DoubleProperty MINIMUM_DESCRIPTOR = new DoubleProperty("minimum", "Minimum reporting threshold",
                                                                               0d,
                                                                               100d, null,
                                                                               2.0f);


    public NcssMethodCountRule() {
        definePropertyDescriptor(MINIMUM_DESCRIPTOR);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        int ncss = (int) Metrics.get(OperationMetricKey.NCSS, node);

        if (ncss > getProperty(MINIMUM_DESCRIPTOR)) {
            addViolation(data, node, getMessage());
        }
        return data;
    }


    /**
     * Count the size of all non-constructor methods.
     */
 /*   public NcssMethodCountRule() {
        super(ASTMethodDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return super.visit(node, data);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new String[] { ((ASTMethodDeclaration) point.getNode()).getMethodName(),
            String.valueOf((int) point.getScore()), };
    } */
}
