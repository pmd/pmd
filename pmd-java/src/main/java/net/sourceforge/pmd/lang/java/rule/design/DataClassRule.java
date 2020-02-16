/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey.NOAM;
import static net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey.NOPA;
import static net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey.WMC;
import static net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey.WOC;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
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

        if (!MetricsUtil.supportsAll(node, NOAM, NOPA, WMC, WOC)) {
            return super.visit(node, data);
        }

        boolean isDataClass = interfaceRevealsData(node) && classRevealsDataAndLacksComplexity(node);

        if (isDataClass) {
            double woc = MetricsUtil.computeMetric(WOC, node);
            int nopa = (int) MetricsUtil.computeMetric(NOPA, node);
            int noam = (int) MetricsUtil.computeMetric(NOAM, node);
            int wmc = (int) MetricsUtil.computeMetric(WMC, node);

            addViolation(data, node, new Object[] {node.getSimpleName(),
                                                   StringUtil.percentageString(woc, 3),
                                                   nopa, noam, wmc, });
        }

        return super.visit(node, data);
    }


    private boolean interfaceRevealsData(ASTAnyTypeDeclaration node) {
        double woc = MetricsUtil.computeMetric(WOC, node);
        return woc < WOC_LEVEL;
    }

    private boolean classRevealsDataAndLacksComplexity(ASTAnyTypeDeclaration node) {

        int nopa = (int) MetricsUtil.computeMetric(NOPA, node);
        int noam = (int) MetricsUtil.computeMetric(NOAM, node);
        int wmc = (int) MetricsUtil.computeMetric(WMC, node);

        return nopa + noam > ACCESSOR_OR_FIELD_FEW_LEVEL && wmc < WMC_HIGH_LEVEL
            || nopa + noam > ACCESSOR_OR_FIELD_MANY_LEVEL && wmc < WMC_VERY_HIGH_LEVEL;
    }

}
