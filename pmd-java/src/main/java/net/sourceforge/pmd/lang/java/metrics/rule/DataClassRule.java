/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.rule;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.util.StringUtil;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DataClassRule extends AbstractJavaMetricsRule {

    // probably not worth using properties
    private static final int ACCESSOR_OR_FIELD_FEW_LEVEL = 3;
    private static final int ACCESSOR_OR_FIELD_MANY_LEVEL = 5;
    private static final double WOC_LEVEL = 1. / 3.;
    private static final int WMC_HIGH_LEVEL = 31;
    private static final int WMC_VERY_HIGH_LEVEL = 47;


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        boolean isDataClass = interfaceRevealsData(node) && classRevealsDataAndLacksComplexity(node);

        if (isDataClass) {
            double woc = JavaMetrics.get(JavaClassMetricKey.WOC, node);
            int nopa = (int) JavaMetrics.get(JavaClassMetricKey.NOPA, node);
            int noam = (int) JavaMetrics.get(JavaClassMetricKey.NOAM, node);
            int wmc = (int) JavaMetrics.get(JavaClassMetricKey.WMC, node);

            addViolation(data, node, new Object[] {node.getImage(),
                                                   StringUtil.percentageString(woc, 3, true),
                                                   nopa, noam, wmc});
        }

        return super.visit(node, data);
    }


    private boolean interfaceRevealsData(ASTAnyTypeDeclaration node) {
        double woc = JavaMetrics.get(JavaClassMetricKey.WOC, node);
        return woc < WOC_LEVEL;
    }


    private boolean classRevealsDataAndLacksComplexity(ASTAnyTypeDeclaration node) {
        int nopa = (int) JavaMetrics.get(JavaClassMetricKey.NOPA, node);
        int noam = (int) JavaMetrics.get(JavaClassMetricKey.NOAM, node);
        int wmc = (int) JavaMetrics.get(JavaClassMetricKey.WMC, node);

        return nopa + noam > ACCESSOR_OR_FIELD_FEW_LEVEL && wmc < WMC_HIGH_LEVEL
            || nopa + noam > ACCESSOR_OR_FIELD_MANY_LEVEL && wmc < WMC_VERY_HIGH_LEVEL;
    }

}
