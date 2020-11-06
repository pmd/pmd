/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;


import static net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey.ATFD;
import static net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey.TCC;
import static net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey.WMC;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.util.StringUtil;


/**
 * The God Class Rule detects the God Class design flaw using metrics. A god class does too many things, is very big and
 * complex. It should be split apart to be more object-oriented. The rule uses the detection strategy described in [1].
 * The violations are reported against the entire class.
 *
 * <p>[1] Lanza. Object-Oriented Metrics in Practice. Page 80.
 *
 * @since 5.0
 */
public class GodClassRule extends AbstractJavaRule {

    /**
     * Very high threshold for WMC (Weighted Method Count). See: Lanza. Object-Oriented Metrics in Practice. Page 16.
     */
    private static final int WMC_VERY_HIGH = 47;

    /**
     * Few means between 2 and 5. See: Lanza. Object-Oriented Metrics in Practice. Page 18.
     */
    private static final int FEW_ATFD_THRESHOLD = 5;

    /**
     * One third is a low value. See: Lanza. Object-Oriented Metrics in Practice. Page 17.
     */
    private static final double TCC_THRESHOLD = 1.0 / 3.0;


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!MetricsUtil.supportsAll(node, WMC, TCC, ATFD)) {
            return super.visit(node, data);
        }

        int wmc = (int) MetricsUtil.computeMetric(WMC, node);
        double tcc = MetricsUtil.computeMetric(TCC, node);
        int atfd = (int) MetricsUtil.computeMetric(ATFD, node);

        super.visit(node, data);

        if (wmc >= WMC_VERY_HIGH && atfd > FEW_ATFD_THRESHOLD && tcc < TCC_THRESHOLD) {

            addViolation(data, node, new Object[] {wmc,
                                                   StringUtil.percentageString(tcc, 3),
                                                   atfd, });
        }
        return data;
    }

}
