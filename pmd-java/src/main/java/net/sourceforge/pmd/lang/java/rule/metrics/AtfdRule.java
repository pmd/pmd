/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.metrics;

import net.sourceforge.pmd.annotations.UsesMetrics;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.Metrics.ClassMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
@UsesMetrics
public class AtfdRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        double atfd = Metrics.get(ClassMetricKey.ATFD, node);
        if (atfd > .3) {
            addViolation(data, node);
        }
        return data;
    }
}
