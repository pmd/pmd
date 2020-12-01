/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics.NcssOption;
import net.sourceforge.pmd.lang.metrics.MetricOption;

/**
 * @author Clément Fournier
 */
public class NcssTestRule extends JavaIntMetricTestRule {

    public NcssTestRule() {
        super(JavaMetrics.NCSS);
    }

    @Override
    protected boolean reportOn(Node node) {
        return node instanceof ASTBodyDeclaration;
    }

    @Override
    protected Map<String, MetricOption> optionMappings() {
        Map<String, MetricOption> mappings = super.optionMappings();
        mappings.put(NcssOption.COUNT_IMPORTS.valueName(), NcssOption.COUNT_IMPORTS);
        return mappings;
    }
}
