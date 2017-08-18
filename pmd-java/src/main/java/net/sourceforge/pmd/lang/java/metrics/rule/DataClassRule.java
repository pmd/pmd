/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.rule;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaMetricsRule;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DataClassRule extends AbstractJavaMetricsRule {


    private static int accessorOrFieldFewLevel = 3;
    private static int accessorOrFieldManyLevel = 6;
    private static int wocLevel = 33;
    private static int wmcHighLevel = 31;
    private static int wmcVeryHighLevel = 47;


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {

        boolean isDataClass = interfaceRevealsData(node) && classRevealsDataAndLacksComplexity(node);

        if (isDataClass) {
            addViolation(data, node, new String[] {node.getImage()});
        }

        return super.visit(node, data);
    }


    private boolean interfaceRevealsData(ASTAnyTypeDeclaration node) {
        double woc = JavaMetrics.get(JavaClassMetricKey.WOC, node);

        return woc < wocLevel; // TODO parameterize
    }


    private boolean classRevealsDataAndLacksComplexity(ASTAnyTypeDeclaration node) {
        int nopa = (int) JavaMetrics.get(JavaClassMetricKey.NOPA, node);
        int noam = (int) JavaMetrics.get(JavaClassMetricKey.NOAM, node);
        int wmc = (int) JavaMetrics.get(JavaClassMetricKey.WMC, node);

        return nopa + noam > accessorOrFieldFewLevel && wmc < wmcHighLevel
            || nopa + noam > accessorOrFieldManyLevel && wmc < wmcVeryHighLevel;
    }

}
